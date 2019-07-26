package albumListRecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

import data.Album;
import data.AppDatabase;
import data.Participant;
import data.User;
import pt.ulisboa.tecnico.cmov.p2photo.R;
import pt.ulisboa.tecnico.cmov.p2photo.ViewAlbumPhotosActivity;


public class AlbumViewHolder extends RecyclerView.ViewHolder{

    // UI references.
    private TextView title_view;
    private TextView date_view;
    private TextView shared_with_view;
    private ImageView thumbnail_view;

    private View itemView;

    private static SimpleDateFormat calendarDayFormat = new SimpleDateFormat("dd/MM/yyyy");


    public AlbumViewHolder(@NonNull View itemView) {
        super(itemView);
       title_view = itemView.findViewById(R.id.text_album_title);
       date_view = itemView.findViewById(R.id.text_creation_date);
       shared_with_view = itemView.findViewById(R.id.text_shared_with);
       thumbnail_view = itemView.findViewById(R.id.album_thumbnail);
       this.itemView = itemView;
    }



    protected void setViewHolder(final Album album){
        setTitle_view(album.getTitle());
        setDate_view(calendarDayFormat.format(album.getCreationDate().getTime()));
        setShared_with_view(getSharedWithText(album));
        //TODO setImage()
        setThumbnail_view(album.getThumbnail(AppDatabase.getAppDatabase(itemView.getContext())));
        setClickListener_view(album);
    }


    private String getSharedWithText(final Album album){

        StringBuilder stringBuilder = new StringBuilder();

        //TODO change this String concatnation to acess from context and strings xml
        //initial text
        stringBuilder.append("Shared with: ");

        final List<User> participants= album.getParticipants(AppDatabase.getAppDatabase(itemView.getContext()));
        //add first element
        List<Participant> all = AppDatabase.getAppDatabase(itemView.getContext()).participantDao().getAll();
        stringBuilder.append(participants.get(0).getName());
        for (int i = 1; i < participants.size() - 1; i++) {
            stringBuilder.append(", ");
            stringBuilder.append(participants.get(i).getName());
        }

        //add last element
        if (participants.size() > 1){
            stringBuilder.append("and ");
            stringBuilder.append(participants.get(participants.size()-1).getName());
        }

        return stringBuilder.toString();
    }



    private void setTitle_view(final String title_text) {
        this.title_view.setText(title_text);
    }

    private void setDate_view(final String date_text) {
        this.date_view.setText(date_text);
    }

    private void setShared_with_view(final String shared_with_text) {
        this.shared_with_view.setText(shared_with_text);
    }

    private void setThumbnail_view(String imageLink) {
        //TODO setImage
        this.thumbnail_view.setImageResource(R.drawable.ic_photo_black_24dp);
    }

    private void setClickListener_view(final Album album){
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, ViewAlbumPhotosActivity.class);
                intent.putExtra(ViewAlbumPhotosActivity.EXTRA_ALBUM, (Serializable) album);
                context.startActivity(intent);
            }
        });

    }


}
