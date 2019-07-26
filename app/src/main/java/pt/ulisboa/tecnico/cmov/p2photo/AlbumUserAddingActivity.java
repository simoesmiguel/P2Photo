package pt.ulisboa.tecnico.cmov.p2photo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import data.Album;
import data.AppDatabase;
import data.User;
import userListRecyclerView.UserListController;

public class AlbumUserAddingActivity extends AppCompatActivity {

    //UI References
    private Toolbar toolbar;
    private View userListsContainer;
    private Button confirmButton;
    private Button cancelButton;


    public final static String EXTRA_ALBUM = "EXTRA_ALBUM";

    private Album album;
    private  UserListController userListController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        album = (Album) getIntent().getSerializableExtra(EXTRA_ALBUM);

        setContentView(R.layout.activity_album_user_adding);
        toolbar = findViewById(R.id.toolbar);
        setToolbar(album);
        userListsContainer = findViewById(R.id.user_adding_lists);
        confirmButton = findViewById(R.id.button_confirm);
        cancelButton = findViewById(R.id.button_cancel);

        userListController = new UserListController(userListsContainer, album);
        setConfirmButton();
        setCancelButton();
    }

    private void setToolbar(Album album){

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(album.getTitle() + " Users");

    }

    private void setConfirmButton(){
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<User> newUsers= userListController.getAddedUsers();
                album.addParticipants(AppDatabase.getAppDatabase(getApplicationContext()), getApplicationContext(), newUsers);
                finish();
            }
        });
    }

    private void setCancelButton(){
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}