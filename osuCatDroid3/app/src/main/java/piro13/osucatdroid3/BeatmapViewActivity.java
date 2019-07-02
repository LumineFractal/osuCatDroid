package piro13.osucatdroid3;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;

import piro13.osucatdroid3.accounts.Account;
import piro13.osucatdroid3.accounts.AccountViewModel;
import piro13.osucatdroid3.accounts.UserSession;
import piro13.osucatdroid3.data.AccountMaps;
import piro13.osucatdroid3.data.Beatmap;

public class BeatmapViewActivity extends AppCompatActivity {
    public static final String EXTRA_BEATMAP = "com.example.kaveri.android10.EXTRA_BEATMAP";
    private ImageView background, bpmImage, lengthImage;
    private TextView title, artist, mapper, submitted, statusT, status,
            bpm, length, cs, hp, od, ar, sr, success, sourceT, source, tags, tagsT;
    private Button removeFromCollection;
    private AccountViewModel accountViewModel;
    private Account currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beatmap);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.beatmap_info);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_BEATMAP)) {
            accountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
            try {
                currentAccount = new CheckByName().execute().get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        Beatmap beatmap = null;
        try {
            beatmap = new GetBeatmapFromDB().execute(intent.getExtras().getInt(EXTRA_BEATMAP)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        background = findViewById(R.id.beatmap_image);

        String path = Environment.getExternalStorageDirectory().getPath() + "/osuCatDroid/beatmaps/" + beatmap.getBeatmapset_id() + ".jpeg";
        File file = new File(path);
        if(file.exists()){
            Picasso.get().load(file).into(background);
        }else{
            String imageURL = "https://b.ppy.sh/thumb/" + beatmap.getBeatmapset_id() + ".jpg";
            Picasso.get().load(imageURL).placeholder(R.drawable.ic_map).into(background);
        }

        bpmImage = findViewById(R.id.beatmap_bpmT);
        lengthImage = findViewById(R.id.beatmap_lengthT);
        title = findViewById(R.id.beatmap_title);
        artist = findViewById(R.id.beatmap_artist);
        mapper = findViewById(R.id.beatmap_mapper);
        submitted = findViewById(R.id.beatmap_submitted);
        statusT = findViewById(R.id.beatmap_statusT);
        status = findViewById(R.id.beatmap_status);
        bpm = findViewById(R.id.beatmap_bpm);
        length = findViewById(R.id.beatmap_length);
        cs = findViewById(R.id.beatmap_cs);
        od = findViewById(R.id.beatmap_od);
        hp = findViewById(R.id.beatmap_hp);
        ar = findViewById(R.id.beatmap_ar);
        sr = findViewById(R.id.beatmap_sr);
        success = findViewById(R.id.beatmap_success);
        sourceT = findViewById(R.id.beatmap_sourceT);
        source = findViewById(R.id.beatmap_source);
        tagsT = findViewById(R.id.beatmap_tagsT);
        tags = findViewById(R.id.beatmap_tags);
        removeFromCollection = findViewById(R.id.beatmap_button_remove);

        bpmImage.setImageResource(R.drawable.bpm);
        lengthImage.setImageResource(R.drawable.length);
        title.setText(beatmap.getTitle());
        artist.setText(beatmap.getArtist());
        mapper.setText(beatmap.getCreator());
        submitted.setText(beatmap.getLast_update().substring(0, 10));//thats not it
        int statusNumber = beatmap.getApproved();
        if (statusNumber == 1 || statusNumber == 2) {
            statusT.setText(R.string.ranked_on);
            String s = "hello";
            System.out.println(s.substring(0, 2));//he
            status.setText(beatmap.getApproved_date().substring(0, 10));
        } else if (statusNumber == 3) {
            statusT.setText(R.string.qualified_on);
            status.setText(beatmap.getApproved_date().substring(0, 10));
        } else if (statusNumber == 4) {
            statusT.setText(R.string.loved_on);
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
        if(beatmap.getPlaycount()!=0){
            double successRate = beatmap.getPasscount() / beatmap.getPlaycount() * 100;
            String succRate = formatOne.format(successRate) + "%";
            String successR = succRate + " (" + beatmap.getPasscount() + " of " + beatmap.getPlaycount() + " plays)";
            success.setText(successR);
        }else{
            success.setText(R.string.not_yet_played);
        }
        if (!beatmap.getSource().equals("")) {
            sourceT.setText(R.string.source);
            source.setText(beatmap.getSource());
        }
        if(!beatmap.getTags().equals("")){
            tagsT.setText(R.string.tags);
            tags.setText(beatmap.getTags());
        }
        final ParamsForRemove paramsForRemove = new ParamsForRemove(currentAccount, beatmap);
        removeFromCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    new RemoveBeatmapFromAccount().execute(paramsForRemove).get();
                } catch (ExecutionException | InterruptedException e) {
                    Log.d(AddBeatmapActivity.class.getSimpleName(), "Problem with removing app from account");
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(),
                        getString(R.string.beatmap_removed_from_this_account),
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
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

    private class ParamsForRemove {
        Account account;
        Beatmap beatmap;

        ParamsForRemove(Account account, Beatmap beatmap) {
            this.account = account;
            this.beatmap = beatmap;
        }
    }

    private class CheckByName extends AsyncTask<String, Void, Account> {
        @Override
        protected Account doInBackground(String... name) {
            Account respond = accountViewModel.getOneAccountByName(UserSession.getCurrentlyLogged());
            if (respond == null) {
                Log.d(BeatmapViewActivity.class.getSimpleName(), "Error: there is a problem with this account");
            }
            return respond;
        }
    }

    private class RemoveBeatmapFromAccount extends AsyncTask<ParamsForRemove, Void, Boolean> {
        @Override
        protected Boolean doInBackground(ParamsForRemove... params) {
            Account account = params[0].account;
            Beatmap beatmap = params[0].beatmap;
            AccountMaps accountMaps = new AccountMaps(account.getId(), beatmap.getId());
            accountViewModel.delete(accountMaps);
            return true;
        }
    }

    private class GetBeatmapFromDB extends AsyncTask<Integer, Void, Beatmap> {
        @Override
        protected Beatmap doInBackground(Integer... name) {
            Beatmap beatmap = accountViewModel.getOneBeatmapById(name[0]);
            if (beatmap == null) {
                Log.d(BeatmapViewActivity.class.getSimpleName(), "Error: there is a problem with this beatmap");
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
