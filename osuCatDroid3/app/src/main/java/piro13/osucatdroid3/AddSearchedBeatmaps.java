package piro13.osucatdroid3;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import piro13.osucatdroid3.accounts.Account;
import piro13.osucatdroid3.accounts.AccountViewModel;
import piro13.osucatdroid3.accounts.ConfigureProfileActivity;
import piro13.osucatdroid3.accounts.UserSession;
import piro13.osucatdroid3.data.Beatmap;

public class AddSearchedBeatmaps extends AppCompatActivity {
    public static final int ADD_BEATMAP_REQUEST = 1;
    private AccountViewModel accountViewModel;
    private HttpHandler sh;
    private ArrayList<String> list;
    private List<Beatmap> beatmapList;
    private BeatmapAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_searched_beatmaps);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.search_results);
        sh = new HttpHandler();

        accountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
        Account currentAccount = null;
        try {
            currentAccount = new CheckByName().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();

        recyclerView = findViewById(R.id.recycler_view_add_beatmaps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new BeatmapAdapter();
        recyclerView.setAdapter(adapter);

        list = intent.getStringArrayListExtra("beatmapList");
        beatmapList = new ArrayList<>();

        int index = 0;
        for (String item : list) {
            if (index > 10) {
                break;
            }
            index++;
            AddParams params = new AddParams(currentAccount, Integer.parseInt(item));
            Beatmap beatmap = null;
            try {
                beatmap = new GetBeatmapToTempList().execute(params).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            if (beatmap != null) {
                beatmapList.add(beatmap);
            } else {
                index--;
            }
        }

        adapter.submitList(beatmapList);

        adapter.setOnItemClickListener(new BeatmapAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Beatmap beatmap) {
                Intent intentToBeatmap = new Intent(AddSearchedBeatmaps.this, AddBeatmapActivity.class);
                intentToBeatmap.putExtra(AddBeatmapActivity.EXTRA_BEATMAP, beatmap.getBeatmapset_id());
                startActivityForResult(intentToBeatmap, ADD_BEATMAP_REQUEST);
            }
        });

    }

    private static class AddParams {
        Account account;
        int beatmapsetId;

        AddParams(Account account, int beatmapsetId) {
            this.account = account;
            this.beatmapsetId = beatmapsetId;
        }
    }

    private class CheckByName extends AsyncTask<String, Void, Account> {
        @Override
        protected Account doInBackground(String... name) {
            Account respond = accountViewModel.getOneAccountByName(UserSession.getCurrentlyLogged());
            if (respond == null) {
                Log.d(ConfigureProfileActivity.class.getSimpleName(), "Error: there is a problem with this account");
            }
            return respond;
        }
    }

    private class GetBeatmapToTempList extends AsyncTask<AddParams, Void, Beatmap> {
        @Override
        protected Beatmap doInBackground(AddParams... params) {
            Beatmap beatmap = null;
            System.out.println(params[0].account.getName());
            System.out.println(params[0].beatmapsetId);
            String url = "https://osu.ppy.sh/api/get_beatmaps" + "?k=" + params[0].account.getApi() + "&s=" + params[0].beatmapsetId + "&m=" + 0;
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
}
