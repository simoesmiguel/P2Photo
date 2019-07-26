package pt.ulisboa.tecnico.cmov.p2photo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import android.widget.TextView;

import data.Photo;

public class ViewPhotoActivity extends AppCompatActivity {

    //UI references
    ImageView photo_image;
    TextView userOwner_text;
    TextView date_text;

    public final static String EXTRA_PHOTO = "EXTRA_PHOTO";
    private Photo photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        photo = (Photo) getIntent().getSerializableExtra(EXTRA_PHOTO);

        userOwner_text = findViewById(R.id.text_photo_owner);
        date_text = findViewById(R.id.text_date);
    }

}
