package pt.ulisboa.tecnico.cmov.p2photo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import albumCreation.AlbumCreationViewPagerAdapter;
import albumCreation.AlbumCreationViewPagerListener;
import data.Album;
import data.User;

public class AlbumCreationActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private AlbumCreationViewPagerAdapter viewAdapter;
    private AlbumCreationViewPagerListener viewPagerListener;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_creation);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbar();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.view_pager);

        Album album = new Album("New album", User.getSelfUser());
        viewAdapter = new AlbumCreationViewPagerAdapter(this, viewPager, album);
        viewPagerListener = new AlbumCreationViewPagerListener(viewAdapter);

        // adding bottom dots
        viewPager.setAdapter(viewAdapter);
        viewPager.addOnPageChangeListener(viewPagerListener);

    }

    private void setToolbar(){

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Album creation");

    }

}

