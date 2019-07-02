package piro13.osucatdroid3.accounts;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserSession extends Application {
    private static UserSession instance;

    // Shared Preferences reference
    private static SharedPreferences pref;

    // Editor reference for Shared preferences
    private static Editor editor;

    // Shared preferences file name
    private static final String PREFER_NAME = "ACCOUNT";

    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "isUserLoggedIn";
    private static final String KEY_NAME = "currentlyLogged";

    private static Context context;

    public static void init(Context context2) {
        context = context2.getApplicationContext();
        getInstance();
    }

    // Constructor
    private UserSession() {
        // Shared preferences mode;
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.commit();
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    //Create login session
    public static void createUserLoginSession(String uName) {
        if (isUserLoggedIn()) {
            logoutUser();
        }

        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

        // Storing name in preferences
        editor.putString(KEY_NAME, uName);

        // commit changes
        editor.commit();
    }

    public static String getCurrentlyLogged() {
        return pref.getString(KEY_NAME, "");
    }

    /**
     * Clear session details
     */
    public static void logoutUser() {
        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();
    }

    public static boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public static String getKeyName() {
        return KEY_NAME;
    }
}
