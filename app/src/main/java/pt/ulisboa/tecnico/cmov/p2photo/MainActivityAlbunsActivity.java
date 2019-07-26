package pt.ulisboa.tecnico.cmov.p2photo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import albumListRecyclerView.AlbumListAdapter;
import data.Album;
import data.AppDatabase;
import serverConnection.Server;


//@F7

public class MainActivityAlbunsActivity extends AppCompatActivity {

    //TODO maybe add a floating button when the last element is not visible

    //UI references
    RecyclerView album_list_view;
    private Toolbar toolbar;
    TextView warning_no_albuns_view;
    private AlbumListAdapter adapterAlbumList;
    private List<Album> albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_albuns);

        toolbar = findViewById(R.id.toolbar);
        setToolbar();

        // Lookup the recyclerview in activity layout
        album_list_view = findViewById(R.id.recyclerView_album_list);

        // Get Albuns

        albumList = AppDatabase.getAppDatabase(getApplicationContext()).albumDao().getAll();

        // Create adapter passing in the sample user data
        adapterAlbumList = new AlbumListAdapter(albumList);
        // Attach the adapter to the recyclerview to populate items
        album_list_view.setAdapter(adapterAlbumList);
        // Set layout manager to position the items
        album_list_view.setLayoutManager(new LinearLayoutManager(this));
    }


    private void setToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(getString(R.string.appName));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_update:
                Server.updateAll(this.getApplicationContext());
                return true;
            case R.id.button_clear:
                AppDatabase.clearAll(AppDatabase.getAppDatabase(this.getApplicationContext()));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main_view, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        albumList = AppDatabase.getAppDatabase(getApplicationContext()).albumDao().getAll();
        adapterAlbumList.setAlbumList(albumList);
        adapterAlbumList.notifyDataSetChanged();
    }
}
