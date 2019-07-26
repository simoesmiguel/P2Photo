package pt.ulisboa.tecnico.cmov.p2photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import googleDriveCostume.SavePhoto;

public class TakePhotoActivity extends AppCompatActivity {


    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView imView;
    Button send;

    private SavePhoto sp;
    private String savedDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        imView = findViewById(R.id.imageView);
        dispatchTakePictureIntent();

        send = findViewById(R.id.send_to_drive);
        sp = SavePhoto.getInstance(getApplicationContext());

//TODO
        //send.setOnClickListener(new View.OnClickListener() {
        //    @Override
       //     public void onClick(View v) {
       //         sp.savePhotoToDrive(sp.getAbsolutePathh(), null);
        //    }
        //});

        Gson gson = new Gson();

        //String a = getIntent().getStringExtra("driveService");
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imView.setImageBitmap(imageBitmap);

            sp.saveToInternalStorage(imageBitmap);

        }
    }

}

