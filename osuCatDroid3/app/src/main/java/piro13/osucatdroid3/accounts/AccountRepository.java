package piro13.osucatdroid3.accounts;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import piro13.osucatdroid3.data.AccountMaps;
import piro13.osucatdroid3.data.Beatmap;

public class AccountRepository {
    private AccountDao accountDao;
    private LiveData<List<Account>> allAccounts;

    public AccountRepository(Application application){
        AccountDatabase database = AccountDatabase.getInstance(application);
        accountDao = database.accountDao();//room automatycznie wygeneruje klasy dla abstracta accountDao, bo jest tam .build() dla instance;
    }

    public void insert(AccountMaps accountMaps){
        new InsertAccountMapsAsyncTask(accountDao).execute(accountMaps);
    }

    public void delete(AccountMaps accountMaps){
        new DeleteAccountMapsAsyncTask(accountDao).execute(accountMaps);
    }

    public void insert(Account account){
        new InsertAccountAsyncTask(accountDao).execute(account);
    }

    public void update(Account account){
        new UpdateAccountAsyncTask(accountDao).execute(account);
    }

    public void delete(Account account){
        new DeleteAccountAsyncTask(accountDao).execute(account);
    }

    public void insert(Beatmap beatmap){
        new InsertBeatmapAsyncTask(accountDao).execute(beatmap);
    }

    public void update(Beatmap beatmap){
        new UpdateBeatmapAsyncTask(accountDao).execute(beatmap);
    }

    public void delete(Beatmap beatmap){
        new DeleteBeatmapAsyncTask(accountDao).execute(beatmap);
    }

    public void deleteAllAccounts(){
        new DeleteAllAccountAsyncTask(accountDao).execute();
    }

    public LiveData<List<Account>> getAllAccounts() {
        allAccounts = accountDao.getAllAccounts();
        return allAccounts;
    }

    public LiveData<List<Account>> getSearchedAccounts(String name) {
        allAccounts = accountDao.getAccountsWithName(name);
        return allAccounts;
    }

    public Account getAccountName(int id2){
        return accountDao.getAccountName(id2);
    }

    public Beatmap getOneBeatmapById(int id){
        return accountDao.getOneBeatmapById(id);
    }

    public Account getOneAccountByName(String givenName){
        return accountDao.getOneAccountByName(givenName);
    }

    public LiveData<List<Beatmap>> getBeatmapsForAccount(int accountId){
        return accountDao.getBeatmapsForAccount(accountId);
    }

    public List<Beatmap> getBeatmapsForAccountAsNormalList(int accountId){
        return accountDao.getBeatmapsForAccountAsNormalList(accountId);
    }

    public List<Beatmap> getAllBeatmaps(){
        return accountDao.getAllBeatmaps();
    }

    private static class InsertAccountAsyncTask extends AsyncTask<Account, Void, Void>{
        private AccountDao accountDao;

        private InsertAccountAsyncTask(AccountDao accountDao){
            this.accountDao = accountDao;
        }

        @Override
        protected Void doInBackground(Account... accounts) {
            accountDao.insert(accounts[0]);
            return null;
        }
    }

    private static class UpdateAccountAsyncTask extends AsyncTask<Account, Void, Void>{
        private AccountDao accountDao;

        private UpdateAccountAsyncTask(AccountDao accountDao){
            this.accountDao = accountDao;
        }

        @Override
        protected Void doInBackground(Account... accounts) {
            accountDao.update(accounts[0]);
            return null;
        }
    }

    private static class DeleteAccountAsyncTask extends AsyncTask<Account, Void, Void>{
        private AccountDao accountDao;

        private DeleteAccountAsyncTask(AccountDao accountDao){
            this.accountDao = accountDao;
        }

        @Override
        protected Void doInBackground(Account... accounts) {
            accountDao.delete(accounts[0]);
            return null;
        }
    }

    private static class DeleteAllAccountAsyncTask extends AsyncTask<Void, Void, Void>{
        private AccountDao accountDao;

        private DeleteAllAccountAsyncTask(AccountDao accountDao){
            this.accountDao = accountDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            accountDao.deleteAllAccounts();
            return null;
        }
    }

    private static class InsertBeatmapAsyncTask extends AsyncTask<Beatmap, Void, Void>{
        private AccountDao beatmapDao;

        private InsertBeatmapAsyncTask(AccountDao beatmapDao){
            this.beatmapDao = beatmapDao;
        }

        @Override
        protected Void doInBackground(Beatmap... beatmaps) {
            beatmapDao.insert(beatmaps[0]);
            return null;
        }
    }

    private static class UpdateBeatmapAsyncTask extends AsyncTask<Beatmap, Void, Void>{
        private AccountDao accountDao;

        private UpdateBeatmapAsyncTask(AccountDao accountDao){
            this.accountDao = accountDao;
        }

        @Override
        protected Void doInBackground(Beatmap... beatmaps) {
            accountDao.update(beatmaps[0]);
            return null;
        }
    }

    private static class DeleteBeatmapAsyncTask extends AsyncTask<Beatmap, Void, Void>{
        private AccountDao accountDao;

        private DeleteBeatmapAsyncTask(AccountDao accountDao){
            this.accountDao = accountDao;
        }

        @Override
        protected Void doInBackground(Beatmap... beatmaps) {
            accountDao.delete(beatmaps[0]);
            return null;
        }
    }

    private static class InsertAccountMapsAsyncTask extends AsyncTask<AccountMaps, Void, Void>{
        private AccountDao accountDao;

        private InsertAccountMapsAsyncTask(AccountDao accountDao){
            this.accountDao = accountDao;
        }

        @Override
        protected Void doInBackground(AccountMaps... accountMaps) {
            accountDao.insert(accountMaps[0]);
            return null;
        }
    }

    private static class DeleteAccountMapsAsyncTask extends AsyncTask<AccountMaps, Void, Void>{
        private AccountDao accountDao;

        private DeleteAccountMapsAsyncTask(AccountDao accountDao){
            this.accountDao = accountDao;
        }

        @Override
        protected Void doInBackground(AccountMaps... accountMaps) {
            accountDao.delete(accountMaps[0]);
            return null;
        }
    }
}
