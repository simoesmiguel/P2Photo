package data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import googleDriveCostume.SavePhoto;
import serverConnection.Server;

@Entity(tableName = "Participant",
        primaryKeys = {"user_name","album_id"},
        indices = {
                @Index(value = {"user_name"}),
                @Index(value = {"album_id"}),
                @Index(value = {"user_name", "album_id"}),
        })

public class Participant extends Object implements Serializable {
    @ColumnInfo(name = "user_name")
    @NonNull
    public String userName;

    @ColumnInfo(name = "album_id")
    @NonNull
    private long albumId;

    @ColumnInfo(name = "album_slice_link")
    private String albumSliceLink;


    public Participant(@NonNull String userName, long albumId, @NonNull String albumSliceLink) {
        this.userName = userName;
        this.albumId = albumId;
        this.albumSliceLink = albumSliceLink;
    }

    public Participant(User user, Album album, String link_album_slice){
        this(user.getName(),album.getId(), link_album_slice);
    }

    public static Participant instanceFromHashMap(HashMap<String,String> map){

        Participant participant =  new Participant(map.get("participantname"), Long.parseLong(map.get("albumid")), map.get("albumslicelink"));
        return participant;
    }

    public void addToServerAndDB(AppDatabase db, Context t){
        Server.addParticipants(this, db, t);
    }

    public void addToDb(AppDatabase db){
        db.participantDao().insertAll(this);
    }

    @NonNull
    public String getUserName() {
        return userName;
    }

    public Long getAlbumId() {
        return albumId;
    }

    @NonNull
    public String getAlbumSliceLink() {
        if (albumSliceLink!= null){
            return albumSliceLink;
        }else{
            return "";
        }
    }

    public void setAlbumSliceLink(@NonNull String albumSliceLink) {
        this.albumSliceLink = albumSliceLink;
    }

    public static List<User> getUsersNotParticipatingInAlbum(AppDatabase db, Album album){
        return db.participantDao().getUsersNotParticipatingInAlbum(album.getId());
    }

    public void addParticipantTodb(AppDatabase db, Context context){
        if (db.participantDao().findByKey(getAlbumId(),getUserName(),getAlbumSliceLink())==null){
            db.participantDao().insertAll(this);
        }
        SavePhoto savePhoto = SavePhoto.getInstance(context);
        if (!getAlbumSliceLink().equals("")) {
            savePhoto.loadPhotosFromFileToDb(
                    savePhoto.getmDriveService(),
                    context,
                    getAlbumSliceLink(),
                    db.userDao().findByName(getUserName()),
                    db.albumDao().findById(getAlbumId()));
        }
    }


}


