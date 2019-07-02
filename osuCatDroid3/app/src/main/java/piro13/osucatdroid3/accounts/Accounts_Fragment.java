package piro13.osucatdroid3.accounts;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.ExecutionException;

import piro13.osucatdroid3.AddBeatmapActivity;
import piro13.osucatdroid3.Profile_Fragment;
import piro13.osucatdroid3.R;
import piro13.osucatdroid3.data.AccountMaps;
import piro13.osucatdroid3.data.Beatmap;

import static android.app.Activity.RESULT_FIRST_USER;
import static android.app.Activity.RESULT_OK;

public class Accounts_Fragment extends Fragment {
    public static final int ACCOUNT_LOGIN_REQUEST = 1;
    public static final int ACCOUNT_REGISTER_REQUEST = 2;
    private AccountViewModel accountViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accounts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        final AccountAdapter adapter = new AccountAdapter();
        recyclerView.setAdapter(adapter);

        accountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);

        accountViewModel.getAllAccounts().observe(this, new Observer<List<Account>>() {
            @Override
            public void onChanged(@Nullable List<Account> accounts) {
                adapter.submitList(accounts);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                Account account = adapter.getAccountAt(viewHolder.getAdapterPosition());
                if(account.getName().equals(UserSession.getCurrentlyLogged())){
                    UserSession.logoutUser();
                    TextView infonav = (TextView) getActivity().findViewById(R.id.infoNav);
                    infonav.setText(R.string.github_com_piro13);
                    NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                    View headerView = navigationView.getHeaderView(0);
                    ImageView avatar = headerView.findViewById(R.id.imageViewNav);
                    Picasso.get().load(R.drawable.account_placeholder).placeholder(R.drawable.account_placeholder).into(avatar);
                }
                List<Beatmap> beatmapList = null;
                try {
                    beatmapList = new getBeatmapsForAccount().execute(account.getId()).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                if(beatmapList!=null && !beatmapList.isEmpty()){
                    for (Beatmap beatmap : beatmapList) {
                        AccountMaps accountMaps = new AccountMaps(account.getId(), beatmap.getId());
                        accountViewModel.delete(accountMaps);
                    }
                }
                accountViewModel.delete(account);
                Toast.makeText(getActivity(), getString(R.string.account_deleted), Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        FloatingActionButton fab = getActivity().findViewById(R.id.button_addaccount);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivityForResult(intent, ACCOUNT_REGISTER_REQUEST);
            }
        });

        adapter.setOnItemClickListener(new AccountAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Account account) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra(LoginActivity.EXTRA_ACCOUNT, account.getId());
                startActivityForResult(intent, ACCOUNT_LOGIN_REQUEST);
            }
        });
    }

    private class getBeatmapsForAccount extends AsyncTask<Integer, Void, List<Beatmap>> {
        @Override
        protected List<Beatmap> doInBackground(Integer... id) {
            return accountViewModel.getBeatmapsForAccountAsNormalList(id[0]);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK || resultCode == RESULT_FIRST_USER) {
            TextView header = (TextView) getActivity().findViewById(R.id.infoNav);
            String text = getString(R.string.logged_as) + " " + data.getStringExtra("name");
            header.setText(text);
            NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
            navigationView.setCheckedItem(R.id.nav_profile);
            Fragment profile = new Profile_Fragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, profile).addToBackStack(null).commit();
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
