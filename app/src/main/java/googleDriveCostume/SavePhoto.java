package googleDriveCostume;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import data.Album;
import data.AppDatabase;
import data.Photo;
import data.User;
import pt.ulisboa.tecnico.cmov.p2photo.ViewAlbumPhotosActivity;



public class SavePhoto {

    Drive mDriveService;
    private final Executor mExecutor = Executors.newSingleThreadExecutor();

    private static String TAG= "SavePhoto";
    private static SavePhoto single_instance = null;
    private String absolutePath;

    private Context c;
    public SavePhoto(Context c){
        this.c = c;
    }

    public static SavePhoto getInstance(Context c)
    {
        if (single_instance == null)
            single_instance = new SavePhoto(c);

        return single_instance;
    }


    public String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(c);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Log.i(TAG,"image Saved to Internal Storage");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        absolutePath = directory.getAbsolutePath();
        return absolutePath;
    }


    //sends the photo which is in the @savedDirectory local directory to the user's drive cloud
    public void savePhotoToDrive(final Photo photo, final ViewAlbumPhotosActivity viewAlbumPhotosActivity, final String savedDirectory, final String folderId, final String googleIdText, final Album album) {

        Tasks.call(mExecutor, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Log.i(TAG,"Google drive folder Id "+folderId);

                com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();

                fileMetadata.setName("olaa.jpg");
                fileMetadata.setParents(Collections.singletonList(folderId));

                File filePath = new File(savedDirectory + "/profile.jpg");

                FileContent mediaContent = new FileContent("image/jpeg", filePath);
                com.google.api.services.drive.model.File file = null;

                try {

                    file = mDriveService.files().create(fileMetadata, mediaContent)

                            .setFields("id, parents")
                            .execute();

                    Log.i(TAG,"Photo File ID: " + file.getId());
                    Log.i(TAG,"Image sent to Drive");

                    Permission permission = insertPermission(mDriveService,file.getId());
                    String fileId = file.getId();
                    com.google.api.services.drive.model.File fileD = mDriveService.files().get(fileId).setFields("webContentLink").execute();


                    photo.setLink(fileD.getWebContentLink());
                    photo.addToDb(AppDatabase.getAppDatabase(viewAlbumPhotosActivity.getBaseContext()), viewAlbumPhotosActivity);

                    updateUrlFile(viewAlbumPhotosActivity.getBaseContext(), fileD.getWebContentLink(), googleIdText, folderId, album);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        });}

        public static Permission insertPermission(Drive service, String fileId) {
            Permission newPermission = new Permission();

            newPermission.setType("anyone");
            newPermission.setRole("reader");
            try {
                return service.permissions().create(fileId, newPermission).execute();
            } catch (IOException e) {
                System.out.println("An error occurred: " + e);
            }
            return null;

    }



    public void setDriveService(Drive mDriveService){
        this.mDriveService = mDriveService;
    }

    public Drive getmDriveService(){ return this.mDriveService; }

    public String getAbsolutePathh(){
        return this.absolutePath;
    }


    public void updateUrlFile(final Context c, final String link, final String googleIdText, final String parentFolderId, final Album album){

            Log.d(TAG, "Reading file .. " + googleIdText);

            // first we got to read the file in order to save its content in a variable. (content)
            readFile(googleIdText)
                    .addOnSuccessListener(new OnSuccessListener<Pair<String, String>>() {
                        @Override
                        public void onSuccess(Pair<String, String> nameAndContent) {
                            String name = nameAndContent.first; // name = "UrlsFileId"
                            String content = nameAndContent.second;

                            // If readFile is done with success, then append the _link_ to the previous content and update the file
                            final String new_content = content+link+"  ";

                            Log.i(TAG,"old_content: "+content);
                            Log.i(TAG,"new_content: "+new_content);


                            /*
                            updateFile(googleIdText, name, new_content)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Log.e(TAG, "Unable to save file via REST.", exception);
                                        }
                                    });
                            */

                           // updateFile(googleIdText,c, new_content);

                            deleteFile(googleIdText, parentFolderId, new_content, album);


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e(TAG, "Couldn't read file.", exception);
                        }
                    });
    }


    /**
     * Updates the file identified by {@code fileId} with the given {@code name} and {@code
     * content}.
     */
    public Task<Void> updateFile(final String fileId, final String name, final String content) {
        return Tasks.call(mExecutor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Create a File containing any metadata changes.
                com.google.api.services.drive.model.File metadata = new com.google.api.services.drive.model.File()
                        .setName(name);

                // Convert content to an AbstractInputStreamContent instance.
                ByteArrayContent contentStream = ByteArrayContent.fromString("text/plain", content);

                // Update the metadata and contents.
                mDriveService.files().update(fileId, metadata, contentStream).execute();
                return null;
            }
        });
    }


    private void updateFile(final String fileId, final Context c, final String newContent) {
        Tasks.call(mExecutor, new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                try {
                    // First retrieve the file from the API.
                    com.google.api.services.drive.model.File file = mDriveService.files().get(fileId).execute();

                    // File's new metadata.
                    file.setMimeType("text/plain");

                    String path = writeFileOnInternalStorage(c,"urlsFile",newContent);

                    // File's new content.
                    java.io.File fileContent = new java.io.File(path);
                    FileContent mediaContent = new FileContent("text/plain", fileContent);

                    // Send the request to the API.
                    com.google.api.services.drive.model.File updatedFile = mDriveService.files().update(fileId, file, mediaContent).execute();

                } catch (IOException e) {
                    System.out.println("An error occurred: " + e);
                }
                return null;
            }
        });
    }

    public void deleteFile(final String fileId, final String parentFolderId,
                           final String new_content, final Album album){

        Tasks.call(mExecutor, new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                try {
                    mDriveService.files().delete(fileId).execute();
                } catch (IOException e) {
                    System.out.println("An error occurred: " + e);
                }

                uploadNewFile(fileId, parentFolderId, new_content, album);

                return null;
            }
        });
    }


    public void  uploadNewFile(final String fileid, final String parentFolderId, String newContent, final Album album){

        final String path = writeFileOnInternalStorage(c,"urlsFile",newContent);

        Tasks.call(mExecutor, new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {

                        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();

                        fileMetadata.setName("urlsFile.jpg");
                        fileMetadata.setParents(Collections.singletonList(parentFolderId));

                        File filePath = new File(path);

                        FileContent mediaContent = new FileContent("text/plain", filePath);
                        com.google.api.services.drive.model.File file = null;

                        try {

                            file = mDriveService.files().create(fileMetadata, mediaContent)
                                    .setFields("id, parents")
                                    .execute();

                            // Log.i(TAG, "Photo File ID: " + file.getId()); // fazer update deste novo ID
                            Log.i(TAG, "Updated File sent to Drive with Id : " + file.getId());
                            album.setGoogleIdText(file.getId());

                        } catch (Exception e) {
                            System.out.println("An error occurred: " + e);

                        }
                    return null;
                    }
             });
    }


    /**
     * Opens the file identified by {@code fileId} and returns a {@link Pair} of its name and
     * contents.
     */
    public Task<Pair<String, String>> readFile(final String fileId) {
        return Tasks.call(mExecutor, new Callable<Pair<String, String>>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public Pair<String, String> call() throws Exception {
                // Retrieve the metadata as a File object.
                com.google.api.services.drive.model.File metadata = mDriveService.files().get(fileId).execute();
                String name = metadata.getName();

                // Stream the file contents to a String.
                try (InputStream is = mDriveService.files().get(fileId).executeMediaAsInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    String contents = stringBuilder.toString();
                    return Pair.create(name, contents);
                }
            }
        });
    }



    public String writeFileOnInternalStorage(Context mcoContext,String sFileName, String sBody){

        File file = new File(mcoContext.getFilesDir(),"mydir");

        String path="";
        if(!file.exists()){
            file.mkdir();
        }

        try{
            File gpxfile = new File(file, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();

            path= gpxfile.getAbsolutePath();

        }catch (Exception e){
            e.printStackTrace();

        }

        return path;
    }



    public void loadPhotosFromFileToDb(Drive service, final Context context, String UrlsFileID, final User ownerOfSlice, final Album album){
        //TODO download do file somehow
        //Input stream somehow
        // first we got to read the file in order to save its content in a variable. (content)
        readFile(UrlsFileID)
                .addOnSuccessListener(new OnSuccessListener<Pair<String, String>>() {
                    @Override
                    public void onSuccess(Pair<String, String> nameAndContent) {
                        String name = nameAndContent.first; // name = "UrlsFileId"
                        String content = nameAndContent.second;

                        String[] all_links = content.split("  ");


                        AppDatabase db = AppDatabase.getAppDatabase(context);
                        for (String link : all_links) {
                            Photo photo = new Photo(ownerOfSlice, album, link);
                            if (db.photoDao().findByLink(link) == null){
                                db.photoDao().insertAll(photo);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "Couldn't read file.", exception);
                    }
                });

    }

}

//TODO esta aqui a toa mas e preciso fazer um botao de update para participantes e albuns e assim