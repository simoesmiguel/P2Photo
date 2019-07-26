package albumListRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import data.Album;
import pt.ulisboa.tecnico.cmov.p2photo.R;

public class AlbumListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int VIEW_TYPE_ALBUM = 0;
    private static final int VIEW_TYPE_ADD_BUTTON = 1;

    private List<Album> albumList;

    public AlbumListAdapter(List<Album> albumList) {
        this.albumList = albumList;
    }

    public void setAlbumList(List<Album> albumList) {
        this.albumList = albumList;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == albumList.size()) ? VIEW_TYPE_ADD_BUTTON : VIEW_TYPE_ALBUM;
    }

    @Override
    public int getItemCount() {
        return albumList.size() + 1;
    }

    private Album getItemOnPosition(final int position){
        return albumList.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        switch (viewType) {
                case VIEW_TYPE_ALBUM:
                    // Inflate the album custom layout
                    View albumView = inflater.inflate(R.layout.item_album_list, parent, false);
                    return new AlbumViewHolder(albumView);
                case VIEW_TYPE_ADD_BUTTON:
                    // Inflate the add button custom layout
                    View addButtonView = inflater.inflate(R.layout.item_album_list_add_button, parent, false);
                    return new AddButtonViewHolder(addButtonView);
                default: throw new RuntimeException();
            }
    }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            switch (holder.getItemViewType()) {
                case VIEW_TYPE_ALBUM:
                    AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;
                    albumViewHolder.setViewHolder(getItemOnPosition(position));
                    break;

                case VIEW_TYPE_ADD_BUTTON:
                    AddButtonViewHolder addButtonViewHolder = (AddButtonViewHolder) holder;
                    break;

                default: throw new RuntimeException();
            }
        }
    }


