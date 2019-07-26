package data;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.HashMap;

import pt.ulisboa.tecnico.cmov.p2photo.ViewAlbumPhotosActivity;

@Entity(tableName = "Photo",
        indices = {
                @Index(value = {"user_name"}),
                @Index(value = {"album_id"}),
                @Index(value = {"user_name", "album_id"}),
                },
        foreignKeys = {@ForeignKey(entity = User.class,
                parentColumns = "name",
                childColumns = "user_name",
                onDelete = ForeignKey.CASCADE),

        @ForeignKey(entity = Album.class,
                parentColumns = "id",
                childColumns = "album_id",
                onDelete = ForeignKey.CASCADE)})


public class Photo extends Object implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "user_name")
    @NonNull
    public String userName;

    @ColumnInfo(name = "album_id")
    @NonNull
    private long albumId;

    @ColumnInfo(name = "link")
    @NonNull
    private String link;


    public Photo(@NonNull String userName, long albumId, @NonNull String link){
        this.userName = userName;
        this.albumId = albumId;
        this.link = link;
    }

    public Photo(User user, Album album, String link){
        this(user.getName(),album.getId(),link);
    }


    public Photo(User user, Album album){
        this.userName = user.getName();
        this.albumId =album.getId();
    }


    public static Photo instanceFromHashMap(HashMap<String,String> map){
        Photo photo =  new Photo(map.get("username"), Long.parseLong(map.get("albumid")), map.get("link"));
        return photo;
    }

    public long getAlbumId() {
        return albumId;
    }

    @NonNull
    public String getLink() {
        return link;
    }

    public void setLink(@NonNull String link) {
        this.link = link;
    }

    public void addToDb(AppDatabase db, ViewAlbumPhotosActivity viewAlbumPhotosActivity){
        db.photoDao().insertAll(this);
    }

}
