package userListRecyclerView;

import android.view.View;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import data.Album;
import data.AppDatabase;
import data.Participant;
import data.User;
import photoListRecyclerView.PhotoListAdapter;
import pt.ulisboa.tecnico.cmov.p2photo.R;

public class UserListController {

    //UI References
    private View contaneirView;
    private RecyclerView usersAddedView;
    private RecyclerView usersToAddView;

    //Adapters
    private UserListAddedAdapter usersAddedAdapter;
    private UserListToAddAdapter usersToAddAdapter;

    //Data
    private AppDatabase db;
    private Album album;
    private List<User> usersAdded;
    private List<User> usersToAdd;

    public UserListController(View contaneirView, Album album) {
        this.contaneirView = contaneirView;
        this.album = album;
        this.db = AppDatabase.getAppDatabase(contaneirView.getContext());

        setUserLists();
        setAdapters();
    }

    private void setUserLists(){
        usersAdded = album.getParticipants(db);
        usersToAdd = Participant.getUsersNotParticipatingInAlbum(db, album);
    }

    private void setAdapters(){

        // Lookup the recyclerview in activity layout
        usersAddedView = contaneirView.findViewById(R.id.recyclerView_added_users);
        usersToAddView = contaneirView.findViewById(R.id.recyclerView_users_to_add);

        // Create adapters
        usersAddedAdapter = new UserListAddedAdapter(this,usersAddedView, usersAdded);
        usersToAddAdapter = new UserListToAddAdapter(this,usersToAddView, usersToAdd);

        // Attach the adapter
        usersAddedView.setAdapter(usersAddedAdapter);
        usersToAddView.setAdapter(usersToAddAdapter);

        // Set layout manager to position the items
        usersAddedView.setLayoutManager(new LinearLayoutManager(contaneirView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        usersToAddView.setLayoutManager(new LinearLayoutManager(contaneirView.getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    //Add user
    protected void moveUser(User user){
        usersToAddAdapter.moveUser(user);
        usersAddedAdapter.moveUser(user);
    }

    //RemoveUser
    protected void moveUserBack(User user){
        usersToAddAdapter.moveUserBack(user);
        usersAddedAdapter.moveUserBack(user);
    }

    public List<User> getAddedUsers(){
        return usersToAddAdapter.movedUserList;
    }
}











