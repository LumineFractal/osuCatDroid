package piro13.osucatdroid3.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "beatmap_table")
public class Beatmap {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int approved;// 4 = loved, 3 = qualified, 2 = approved, 1 = ranked, 0 = pending, -1 = WIP, -2 = graveyard
    private String approved_date;
    private String last_update;
    private String artist;
    private int beatmap_id;
    private int beatmapset_id;
    private double bpm;
    private String creator;
    private int creator_id;
    private double difficultyrating;
    private double diff_size; //CS
    private double diff_overall; //OD
    private double diff_approach; //AR
    private double diff_drain; //HP
    private int hit_length; // seconds from first note to last note not including breaks
    private String source;
    private String title;
    private int total_length;
    private String version;
    private int mode;
    private String tags;
    private int favourite_count;
    private int playcount;
    private int passcount;
    private int max_combo;

    public Beatmap() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }

    public String getApproved_date() {
        return approved_date;
    }

    public void setApproved_date(String approved_date) {
        this.approved_date = approved_date;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getBeatmap_id() {
        return beatmap_id;
    }

    public void setBeatmap_id(int beatmap_id) {
        this.beatmap_id = beatmap_id;
    }

    public int getBeatmapset_id() {
        return beatmapset_id;
    }

    public void setBeatmapset_id(int beatmapset_id) {
        this.beatmapset_id = beatmapset_id;
    }

    public double getBpm() {
        return bpm;
    }

    public void setBpm(double bpm) {
        this.bpm = bpm;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public int getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(int creator_id) {
        this.creator_id = creator_id;
    }

    public double getDifficultyrating() {
        return difficultyrating;
    }

    public void setDifficultyrating(double difficultyrating) {
        this.difficultyrating = difficultyrating;
    }

    public double getDiff_size() {
        return diff_size;
    }

    public void setDiff_size(double diff_size) {
        this.diff_size = diff_size;
    }

    public double getDiff_overall() {
        return diff_overall;
    }

    public void setDiff_overall(double diff_overall) {
        this.diff_overall = diff_overall;
    }

    public double getDiff_approach() {
        return diff_approach;
    }

    public void setDiff_approach(double diff_approach) {
        this.diff_approach = diff_approach;
    }

    public double getDiff_drain() {
        return diff_drain;
    }

    public void setDiff_drain(double diff_drain) {
        this.diff_drain = diff_drain;
    }

    public int getHit_length() {
        return hit_length;
    }

    public void setHit_length(int hit_length) {
        this.hit_length = hit_length;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotal_length() {
        return total_length;
    }

    public void setTotal_length(int total_length) {
        this.total_length = total_length;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getFavourite_count() {
        return favourite_count;
    }

    public void setFavourite_count(int favourite_count) {
        this.favourite_count = favourite_count;
    }

    public int getPlaycount() {
        return playcount;
    }

    public void setPlaycount(int playcount) {
        this.playcount = playcount;
    }

    public int getPasscount() {
        return passcount;
    }

    public void setPasscount(int passcount) {
        this.passcount = passcount;
    }

    public int getMax_combo() {
        return max_combo;
    }

    public void setMax_combo(int max_combo) {
        this.max_combo = max_combo;
    }
}
