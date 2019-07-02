package piro13.osucatdroid3;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import java.util.concurrent.ExecutionException;

import piro13.osucatdroid3.accounts.Account;
import piro13.osucatdroid3.accounts.AccountViewModel;
import piro13.osucatdroid3.accounts.ConfigureProfileActivity;
import piro13.osucatdroid3.accounts.UserSession;
import piro13.osucatdroid3.data.Beatmap;

public class Collections_Fragment extends Fragment {
    private AccountViewModel accountViewModel;
    public static final int SEARCH_BEATMAP_REQUEST = 1;
    public static final int SHOW_BEATMAP_REQUEST = 1;
    private int guestMode = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String name = UserSession.getCurrentlyLogged();
        if (name.equals("")) {
            guestMode = 1;
            return inflater.inflate(R.layout.fragment_collections_guest, container, false);
        } else {
            guestMode = 0;
            return inflater.inflate(R.layout.fragment_collections, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        if (guestMode == 0) {
            setHasOptionsMenu(true);

            accountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
            Account currentAccount = null;
            LiveData<List<Beatmap>> lista = null;
            try {
                currentAccount = new CheckByName().execute().get();
                lista = new Checkk().execute(currentAccount).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            RecyclerView recyclerView = view.findViewById(R.id.recycler_view_collections);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setHasFixedSize(true);

            final BeatmapAdapter adapter = new BeatmapAdapter();
            recyclerView.setAdapter(adapter);

            accountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);

            lista.observe(this, new Observer<List<Beatmap>>() {
                @Override
                public void onChanged(@Nullable List<Beatmap> beatmaps) {
                    adapter.submitList(beatmaps);
                }
            });

            final int id_account = currentAccount.getId();

            FloatingActionButton fab = getActivity().findViewById(R.id.button_addbeatmap);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), SearchBeatmapsActivity.class);
                    intent.putExtra(SearchBeatmapsActivity.EXTRA_ACCOUNT, id_account);
                    startActivityForResult(intent, SEARCH_BEATMAP_REQUEST);
                }
            });

            adapter.setOnItemClickListener(new BeatmapAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Beatmap beatmap) {
                    Intent intentToBeatmap = new Intent(getActivity(), BeatmapViewActivity.class);
                    intentToBeatmap.putExtra(BeatmapViewActivity.EXTRA_BEATMAP, beatmap.getBeatmapset_id());
                    startActivityForResult(intentToBeatmap, SHOW_BEATMAP_REQUEST);
                }
            });


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class Checkk extends AsyncTask<Account, Void, LiveData<List<Beatmap>>> {
        @Override
        protected LiveData<List<Beatmap>> doInBackground(Account... currentAccount) {
            LiveData<List<Beatmap>> lista = accountViewModel.getBeatmapsForAccount(currentAccount[0].getId());
            return lista;
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.id.photo_for_profile);
        item.setVisible(false);
        MenuItem item2=menu.findItem(R.id.redownload_avatar);
        item2.setVisible(false);
        MenuItem item3=menu.findItem(R.id.update_information);
        item3.setVisible(false);
    }
}
