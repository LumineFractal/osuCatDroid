package piro13.osucatdroid3.accounts;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import piro13.osucatdroid3.data.AccountMaps;
import piro13.osucatdroid3.data.Beatmap;

public class AccountViewModel extends AndroidViewModel {
    private AccountRepository repository;
    private LiveData<List<Account>> allAccounts;

    public AccountViewModel(@NonNull Application application) {
        super(application);
        repository = new AccountRepository(application);
    }


    public void insert(AccountMaps accountMaps) {
        repository.insert(accountMaps);
    }

    public void delete(AccountMaps accountMaps) {
        repository.delete(accountMaps);
    }

    public void insert(Account account) {
        repository.insert(account);
    }

    public void update(Account account) {
        repository.update(account);
    }

    public void delete(Account account) {
        repository.delete(account);
    }

    public void insert(Beatmap beatmap) {
        repository.insert(beatmap);
    }

    public void update(Beatmap beatmap) {
        repository.update(beatmap);
    }

    public void delete(Beatmap beatmap) {
        repository.delete(beatmap);
    }

    public void deleteAllAccounts() {
        repository.deleteAllAccounts();
    }

    public LiveData<List<Account>> getAllAccounts() {
        allAccounts = repository.getAllAccounts();
        return allAccounts;
    }

    public LiveData<List<Account>> getSearchedAccounts(String name) {
        allAccounts = repository.getSearchedAccounts(name);
        return allAccounts;
    }

    public LiveData<List<Beatmap>> getBeatmapsForAccount(int accountId){
        return repository.getBeatmapsForAccount(accountId);
    }

    public List<Beatmap> getBeatmapsForAccountAsNormalList(int accountId){
        return repository.getBeatmapsForAccountAsNormalList(accountId);
    }

    public List<Beatmap> getAllBeatmaps(){
        return repository.getAllBeatmaps();
    }

    public Account getAccountName(int id2) {
        return repository.getAccountName(id2);
    }

    public Account getOneAccountByName(String givenName) {
        return repository.getOneAccountByName(givenName);
    }

    public Beatmap getOneBeatmapById(int id) {
        return repository.getOneBeatmapById(id);
    }
}
