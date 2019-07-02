package piro13.osucatdroid3.accounts;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import piro13.osucatdroid3.HttpHandler;
import piro13.osucatdroid3.R;

public class RegisterActivity extends AppCompatActivity {
    private SharedPreferences.Editor editor;
    private UserSession session;
    private AccountViewModel viewModel;

    private EditText username, password, api;
    private long searchResult;
    private long apiResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.new_account);

        Button buttonReg = findViewById(R.id.buttonRegister);
        username = findViewById(R.id.usernameregEditText);
        password = findViewById(R.id.passwordregEditText);
        api = findViewById(R.id.apiregEditText);

        viewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("ACCOUNT", 0);
        editor = sharedPreferences.edit();

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        api.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        Button getAPI = findViewById(R.id.buttonGetAPI);
        getAPI.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://osu.ppy.sh/p/api"));
                startActivity(browserIntent);
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = username.getText().toString();
                String pass = password.getText().toString();
                String apii = api.getText().toString();

                if (username.getText().length() <= 0) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.enter_name), Toast.LENGTH_SHORT).show();
                } else if (password.getText().length() <= 0) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
                } else if (api.getText().length() <= 0) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.enter_api), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        new CheckName().execute(name).get();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (searchResult == 1) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.There_is_already_account_with_that_name), Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            new CheckApi().execute(apii).get();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (apiResult == 1) {
                            Toast.makeText(RegisterActivity.this, getString(R.string.wrong_api_key), Toast.LENGTH_SHORT).show();
                        } else {
                            editor.putString("currentlyLogged", name);
                            editor.apply();
                            Account account = new Account(name, apii, pass);
                            viewModel.insert(account);
                            //session = UserSession.getInstance();
                            //session.createUserLoginSession(name);
                            Intent data = new Intent();
                            data.putExtra("id", account.getId());
                            data.putExtra("name", account.getName());
                            setResult(RESULT_FIRST_USER, data);
                            finish();
                        }
                    }
                }
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private class CheckName extends AsyncTask<String, Void, Long> {
        @Override
        protected Long doInBackground(String... name) {
            searchResult = 0;
            Account respond = viewModel.getOneAccountByName(username.getText().toString());
            if (respond != null) {
                if(respond.getName().equals(name[0])){
                    searchResult = 1;
                }
            }
            return searchResult;
        }
    }

    private class CheckApi extends AsyncTask<String, Void, Long> {
        @Override
        protected Long doInBackground(String... givenAPI) {
            apiResult = 1;
            String TAG = RegisterActivity.class.getSimpleName();

            HttpHandler sh = new HttpHandler();
            String url = "https://osu.ppy.sh/api/get_user" + "?k=" + givenAPI[0] + "&u=" + 2 + "&m=" + 0;
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                apiResult=0;
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return apiResult;
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
