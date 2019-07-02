package piro13.osucatdroid3.accounts;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import piro13.osucatdroid3.data.AccountMaps;
import piro13.osucatdroid3.data.Beatmap;

@Dao
public interface AccountDao {

    @Insert
    void insert(Account product);

    @Update
    void update(Account product);

    @Delete
    void delete(Account product);

    @Query("DELETE FROM accounts_table")
    void deleteAllAccounts();

    @Query("SELECT * FROM accounts_table ORDER BY api ASC")
    LiveData<List<Account>> getAllAccounts();

    @Query("SELECT * FROM accounts_table WHERE name LIKE :likeName || '%'")
    LiveData<List<Account>> getAccountsWithName(String likeName);

    @Query("SELECT * FROM accounts_table WHERE id=:id2")
    Account getAccountName(int id2);

    @Query("SELECT * FROM accounts_table WHERE name = :givenName")
    Account getOneAccountByName(String givenName);

    @Insert
    void insert(Beatmap beatmap);

    @Update
    void update(Beatmap beatmap);

    @Delete
    void delete(Beatmap beatmap);

    @Query("SELECT * FROM beatmap_table ORDER BY beatmap_id ASC")
    List<Beatmap> getAllBeatmaps();

    @Query("SELECT * FROM beatmap_table WHERE beatmapset_id = :idd")
    Beatmap getOneBeatmapById(int idd);

    @Insert
    void insert(AccountMaps accountMaps);

    @Update
    void update(AccountMaps accountMaps);

    @Delete
    void delete(AccountMaps accountMaps);

    @Query("SELECT * FROM beatmap_table INNER JOIN accountmaps_jointable ON beatmap_table.id=accountmaps_jointable.beatmapId WHERE accountmaps_jointable.accountId=:accountId")
    LiveData<List<Beatmap>> getBeatmapsForAccount(int accountId);

    @Query("SELECT * FROM beatmap_table INNER JOIN accountmaps_jointable ON beatmap_table.id=accountmaps_jointable.beatmapId WHERE accountmaps_jointable.accountId=:accountId")
    List<Beatmap> getBeatmapsForAccountAsNormalList(int accountId);
}
