package pt.ulisboa.tecnico.cmov.p2photo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;

import data.Album;
import data.AppDatabase;
import data.Photo;
import data.User;
import googleDriveCostume.SavePhoto;
import photoListRecyclerView.PhotoListAdapter;

public class ViewAlbumPhotosActivity extends AppCompatActivity {
    // UI references.
    private RecyclerView photoListView;
    private FloatingActionButton addPhotosButton;
    private ActionMenuItemView addUsersButton;
    private Toolbar toolbar;
    private PhotoListAdapter photoListAdapter;

    public final static String EXTRA_ALBUM = "EXTRA_ALBUM";
    public static final int PICK_IMAGE = 1;
    private Intent resultData;


    private Album album;
    private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=1;
    private String TAG="ViewAlbumPhotosActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        album = (Album) getIntent().getSerializableExtra(EXTRA_ALBUM);

        setContentView(R.layout.activity_view_album_photos);
        toolbar = findViewById(R.id.toolbar);
        setToolbar(album);

        addPhotosButton = findViewById(R.id.button_add_photos);
        addPhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Add photo TODO", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                /*
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                */
                Intent intent=new Intent(Intent.ACTION_PICK);
                // Sets the type as image/*. This ensures only components of type image are selected
                intent.setType("image/*");
                //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
                String[] mimeTypes = {"image/jpeg", "image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                // Launching the Intent
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

        //TODO set album name in the toolbar top

        // Lookup the recyclerview in activity layout
        photoListView = findViewById(R.id.recyclerView_photo_list);

        // Create adapter passing in the sample user data
        photoListAdapter = new PhotoListAdapter(album, AppDatabase.getAppDatabase(getApplicationContext()));
        // Attach the adapter to the recyclerview to populate items
        photoListView.setAdapter(photoListAdapter);
        // Set layout manager to position the items
        photoListView.setLayoutManager(new LinearLayoutManager(this));

    }


    public void setResultData(Intent resultData){
        this.resultData = resultData;
    }

    public Intent getResultData(){
        return this.resultData;
    }


    public void parseAndSave(){

        Uri pickedImage = getResultData().getData();
        // Let's read picked image path using content resolver
        String[] filePath = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
        cursor.moveToFirst();
        String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath, options);

        cursor.close();

        //save photo to internal storage

        SavePhoto sp = SavePhoto.getInstance(getApplicationContext());
        String path = sp.saveToInternalStorage(imageBitmap);

        //falta guardar a foto neste album? na db?


        String a = album.getLink(AppDatabase.getAppDatabase(this.getBaseContext()));
        Log.i(TAG, "Google Drive folder link : "+a);
        Log.i(TAG,"Google Drive folder Id : "+album.getGoogleIdFolder());

        //saves the photo to the album with the given Id
        Photo photo = new Photo(User.getSelfUser(), album);
        sp.savePhotoToDrive(photo, this, path, album.getGoogleIdFolder(), album.getGoogleIdText(), album);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    parseAndSave();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case PICK_IMAGE:
                //Bundle extras = resultData.getExtras();
                //Bitmap imageBitmap = (Bitmap) extras.get("data");

                boolean alreadyGranted=false;
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {


                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.


                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }else{
                    alreadyGranted = true; //the user already granted the permission to read files from external storage

                }

                if(alreadyGranted){
                    setResultData(resultData);
                    parseAndSave();
                }
                else{
                    setResultData(resultData);
                }

        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }


    private void setToolbar(Album album){

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(album.getTitle());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_add_user:
                Intent intent = new Intent(getApplicationContext(), AlbumUserAddingActivity.class);
                intent.putExtra(AlbumUserAddingActivity.EXTRA_ALBUM, (Serializable) album);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_album_view, menu);
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        photoListAdapter.notifyDataSetChanged();
    }


    public void notifyNewPhoto(){
        try {
            photoListAdapter.notifyDataSetChanged();
        }catch (NullPointerException e){
           // "NO issue";
            ;
        }
    }
}
