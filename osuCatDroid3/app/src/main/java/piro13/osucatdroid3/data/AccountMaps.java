package piro13.osucatdroid3.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

import piro13.osucatdroid3.accounts.Account;

@Entity(tableName = "accountmaps_jointable",
        primaryKeys = { "accountId", "beatmapId" },
        foreignKeys = {
                @ForeignKey(entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId"),
                @ForeignKey(entity = Beatmap.class,
                        parentColumns = "id",
                        childColumns = "beatmapId")
        })
public class AccountMaps {

    public final int accountId;
    public final int beatmapId;

    public AccountMaps(final int accountId, final int beatmapId) {
        this.accountId = accountId;
        this.beatmapId = beatmapId;
    }
}
