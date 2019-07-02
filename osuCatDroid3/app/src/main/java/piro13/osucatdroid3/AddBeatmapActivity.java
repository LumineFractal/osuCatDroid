package piro13.osucatdroid3;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;

import piro13.osucatdroid3.accounts.Account;
import piro13.osucatdroid3.accounts.AccountViewModel;
import piro13.osucatdroid3.accounts.UserSession;
import piro13.osucatdroid3.data.AccountMaps;
import piro13.osucatdroid3.data.Beatmap;

public class AddBeatmapActivity extends AppCompatActivity {
    public static final String EXTRA_BEATMAP = "com.example.kaveri.android10.EXTRA_BEATMAP";
    private ImageView background, bpmImage, lengthImage;
    private TextView title, artist, mapper, submitted, statusT, status,
            bpm, length, cs, hp, od, ar, sr, success, sourceT, source, tags, tagsT;
    private Button addToCollection;
    private AccountViewModel accountViewModel;
    private Account currentAccount;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beatmap);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.beatmap_add);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_BEATMAP)) {
            accountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
            try {
                currentAccount = new CheckByName().execute().get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        final AddParams params = new AddParams(currentAccount, intent.getExtras().getInt(EXTRA_BEATMAP));
        Beatmap beatmap = null;
        try {
            beatmap = new GetBeatmap().execute(params).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        background = findViewById(R.id.beatmap_add_image);

        String path = Environment.getExternalStorageDirectory().getPath() + "/osuCatDroid/beatmaps/" + beatmap.getBeatmapset_id() + ".jpeg";
        File file = new File(path);
        if (file.exists()) {
            Picasso.get().load(file).into(background);
        } else {
            String imageURL = "https://b.ppy.sh/thumb/" + beatmap.getBeatmapset_id() + ".jpg";
            Picasso.get().load(imageURL).placeholder(R.drawable.ic_map).into(background);
        }

        bpmImage = findViewById(R.id.beatmap_add_bpmT);
        lengthImage = findViewById(R.id.beatmap_add_lengthT);
        title = findViewById(R.id.beatmap_add_title);
        artist = findViewById(R.id.beatmap_add_artist);
        mapper = findViewById(R.id.beatmap_add_mapper);
        submitted = findViewById(R.id.beatmap_add_submitted);
        statusT = findViewById(R.id.beatmap_add_statusT);
        status = findViewById(R.id.beatmap_add_status);
        bpm = findViewById(R.id.beatmap_add_bpm);
        length = findViewById(R.id.beatmap_add_length);
        cs = findViewById(R.id.beatmap_add_cs);
        od = findViewById(R.id.beatmap_add_od);
        hp = findViewById(R.id.beatmap_add_hp);
        ar = findViewById(R.id.beatmap_add_ar);
        sr = findViewById(R.id.beatmap_add_sr);
        success = findViewById(R.id.beatmap_add_success);
        sourceT = findViewById(R.id.beatmap_add_sourceT);
        source = findViewById(R.id.beatmap_add_source);
        tagsT = findViewById(R.id.beatmap_add_tagsT);
        tags = findViewById(R.id.beatmap_add_tags);
        addToCollection = findViewById(R.id.beatmap_add_button_add);

        bpmImage.setImageResource(R.drawable.bpm);
        lengthImage.setImageResource(R.drawable.length);
        title.setText(beatmap.getTitle());
        artist.setText(beatmap.getArtist());
        mapper.setText(beatmap.getCreator());
        submitted.setText(beatmap.getLast_update().substring(0, 10));//thats not it
        int statusNumber = beatmap.getApproved();
        if (statusNumber == 1 || statusNumber == 2) {
            statusT.setText(getString(R.string.ranked_on));
            status.setText(beatmap.getApproved_date().substring(0, 10));
        } else if (statusNumber == 3) {
            statusT.setText(getString(R.string.qualified_on));
            status.setText(beatmap.getApproved_date().substring(0, 10));
        } else if (statusNumber == 4) {
            statusT.setText(getString(R.string.loved_on));
            status.setText(beatmap.getApproved_date().substring(0, 10));
        }
        DecimalFormat format = new DecimalFormat("##.##");
        bpm.setText(String.valueOf(format.format(beatmap.getBpm())));
        length.setText(convertToMinutes(beatmap.getTotal_length()));
        cs.setText(String.valueOf(beatmap.getDiff_size()));
        od.setText(String.valueOf(beatmap.getDiff_overall()));
        hp.setText(String.valueOf(beatmap.getDiff_drain()));
        ar.setText(String.valueOf(beatmap.getDiff_approach()));
        sr.setText(String.valueOf(format.format(beatmap.getDifficultyrating())));
        DecimalFormat formatOne = new DecimalFormat("##.#");
        if (beatmap.getPlaycount() != 0) {
            double successRate = beatmap.getPasscount() / beatmap.getPlaycount() * 100;
            String succRate = formatOne.format(successRate) + "%";
            String successR = succRate + " (" + beatmap.getPasscount() + " of " + beatmap.getPlaycount() + " plays)";
            success.setText(successR);
        } else {
            success.setText(getString(R.string.not_yet_played));
        }
        if (!beatmap.getSource().equals("")) {
            sourceT.setText(getString(R.string.source));
            source.setText(beatmap.getSource());
        }
        if (!beatmap.getTags().equals("")) {
            tagsT.setText(getString(R.string.tags));
            tags.setText(beatmap.getTags());
        }
        final ParamsForAdd paramsForAdd = new ParamsForAdd(currentAccount, beatmap);
        final Account curAcc2 = currentAccount;
        final int idBeatmap = beatmap.getBeatmapset_id();
        addToCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    if (!new CheckIfBeatmapPresent().execute(idBeatmap).get()) {
                        new AddBeatmap().execute(paramsForAdd).get();
                    }
                    ParamsForAdd params2 = new ParamsForAdd(curAcc2, new GetBeatmapFromDB().execute(idBeatmap).get());
                    //add checkifbeatmappresent
                    new AddBeatmapForAccount().execute(params2).get();
                } catch (ExecutionException | InterruptedException e) {
                    Log.d(AddBeatmapActivity.class.getSimpleName(), "Problem with adding map");
                    e.printStackTrace();
                }
                String imageUrl = "http://b.ppy.sh/thumb/" + idBeatmap + "l.jpg";
                getBackground(imageUrl, idBeatmap);
                Toast.makeText(getApplicationContext(),
                        getString(R.string.beatmap_added),
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
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

    private void getBackground(String imageUrl, final int idBeatmap) {
        verifyStoragePermissions(this);
        if (isExternalStorageWritable()) {
            Picasso.get().load(imageUrl)
                    .into(new Target() {
                              @Override
                              public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                  try {
                                      String root = Environment.getExternalStorageDirectory().getPath();
                                      File myDir = new File(root + "/osuCatDroid/beatmaps");

                                      if (!myDir.exists()) {
                                          myDir.mkdirs();
                                      }

                                      String path = "/" + idBeatmap + ".jpeg";
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

    private String convertToMinutes(int length) {
        int hours = length / 3600;
        int minutes = (length % 3600) / 60;
        int seconds = length % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    private class AddParams {
        Account account;
        int beatmapsetId;

        AddParams(Account account, int beatmapsetId) {
            this.account = account;
            this.beatmapsetId = beatmapsetId;
        }
    }

    private class ParamsForAdd {
        Account account;
        Beatmap beatmap;

        ParamsForAdd(Account account, Beatmap beatmap) {
            this.account = account;
            this.beatmap = beatmap;
        }
    }

    private class CheckByName extends AsyncTask<String, Void, Account> {
        @Override
        protected Account doInBackground(String... name) {
            Account respond = accountViewModel.getOneAccountByName(UserSession.getCurrentlyLogged());
            if (respond == null) {
                Log.d(AddBeatmapActivity.class.getSimpleName(), "Error: there is a problem with this account");
            }
            return respond;
        }
    }

    private class AddBeatmap extends AsyncTask<ParamsForAdd, Void, Boolean> {
        @Override
        protected Boolean doInBackground(ParamsForAdd... params) {
            Beatmap beatmap = params[0].beatmap;
            if (!accountViewModel.getAllBeatmaps().contains(beatmap)) {
                accountViewModel.insert(beatmap);
            }
            return true;
        }
    }

    private class CheckIfBeatmapPresent extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... params) {
            int beatmapId = params[0];
            if (accountViewModel.getOneBeatmapById(beatmapId) != null) {
                return true;
            }
            return false;
        }
    }

    private class AddBeatmapForAccount extends AsyncTask<ParamsForAdd, Void, Boolean> {
        @Override
        protected Boolean doInBackground(ParamsForAdd... params) {
            Account account = params[0].account;
            Beatmap beatmap = params[0].beatmap;
            AccountMaps accountMaps = new AccountMaps(account.getId(), beatmap.getId());
            accountViewModel.insert(accountMaps);
            return true;
        }
    }

    private class GetBeatmapFromDB extends AsyncTask<Integer, Void, Beatmap> {
        @Override
        protected Beatmap doInBackground(Integer... name) {
            Beatmap beatmap = accountViewModel.getOneBeatmapById(name[0]);
            if (beatmap == null) {
                Log.d(AddBeatmapActivity.class.getSimpleName(), "Error: there is a problem with this beatmap");
            }
            return beatmap;
        }
    }

    private class GetBeatmap extends AsyncTask<AddParams, Void, Beatmap> {
        @Override
        protected Beatmap doInBackground(AddParams... params) {
            Beatmap beatmap = null;
            String url = "https://osu.ppy.sh/api/get_beatmaps" + "?k=" + params[0].account.getApi() + "&s=" + params[0].beatmapsetId + "&m=" + 0;
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);

            Log.e("Collections: ", "Response from url: " + jsonStr);
            if (jsonStr != null && !jsonStr.equals("[]")) {
                JsonArray jsonArray = new JsonParser().parse(jsonStr).getAsJsonArray();
                if (jsonArray.size() > 0) {
                    JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                    beatmap = new Beatmap();

                    beatmap.setApproved(jsonObject.get("approved").getAsInt());
                    JsonElement jsonElement = jsonObject.get("approved_date");
                    if (jsonElement != null && !jsonElement.isJsonNull()) {
                        beatmap.setApproved_date(jsonObject.get("approved_date").getAsString());
                    } else {
                        beatmap.setApproved_date("-");
                    }
                    beatmap.setLast_update(jsonObject.get("last_update").getAsString());
                    beatmap.setArtist(jsonObject.get("artist").getAsString());
                    beatmap.setBeatmap_id(jsonObject.get("beatmap_id").getAsInt());
                    beatmap.setBeatmapset_id(jsonObject.get("beatmapset_id").getAsInt());
                    beatmap.setBpm(jsonObject.get("bpm").getAsDouble());
                    beatmap.setCreator(jsonObject.get("creator").getAsString());
                    beatmap.setCreator_id(jsonObject.get("creator_id").getAsInt());
                    beatmap.setDifficultyrating(jsonObject.get("difficultyrating").getAsDouble());
                    beatmap.setDiff_size(jsonObject.get("diff_size").getAsDouble());
                    beatmap.setDiff_overall(jsonObject.get("diff_overall").getAsDouble());
                    beatmap.setDiff_approach(jsonObject.get("diff_approach").getAsDouble());
                    beatmap.setDiff_drain(jsonObject.get("diff_drain").getAsDouble());
                    beatmap.setHit_length(jsonObject.get("hit_length").getAsInt());
                    beatmap.setSource(jsonObject.get("source").getAsString());
                    beatmap.setTitle(jsonObject.get("title").getAsString());
                    beatmap.setTotal_length(jsonObject.get("total_length").getAsInt());
                    beatmap.setVersion(jsonObject.get("version").getAsString());
                    beatmap.setMode(jsonObject.get("mode").getAsInt());
                    beatmap.setTags(jsonObject.get("tags").getAsString());
                    beatmap.setFavourite_count(jsonObject.get("favourite_count").getAsInt());
                    beatmap.setPlaycount(jsonObject.get("playcount").getAsInt());
                    beatmap.setPasscount(jsonObject.get("passcount").getAsInt());
                    beatmap.setMax_combo(jsonObject.get("max_combo").getAsInt());
                } else {
                    Log.e("Collections:", "JSON is empty list.");
                }
            } else {
                Log.e("Collections:", "Couldn't get json from server.");
            }

            return beatmap;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
