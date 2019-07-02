package piro13.osucatdroid3.accounts;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.an.biometric.BiometricCallback;
import com.an.biometric.BiometricManager;

import java.util.concurrent.ExecutionException;
import piro13.osucatdroid3.R;

public class LoginActivity extends AppCompatActivity implements BiometricCallback{
    private AccountViewModel accountViewModel;
    private Account account;

    public static final String EXTRA_ACCOUNT = "com.example.kaveri.android10.EXTRA_ACCOUNT";
    private static final String PREFER_NAME = "ACCOUNT";

    private EditText passwordField;
    private UserSession session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_ACCOUNT)) {
            accountViewModel = ViewModelProviders.of(this).get(AccountViewModel.class);
            try {
                new getNameTask().execute(intent).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSdkVersionNotSupported() {

    }

    @Override
    public void onBiometricAuthenticationNotSupported() {

    }

    @Override
    public void onBiometricAuthenticationNotAvailable() {

    }

    @Override
    public void onBiometricAuthenticationPermissionNotGranted() {

    }

    @Override
    public void onBiometricAuthenticationInternalError(String error) {

    }

    @Override
    public void onAuthenticationFailed() {

    }

    @Override
    public void onAuthenticationCancelled() {

    }

    @Override
    public void onAuthenticationSuccessful() {

    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {

    }

    private class getNameTask extends AsyncTask<Object, Void, Void> {
        Intent intent;

        protected Void doInBackground(Object... params) {
            intent = (Intent) params[0];
            account = accountViewModel.getAccountName(intent.getIntExtra(EXTRA_ACCOUNT, 0));
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            updateFields();
        }
    }

    public void updateFields() {
        TextView nick = findViewById(R.id.nicknameLogin);
        nick.setText(account.getName());
        setTitle(getString(R.string.log_as) + " " + account.getName());
        useSharedPreferences();
    }

    public void useSharedPreferences() {
        final String finalNickname = account.getName();

        passwordField = (EditText) findViewById(R.id.passwordEditText);
        Button buttonLogin = (Button) findViewById(R.id.buttonLogin);

        Button fingerprint = findViewById(R.id.buttonFingerprint);

        fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new BiometricManager.BiometricBuilder(LoginActivity.this)
                        .setTitle("Add a title")
                        .setSubtitle("Add a subtitle")
                        .setDescription("Add a description")
                        .setNegativeButtonText("Add a cancel button")
                        .build()
                        .authenticate(LoginActivity.this);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String password = passwordField.getText().toString();
                if (password.trim().length() > 0) {
                    if (password.equals(account.getPassword())) {
                        if(UserSession.isUserLoggedIn()){
                            UserSession.logoutUser();
                        }
                        UserSession.createUserLoginSession(finalNickname);
                        Intent data = new Intent();
                        data.putExtra("id", account.getId());
                        data.putExtra("name", account.getName());
                        setResult(RESULT_OK, data);
                        finish();
                    } else {
                        //incorrect password
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.password_is_incorrect),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    //no password given
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.please_enter_password),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
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