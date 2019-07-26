package data;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cz.msebera.android.httpclient.Header;
import googleDriveCostume.DriveServiceHelper;
import googleDriveCostume.SavePhoto;
import jsonParser.JsonParser;
import serverConnection.RequestHandler;
import serverConnection.RequestListener;
import serverConnection.Server;


//TODO Implement serialazable in all data


@Entity(tableName = "Album",
        indices = {
                @Index(value = {"creator"}),
        },
        foreignKeys =
        {@ForeignKey(entity = User.class,
        parentColumns = "name",
        childColumns = "creator",
        onDelete = ForeignKey.CASCADE)}
        )


public class Album extends Object implements Serializable{

    private static final Executor mExecutor = Executors.newSingleThreadExecutor();

    public static String TAG= "Album";

    public static Context ctx;


    @PrimaryKey
    @ColumnInfo(name = "id")
    protected long id;

    @ColumnInfo(name ="title")
    @NonNull
    protected String title;

    @ColumnInfo(name ="creator")
    @NonNull
    private String creator;

    @ColumnInfo(name ="creation_date")
    @NonNull
    protected Date creationDate;

    @ColumnInfo(name ="google_id_folder")
    protected String googleIdFolder;

    @ColumnInfo(name ="google_id_text")
    protected String googleIdText;


    public Album(long id, @NonNull String title, @NonNull String creator, @NonNull Date creationDate) {
        this.id = id;
        this.title = title;
        this.creator = creator;
        this.creationDate = creationDate;
    }

    @Ignore
    public Album(@NonNull String title, @NonNull String creator) {
        this.title = title;
        this.creator = creator;
        this.creationDate = Calendar.getInstance().getTime();
    }

    public Album(String title, User user) {
        this(title,user.name);
    }


    @Ignore
    public Album(Long albumid, String title, String owner) {
        this.id = albumid;
        this.title = title;
        this.creator = owner;
        this.creationDate = Calendar.getInstance().getTime();
    }


    public static Album instanceFromHashMap(HashMap<String,String> map){
        Album album =  new Album(Long.parseLong(map.get("albumid")), map.get("title"), map.get("owner"));
        album.setCreationDate(Calendar.getInstance().getTime());
        return album;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }



    @NonNull
    public User getCreator(AppDatabase db) {
        return db.userDao().findByName(this.creator);
    }


    @NonNull
    public String getCreator() {
        return creator;
    }

    public String getTitle() {
        return title;
    }

    public void setCreationDate(@NonNull Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public List<User> getParticipants(AppDatabase db) {
        return db.albumDao().findUsersOfAlbum(this.id);
    }

    public List<User> getParticipantsExceptYou(AppDatabase db){
        List<User> allUsers = db.albumDao().findUsersOfAlbum(this.id);
        allUsers.remove(User.getSelfUser());
        return allUsers;
    }

    public List<Photo> getPhotos(AppDatabase db) {
        return db.albumDao().findPhotosOfAlbum(this.id);
    }

    public List<Photo> getAllPhotosFromUser(AppDatabase db, User user){
        return db.albumDao().findPhotosOfAlbumOfUser(this.id, user.name);
    }

    public String getLink(AppDatabase db){
        return db.participantDao().getLink(this.id, User.getSelfUser().getName());
    }

    public String getGoogleIdFolder() {
        return googleIdFolder;
    }

    public void setGoogleIdFolder(String googleIdFolder) {
        this.googleIdFolder = googleIdFolder;
    }

    public String getGoogleIdText() {
        return googleIdText;
    }

    public void setGoogleIdText(String googleIdText) {
        this.googleIdText = googleIdText;
    }

    public void addParticipant(AppDatabase db, Context context, User user, String link){
        Participant participant = new Participant(user,this, link);

            if (Looper.myLooper() == null)
            {
                Looper.prepare();
            }

        Server.addParticipants(participant, db , context);
    }

    public void addParticipants(AppDatabase db, Context context, List<User> newUsers){
                for (User user : newUsers) {
                    this.addParticipant(db, context, user,null);
                }
    }

    public void setCreator(@NonNull String creator) {
        this.creator = creator;
    }

    public static void addAlbumToDb(final AppDatabase db, final Album album, final Context context){


        //create Album on GoogleDrive Account and save the id as googleDriveFolderId
        DriveServiceHelper mDriveServiceHelper = DriveServiceHelper.getInstance();
        Task<String> task = mDriveServiceHelper.createFolder(album.getTitle())
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(final String googleDriveFolderId) {
                        Log.i(TAG, "Google DriveFolder ID: "+googleDriveFolderId);

                        Toast toast = Toast.makeText(context, "Album " + album.getTitle() + " created with success", Toast.LENGTH_SHORT);
                        toast.show();

                        if (album.getId() == null || album.getId() == 0){
                            RequestHandler handler = RequestHandler.getInstance();
                            handler.makeRequest(Server.IP +  "newalbum?owner="+album.getCreator()+"&albumname="+album.getTitle(), new RequestListener() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                    try {
                                        String res = new String(response, "utf8");
                                        Log.d(TAG, "" + res);
                                        HashMap<String, String> map = JsonParser.jsonToMap(res);
                                        String result = map.get("result");
                                        long id = Long.parseLong(result);
                                        album.setId(id);
                                        album.setGoogleIdFolder(googleDriveFolderId);
                                        createUrlFileInFolder(googleDriveFolderId, album,context);
                                    } catch (UnsupportedEncodingException | JSONException /*| JSONException*/ e) {
                                        e.printStackTrace();
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        }else{
                            album.setGoogleIdFolder(googleDriveFolderId);
                            createUrlFileInFolder(googleDriveFolderId, album,context);
                        }
                    }
                    //
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.i(TAG, "COULDN'T CREATE FOLDER")
                        ;
                        Toast toast = Toast.makeText(context, "ERROR: Album " + album.getTitle() + " not created", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
    }

    //creates empty text UrlsFile inside the folder with the given folderId
    public static Task<String> createUrlFileInFolder(final String folderId, final Album album, final Context ctx) {

        //instantiate SavePhoto just to get the mDriveService variable
        SavePhoto sp = SavePhoto.getInstance(ctx);
        final Drive mDriveService = sp.getmDriveService();

        return Tasks.call(mExecutor, new Callable<String>() {
            @Override
            public String call() throws Exception {
                AppDatabase db = AppDatabase.getAppDatabase(ctx);
                File metadata = new File()
                        .setParents(Collections.singletonList(folderId))
                        .setMimeType("text/plain")
                        .setName("UrlsFile");


                File googleFile = mDriveService.files().create(metadata).execute();
                if (googleFile == null) {
                    throw new IOException("Null result when requesting file creation.");
                }

                String fileId = googleFile.getId();
                Log.i(TAG,"Google Drive Url File ID "+fileId);
                album.setGoogleIdText(fileId);

                SavePhoto.insertPermission(mDriveService,googleFile.getId());
                com.google.api.services.drive.model.File fileD = mDriveService.files().get(fileId).setFields("webContentLink").execute();

                db.albumDao().insert(album);
                album.addParticipant(db, ctx, User.getSelfUser(), fileD.getWebContentLink());
                return googleFile.getId();
                //album has to have an attribute called albumUrlFileId
                //then, save googleFile.getId() to that variable in order to access that whenever we want to update the file ..
                // in the SavePhoto class (when we add a photo to an album)
            }
        });
    }



    public String getThumbnail(AppDatabase db){
        //TODO get thumbnail

        return "TODO";
    }
}

