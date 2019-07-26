package photoListRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Album;
import data.AppDatabase;
import data.Photo;
import data.User;
import pt.ulisboa.tecnico.cmov.p2photo.R;

public class PhotoListAdapter extends RecyclerView.Adapter {

    private Album album;
    private ArrayList<Object> items;
    private ArrayList<Integer> itemTypes;
    private Map<String, Integer> usersNumberOfPhotos;


    // MACRO FOR VIEW TYPES
    private final static int VIEW_TYPE_USER_NAME = 0;
    private final static int VIEW_TYPE_PHOTOS = 1;

    public PhotoListAdapter(Album album, AppDatabase db) {
        this.album = album;
        parseInfoOfAlbumToList(db);
    }

    private void parseInfoOfAlbumToList(AppDatabase db){
        items = new ArrayList<>();
        itemTypes = new ArrayList<Integer>();
        usersNumberOfPhotos = new HashMap<>();

        List<User> users = album.getParticipantsExceptYou(db);
        users.add(0,User.getSelfUser());

        for (User user:
             users) {

            List<Photo> photos = album.getAllPhotosFromUser(db, user);

            //Add name
            items.add(user.getName());
            usersNumberOfPhotos.put(user.getName(), photos.size());
            itemTypes.add(VIEW_TYPE_USER_NAME);


            for (int i = 0; i < photos.size(); i += PhotoViewHolder.numberOfPhotosPerLine) {
                try {
                    items.add(photos.subList(i,i+3));
                }catch (IndexOutOfBoundsException exception) {
                    items.add(photos.subList(i,photos.size()));
                }
                itemTypes.add(VIEW_TYPE_PHOTOS);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return itemTypes.get(position);
    }

    @Override
    public int getItemCount() {
        return itemTypes.size();
    }

    private Object getItemOnPosition(final int position){
        return items.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case VIEW_TYPE_PHOTOS:
                // Inflate the line of photos
                View photoLineView = inflater.inflate(R.layout.item_photo_photo_list, parent, false);
                return new PhotoViewHolder(photoLineView);
            case VIEW_TYPE_USER_NAME:
                // Inflate the user name line
                View userNameView = inflater.inflate(R.layout.item_user_photo_list, parent, false);
                return new UserNameViewHolder(userNameView);
            default: throw new RuntimeException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_PHOTOS:
                PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
                photoViewHolder.setViewHolder( (List<Photo>) getItemOnPosition(position));
                break;

            case VIEW_TYPE_USER_NAME:
                UserNameViewHolder userNameViewHolder = (UserNameViewHolder) holder;
                String userName = (String) getItemOnPosition(position);
                userNameViewHolder.setViewHolder(userName, (Integer) usersNumberOfPhotos.get(userName));
                break;

            default: throw new RuntimeException();

        }
    }
}


