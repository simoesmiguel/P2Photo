package userListRecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

import androidx.core.content.res.ResourcesCompat;

import data.Album;
import data.User;
import pt.ulisboa.tecnico.cmov.p2photo.R;
import pt.ulisboa.tecnico.cmov.p2photo.ViewAlbumPhotosActivity;

public class UserViewHolder extends RecyclerView.ViewHolder{

    //Specific macro for adding remobing or none icons
    final protected static int ADD_ICON = 0;
    final protected static int REMOVE_ICON = 1;
    final protected static int NO_ICON = 3;


    //Reference Parent
    private UserListController userListController;

    // UI references.
    private TextView user_name_view;
    private ImageView user_image_view;
    private ImageView add_remove_icon_view;

    //data
    private User user;


    //TODO set onclickListener for adding or removing users always refreshing the other recycler
    public UserViewHolder(@NonNull View itemView, UserListController userListController) {
        super(itemView);
        this.userListController = userListController;
        user_name_view = itemView.findViewById(R.id.text_user_name);
        user_image_view = itemView.findViewById(R.id.image_user_photo);
        add_remove_icon_view = itemView.findViewById(R.id.image_icon_add_remove);
    }


    protected void setViewHolder(final User user, final int icon) {
        this.user = user;
        setName_view(user.getName());
        setIcon_view(icon);
        setImage_view();
        setClickListener_view(icon);
    }


    private void setName_view(final String user_text) {
        this.user_name_view.setText(user_text);
    }

    private void setImage_view() {
        user_image_view.setImageDrawable(ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.ic_account_circle_black_24dp,  null));
        user_image_view.setColorFilter(user.getColor());
        int a= Color.red(user.getColor());
        int v=Color.green(user.getColor());
        int f=Color.blue(user.getColor());
    }

    private void setIcon_view(final int iconID) {
        switch (iconID){
            case ADD_ICON:
                add_remove_icon_view.setVisibility(View.VISIBLE);
                add_remove_icon_view.setBackgroundDrawable(ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.circle_green, null));
                add_remove_icon_view.setImageDrawable(ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.ic_add, null));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    add_remove_icon_view.setImageTintList(ResourcesCompat.getColorStateList(itemView.getResources(),R.color.colorWhiteSh1, null));
                }
                break;

                case REMOVE_ICON:
                add_remove_icon_view.setVisibility(View.VISIBLE);
                add_remove_icon_view.setBackgroundDrawable(ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.circle_red, null));
                add_remove_icon_view.setImageDrawable(ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.ic_remove, null));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    add_remove_icon_view.setImageTintList(ResourcesCompat.getColorStateList(itemView.getResources(),R.color.colorWhiteSh1, null));
                }
                break;
            case NO_ICON:
                add_remove_icon_view.setVisibility(View.INVISIBLE);
        }
    }

    private void setClickListener_view(final int icon){
        switch (icon){
            case ADD_ICON:
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userListController.moveUser(user);
                    }
                });
                break;

            case REMOVE_ICON:
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userListController.moveUserBack(user);
                    }
                });
                break;
            case NO_ICON:
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //empty
                        ;
                    }
                });

        }
    }
}
