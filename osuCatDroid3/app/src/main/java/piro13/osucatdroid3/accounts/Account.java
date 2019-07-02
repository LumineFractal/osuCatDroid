package piro13.osucatdroid3.accounts;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import piro13.osucatdroid3.data.Profile;

@Entity(tableName = "accounts_table")
public class Account {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String api;
    private String password;
    private int idPpy;

    @Embedded
    private Profile profile;

    public Account(String name, String api, String password) {
        this.name = name;
        this.api = api;
        this.password = password;
        this.idPpy = 0;
        this.profile = new Profile();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getApi() {
        return api;
    }

    public String getPassword() {
        return password;
    }

    public void setIdPpy(int idPpy) {
        this.idPpy = idPpy;
    }

    public int getIdPpy() {
        return idPpy;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
