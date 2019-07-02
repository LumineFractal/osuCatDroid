package piro13.osucatdroid3.accounts;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

import piro13.osucatdroid3.HttpHandler;
import piro13.osucatdroid3.R;

public class ConfigureProfileActivity extends AppCompatActivity {
    public static final String EXTRA_ACCOUNT = "com.example.kaveri.android10.EXTRA_ACCOUNT";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private AccountViewModel accountViewModel;
    private Account account;
    private Button buttonProfileConfirm;
    private EditText editProfileName;
    private ImageView avatar;
    private long result = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.profile_onfiguration);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ACCOUNT)) {
            accountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
            try {
                new getNameTask().execute(intent).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        buttonProfileConfirm = (Button) findViewById(R.id.profile_config_button);
        editProfileName = (EditText) findViewById(R.id.profile_config_edit);

        verifyStoragePermissions(this);

        buttonProfileConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    new CheckUsernamePpy().execute().get();
                    String imageUrl = "http://a.ppy.sh/" + account.getIdPpy();
                    loadImage(imageUrl);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class getNameTask extends AsyncTask<Object, Void, Void> {
        Intent intent;

        protected Void doInBackground(Object... params) {
            intent = (Intent) params[0];
            account = accountViewModel.getAccountName(intent.getIntExtra(EXTRA_ACCOUNT, 0));
            return null;
        }
    }

    private class CheckUsernamePpy extends AsyncTask<String, Void, Long> {
        @Override
        protected Long doInBackground(String... givenAPI) {
            String TAG = ConfigureProfileActivity.class.getSimpleName();
            HttpHandler sh = new HttpHandler();
            String url = "https://osu.ppy.sh/api/get_user" + "?k=" + account.getApi() + "&u=" + editProfileName.getText().toString() + "&m=" + 0;
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                JsonArray jsonArray = new JsonParser().parse(jsonStr).getAsJsonArray();
                JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                account.setIdPpy(jsonObject.get("user_id").getAsInt());
                account.getProfile().setAccuracy(jsonObject.get("accuracy").getAsDouble());
                account.getProfile().setCount50(jsonObject.get("count50").getAsInt());
                account.getProfile().setCount100(jsonObject.get("count100").getAsInt());
                account.getProfile().setCount300(jsonObject.get("count300").getAsInt());
                account.getProfile().setCount_rank_a(jsonObject.get("count_rank_a").getAsInt());
                account.getProfile().setCount_rank_s(jsonObject.get("count_rank_s").getAsInt());
                account.getProfile().setCount_rank_sh(jsonObject.get("count_rank_sh").getAsInt());
                account.getProfile().setCount_rank_ss(jsonObject.get("count_rank_ss").getAsInt());
                account.getProfile().setCount_rank_ssh(jsonObject.get("count_rank_ssh").getAsInt());
                account.getProfile().setCountry(jsonObject.get("country").getAsString());
                account.getProfile().setJoin_date(jsonObject.get("join_date").getAsString());
                account.getProfile().setLevel(jsonObject.get("level").getAsDouble());
                account.getProfile().setPlaycount(jsonObject.get("playcount").getAsInt());
                account.getProfile().setPp_rank(jsonObject.get("pp_rank").getAsInt());
                account.getProfile().setPp_raw(jsonObject.get("pp_raw").getAsDouble());
                account.getProfile().setRanked_score(jsonObject.get("ranked_score").getAsLong());
                account.getProfile().setTotal_score(jsonObject.get("total_score").getAsLong());
                account.getProfile().setTotal_seconds_played(jsonObject.get("total_seconds_played").getAsInt());
                account.getProfile().setUser_id(jsonObject.get("user_id").getAsInt());
                account.getProfile().setUsername(jsonObject.get("username").getAsString());

                accountViewModel.update(account);
                result = 1;
                return result;
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return null;
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

    private void loadImage(String imageUrl)  {
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

                                      String path = "/" + account.getIdPpy() + ".jpeg";
                                      File file = new File(
                                              myDir + path);
                                      try {
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
                                  setResult(RESULT_OK);
                                  finish();
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
}
