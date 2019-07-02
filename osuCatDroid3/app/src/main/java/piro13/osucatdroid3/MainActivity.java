package piro13.osucatdroid3;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

import piro13.osucatdroid3.accounts.Account;
import piro13.osucatdroid3.accounts.AccountViewModel;
import piro13.osucatdroid3.accounts.Accounts_Fragment;
import piro13.osucatdroid3.accounts.ConfigureProfileActivity;
import piro13.osucatdroid3.accounts.UserSession;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public final static int REQUEST_IMAGE_CAPTURE = 13;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private DrawerLayout drawer;

    private TextView infonav;
    private ImageView navAvatar;
    private AccountViewModel accountViewModel;
    private Account currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Profile_Fragment()).commit();
            navigationView.setCheckedItem(R.id.nav_profile);
        }

        UserSession.init(getApplicationContext());
        UserSession.getInstance();
        View headerView = navigationView.getHeaderView(0);
        infonav = (TextView) headerView.findViewById(R.id.infoNav);
        navAvatar = headerView.findViewById(R.id.imageViewNav);
        accountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
        String name = UserSession.getCurrentlyLogged();
        if (name != null) {
            if (!name.equals("")) {
                String text = getString(R.string.logged_as) + " " + name;
                infonav.setText(text);
                new CheckByName().execute();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_collections:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Collections_Fragment()).commit();
                break;
            case R.id.nav_accounts:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Accounts_Fragment()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Profile_Fragment()).commit();
                break;
            case R.id.nav_logout:
                UserSession.logoutUser();
                String text = getString(R.string.github_com_piro13);
                infonav.setText(text);
                NavigationView navigationView = findViewById(R.id.nav_view);
                navigationView.setCheckedItem(R.id.nav_accounts);
                Picasso.get().load(R.drawable.account_placeholder).into(navAvatar);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Accounts_Fragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.photo_for_profile) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else if (id == R.id.redownload_avatar) {
            if (currentAccount == null) {
                try {
                    new CheckByName().execute().get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            String imageUrl = "http://a.ppy.sh/" + currentAccount.getIdPpy();
            redownloadAvatar(imageUrl);
        } else if (id == R.id.update_information) {
            updateInfo();
        }

        return super.onOptionsItemSelected(item);
    }

    private class CheckByName extends AsyncTask<String, Void, Account> {
        @Override
        protected Account doInBackground(String... name) {
            currentAccount = accountViewModel.getOneAccountByName(UserSession.getCurrentlyLogged());
            if (currentAccount == null) {
                Log.d(MainActivity.class.getSimpleName(), "Error: there is a problem with this account");
            }
            return currentAccount;
        }

        @Override
        protected void onPostExecute(Account account) {
            super.onPostExecute(account);
            String path = Environment.getExternalStorageDirectory().getPath() + "/osuCatDroid/avatars/" + account.getIdPpy() + ".jpeg";
            File file = new File(path);
            if (file.exists()) {
                Picasso.get().load(file).into(navAvatar);
            } else {
                //it works
                Picasso.get().load(R.drawable.account_placeholder).placeholder(R.drawable.account_placeholder).into(navAvatar);
            }
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            verifyStoragePermissions(this);
            if (currentAccount == null) {
                try {
                    new CheckByName().execute().get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (isExternalStorageWritable()) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                try {
                    String root = Environment.getExternalStorageDirectory().getPath();
                    File myDir = new File(root + "/osuCatDroid/avatars");

                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }

                    String path = "/" + currentAccount.getIdPpy() + ".jpeg";
                    File file = new File(
                            myDir + path);
                    try {
                        if (file.exists()) {
                            file.delete();
                        }
                        File fileNew = new File(
                                myDir + path);
                        fileNew.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(fileNew);
                        Bitmap output = ThumbnailUtils.extractThumbnail(bitmap, 200, 200);
                        output.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                        ostream.flush();
                        ostream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.d("Picasso", "onBitmapLoaded: error");
                }
                String path = Environment.getExternalStorageDirectory().getPath() + "/osuCatDroid/avatars/" + currentAccount.getIdPpy() + ".jpeg";
                File file = new File(path);
                if (file.exists()) {
                    Picasso.get().invalidate(file);
                    Picasso.get().load(file).into(navAvatar);
                    ImageView avatar = findViewById(R.id.profile_image);
                    Picasso.get().load(file).into(avatar);
                } else {
                    //it works
                    Picasso.get().load(R.drawable.account_placeholder).placeholder(R.drawable.account_placeholder).into(navAvatar);
                }
            }
        }
    }

    public void redownloadAvatar(String imageUrl) {
        if (isExternalStorageWritable()) {
            Picasso.get().load(imageUrl)
                    .into(new Target() {
                              @Override
                              public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                  try {
                                      String root = Environment.getExternalStorageDirectory().getPath();
                                      File myDir = new File(root + "/osuCatDroid/avatars");

                                      if (!myDir.exists()) {
                                          myDir.mkdirs();
                                      }

                                      String path = "/" + currentAccount.getIdPpy() + ".jpeg";
                                      File file = new File(
                                              myDir + path);
                                      try {
                                          if (file.exists()) {
                                              file.delete();
                                          }
                                          file.createNewFile();
                                          FileOutputStream ostream = new FileOutputStream(file);
                                          bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                          ostream.close();
                                      } catch (Exception e) {
                                          e.printStackTrace();
                                      }
                                  } catch (Exception e) {
                                      Log.d("Picasso", "onBitmapLoaded: error");
                                  }
                                  String path = Environment.getExternalStorageDirectory().getPath() + "/osuCatDroid/avatars/" + currentAccount.getIdPpy() + ".jpeg";
                                  File file = new File(path);
                                  if (file.exists()) {
                                      Picasso.get().invalidate(file);
                                      Picasso.get().load(file).into(navAvatar);
                                      ImageView avatar = findViewById(R.id.profile_image);
                                      Picasso.get().load(file).into(avatar);
                                  } else {
                                      //it works
                                      Picasso.get().load(R.drawable.account_placeholder).placeholder(R.drawable.account_placeholder).into(navAvatar);
                                  }
                              }

                              @Override
                              public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                              }

                              @Override
                              public void onPrepareLoad(Drawable placeHolderDrawable) {

                              }
                          }
                    );
        }
    }

    private void updateInfo() {
        if (currentAccount == null) {
            try {
                new CheckByName().execute().get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            new updateInformation().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        Profile_Fragment.getInstance().setProfileData(currentAccount);
    }

    private class updateInformation extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... givenAPI) {
            String TAG = ConfigureProfileActivity.class.getSimpleName();
            HttpHandler sh = new HttpHandler();
            String url = "https://osu.ppy.sh/api/get_user" + "?k=" + currentAccount.getApi() + "&u=" + currentAccount.getIdPpy() + "&m=" + 0;
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                JsonArray jsonArray = new JsonParser().parse(jsonStr).getAsJsonArray();
                JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                currentAccount.setIdPpy(jsonObject.get("user_id").getAsInt());
                currentAccount.getProfile().setAccuracy(jsonObject.get("accuracy").getAsDouble());
                currentAccount.getProfile().setCount50(jsonObject.get("count50").getAsInt());
                currentAccount.getProfile().setCount100(jsonObject.get("count100").getAsInt());
                currentAccount.getProfile().setCount300(jsonObject.get("count300").getAsInt());
                currentAccount.getProfile().setCount_rank_a(jsonObject.get("count_rank_a").getAsInt());
                currentAccount.getProfile().setCount_rank_s(jsonObject.get("count_rank_s").getAsInt());
                currentAccount.getProfile().setCount_rank_sh(jsonObject.get("count_rank_sh").getAsInt());
                currentAccount.getProfile().setCount_rank_ss(jsonObject.get("count_rank_ss").getAsInt());
                currentAccount.getProfile().setCount_rank_ssh(jsonObject.get("count_rank_ssh").getAsInt());
                currentAccount.getProfile().setCountry(jsonObject.get("country").getAsString());
                currentAccount.getProfile().setJoin_date(jsonObject.get("join_date").getAsString());
                currentAccount.getProfile().setLevel(jsonObject.get("level").getAsDouble());
                currentAccount.getProfile().setPlaycount(jsonObject.get("playcount").getAsInt());
                currentAccount.getProfile().setPp_rank(jsonObject.get("pp_rank").getAsInt());
                currentAccount.getProfile().setPp_raw(jsonObject.get("pp_raw").getAsDouble());
                currentAccount.getProfile().setRanked_score(jsonObject.get("ranked_score").getAsLong());
                currentAccount.getProfile().setTotal_score(jsonObject.get("total_score").getAsLong());
                currentAccount.getProfile().setTotal_seconds_played(jsonObject.get("total_seconds_played").getAsInt());
                currentAccount.getProfile().setUser_id(jsonObject.get("user_id").getAsInt());
                currentAccount.getProfile().setUsername(jsonObject.get("username").getAsString());

                accountViewModel.update(currentAccount);
                return null;
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return null;
        }
    }

}
