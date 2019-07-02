package piro13.osucatdroid3;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import piro13.osucatdroid3.accounts.Account;
import piro13.osucatdroid3.accounts.AccountViewModel;

public class SearchBeatmapsActivity extends AppCompatActivity {
    public static final String EXTRA_ACCOUNT = "com.example.kaveri.android10.EXTRA_ACCOUNT";
    public static final int SEARCH_BEATMAP_RESULT_REQUEST = 1;
    public static final int ADD_SEARCHED_BEATMAPS = 2;
    private AccountViewModel accountViewModel;
    private Account account;
    private EditText query;
    private CheckBox checkBoxRanked, checkBoxQualified, checkBoxLoved, checkBoxUnranked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_beatmaps);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.search_beatmaps);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ACCOUNT)) {
            accountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
            try {
                new GetNameTask().execute(intent).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        query = findViewById(R.id.search_query);
        checkBoxRanked = findViewById(R.id.search_ranked_check);
        checkBoxQualified = findViewById(R.id.search_qualified_check);
        checkBoxLoved = findViewById(R.id.search_loved_check);
        checkBoxUnranked = findViewById(R.id.search_unranked_check);
        Button button = findViewById(R.id.search_button);

        button.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                String queryString = query.getText().toString();
                boolean checkR = checkBoxRanked.isChecked();
                boolean checkQ = checkBoxQualified.isChecked();
                boolean checkL = checkBoxLoved.isChecked();
                boolean checkU = checkBoxUnranked.isChecked();

                if (queryString.equals("")) {
                    Toast.makeText(SearchBeatmapsActivity.this, getString(R.string.please_enter_your_query), Toast.LENGTH_SHORT).show();
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("https://bloodcat.com/osu/?q=");
                    stringBuilder.append(queryString);
                    stringBuilder.append("=b&s=");
                    if (checkR) {
                        stringBuilder.append("1,2");
                        if (checkQ || checkL || checkU) {
                            stringBuilder.append(",");
                        }
                    }
                    if (checkQ) {
                        stringBuilder.append("3");
                        if (checkL || checkU) {
                            stringBuilder.append(",");
                        }
                    }
                    if (checkL) {
                        stringBuilder.append("4");
                        if (checkU) {
                            stringBuilder.append(",");
                        }
                    }
                    if (checkU) {
                        stringBuilder.append("0");
                    }
                    stringBuilder.append("&m=0&g=&l=");
                    String request = stringBuilder.toString();

                    Intent intent = new Intent(SearchBeatmapsActivity.this, SearchResultsActivity.class);
                    intent.putExtra(SearchBeatmapsActivity.EXTRA_ACCOUNT, request);
                    startActivityForResult(intent, SEARCH_BEATMAP_RESULT_REQUEST);
                }
            }
        }));


        query.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard(v);
                    return true;
                }
                return false;
            }
        });
        query.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private class GetNameTask extends AsyncTask<Object, Void, Void> {
        Intent intent;

        protected Void doInBackground(Object... params) {
            intent = (Intent) params[0];
            account = accountViewModel.getAccountName(intent.getIntExtra(EXTRA_ACCOUNT, 0));
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(SearchBeatmapsActivity.this, AddSearchedBeatmaps.class);
            intent.putStringArrayListExtra("beatmapList",data.getStringArrayListExtra("list"));
            startActivityForResult(intent, ADD_SEARCHED_BEATMAPS);
        }
    }
}
