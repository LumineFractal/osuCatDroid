package piro13.osucatdroid3;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import piro13.osucatdroid3.accounts.Account;
import piro13.osucatdroid3.accounts.AccountViewModel;
import piro13.osucatdroid3.accounts.ConfigureProfileActivity;
import piro13.osucatdroid3.accounts.UserSession;

import static android.app.Activity.RESULT_OK;

public class Profile_Fragment extends Fragment {
    public static final int PROFILE_CONFIGURE_REQUEST = 1;
    private AccountViewModel accountViewModel;
    private int guestMode = 1;
    private TextView username, performance, rank, playtime, playcount, country, ranked_score, total_score, hit_accuracy,
            level, total_hits, hits_per_play, ss_count, s_count, a_count, join_date;
    //fake singleton
    private static Profile_Fragment instance;

    public static Profile_Fragment getInstance() {
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        instance = this;
        String name = UserSession.getCurrentlyLogged();
        if(name.equals("")){
            guestMode = 1;
            return inflater.inflate(R.layout.fragment_profile_guest, container, false);
        }
        else{
            guestMode = 0;
            return inflater.inflate(R.layout.fragment_profile, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        if(guestMode == 0){
            accountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
            Account currentAccount = null;

            setHasOptionsMenu(true);

            try {
                currentAccount = new CheckByName().execute().get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            ImageView navAvatar = headerView.findViewById(R.id.imageViewNav);
            String pathNav = Environment.getExternalStorageDirectory().getPath() + "/osuCatDroid/avatars/" + currentAccount.getIdPpy() + ".jpeg";
            File fileNav = new File(pathNav);
            if (fileNav.exists()) {
                Picasso.get().load(fileNav).into(navAvatar);
            } else {
                //it works
                Picasso.get().load(R.drawable.account_placeholder).into(navAvatar);
            }

            if(currentAccount.getIdPpy() == 0) {
                Intent intent = new Intent(getActivity(), ConfigureProfileActivity.class);
                intent.putExtra(ConfigureProfileActivity.EXTRA_ACCOUNT, currentAccount.getId());
                startActivityForResult(intent, PROFILE_CONFIGURE_REQUEST);
            }else{
                ImageView avatar = getActivity().findViewById(R.id.profile_image);
                username = getActivity().findViewById(R.id.profile_username);
                performance = getActivity().findViewById(R.id.profile_performance);
                rank = getActivity().findViewById(R.id.profile_rank);
                playtime = getActivity().findViewById(R.id.profile_playtime);
                playcount = getActivity().findViewById(R.id.profile_playcount);
                country = getActivity().findViewById(R.id.profile_country);
                ranked_score = getActivity().findViewById(R.id.profile_ranked_score);
                total_score = getActivity().findViewById(R.id.profile_total_score);
                hit_accuracy = getActivity().findViewById(R.id.profile_hit_accuracy);
                level = getActivity().findViewById(R.id.profile_level);
                total_hits = getActivity().findViewById(R.id.profile_total_hits);
                hits_per_play = getActivity().findViewById(R.id.profile_hits_per_play);
                ImageView rank_ss = getActivity().findViewById(R.id.profile_SS);
                ImageView rank_s = getActivity().findViewById(R.id.profile_S);
                ImageView rank_a = getActivity().findViewById(R.id.profile_A);
                ss_count = getActivity().findViewById(R.id.profile_SS_count);
                s_count = getActivity().findViewById(R.id.profile_S_count);
                a_count = getActivity().findViewById(R.id.profile_A_count);
                join_date = getActivity().findViewById(R.id.profile_join_date);

                String path = Environment.getExternalStorageDirectory().getPath() + "/osuCatDroid/avatars/" + currentAccount.getIdPpy() + ".jpeg";
                File file = new File(path);
                if(file.exists()){
                    Picasso.get().load(file).into(avatar);
                }else{
                    //it works
                    Picasso.get().load(R.drawable.ic_profile).placeholder(R.drawable.ic_profile).into(avatar);
                }

                rank_ss.setImageResource(R.drawable.ss);
                rank_s.setImageResource(R.drawable.s);
                rank_a.setImageResource(R.drawable.a);

                setProfileData(currentAccount);

            }
        }
    }

    public void setProfileData(Account currentAccount){
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.UK);

        String pp = String.valueOf((int)currentAccount.getProfile().getPp_raw()) + "pp";
        performance.setText(pp);
        username.setText(currentAccount.getProfile().getUsername());
        String rankSquare = "#" + String.valueOf(currentAccount.getProfile().getPp_rank());
        rank.setText(rankSquare);
        String playtimeStr =  String.valueOf(currentAccount.getProfile().getTotal_seconds_played()/3600) + " hours";
        playtime.setText(playtimeStr);
        String plcount = numberFormat.format(currentAccount.getProfile().getPlaycount());
        playcount.setText(plcount);
        Locale loc = new Locale("",currentAccount.getProfile().getCountry());
        country.setText(loc.getDisplayCountry());
        long rsScoreTrunc = currentAccount.getProfile().getRanked_score()/1000;
        String rscore = numberFormat.format(rsScoreTrunc);
        ranked_score.setText(rscore);
        long tsScoreTrunc = currentAccount.getProfile().getTotal_score()/1000;
        String tscore = numberFormat.format(tsScoreTrunc);
        total_score.setText(tscore);

        long totalHits = currentAccount.getProfile().getCount300() + currentAccount.getProfile().getCount100() + currentAccount.getProfile().getCount50();
        double hc = ((currentAccount.getProfile().getCount300() * 10) + (currentAccount.getProfile().getCount100() * 3.33)
                + (currentAccount.getProfile().getCount50() * 1.66)) / totalHits *10;

        String hitacc = new DecimalFormat("###.##").format(hc) + "%";
        hit_accuracy.setText(hitacc);
        level.setText(String.valueOf((int)currentAccount.getProfile().getLevel()));
        String th2 = numberFormat.format(totalHits);
        total_hits.setText(th2);
        String hpp = new DecimalFormat("####.##").format((double)totalHits/currentAccount.getProfile().getPlaycount());
        hits_per_play.setText(hpp);
        ss_count.setText(String.valueOf(currentAccount.getProfile().getCount_rank_ssh() + currentAccount.getProfile().getCount_rank_ss()));
        s_count.setText(String.valueOf(currentAccount.getProfile().getCount_rank_sh() + currentAccount.getProfile().getCount_rank_s()));
        a_count.setText(String.valueOf(currentAccount.getProfile().getCount_rank_a()));
        join_date.setText(currentAccount.getProfile().getJoin_date());
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
    }
}
