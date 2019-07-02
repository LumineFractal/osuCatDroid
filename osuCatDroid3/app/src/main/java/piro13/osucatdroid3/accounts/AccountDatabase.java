package piro13.osucatdroid3.accounts;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import piro13.osucatdroid3.data.AccountMaps;
import piro13.osucatdroid3.data.Beatmap;

@Database(entities = {Account.class, Beatmap.class, AccountMaps.class}, version = 1)
public abstract class AccountDatabase extends RoomDatabase {

    private static AccountDatabase instance;

    public abstract AccountDao accountDao();

    public static synchronized AccountDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AccountDatabase.class, "account_database")
                    .fallbackToDestructiveMigration().addCallback(roomCallback).build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private AccountDao accountDao;

        private PopulateDbAsyncTask(AccountDatabase db) {
            accountDao = db.accountDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

}
