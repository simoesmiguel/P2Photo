package photoListRecyclerView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import data.User;
import pt.ulisboa.tecnico.cmov.p2photo.R;

public class UserNameViewHolder extends RecyclerView.ViewHolder {

    //UI references
    private TextView userNameView;
    private TextView photoNumberView;

    public UserNameViewHolder(@NonNull View itemView) {
        super(itemView);
        this.userNameView = itemView.findViewById(R.id.text_user_name_tag);
        this.photoNumberView = itemView.findViewById(R.id.text_photo_number_indicator);
    }

    public void setViewHolder(String userName, Integer numberOfPhotos){
        setUserNameView(userTextBuilder(userName));
        setPhotoNumberView(photoTextBuilder(numberOfPhotos));
    }

    private void setUserNameView(String text) {
        this.userNameView.setText(text);
    }

    private void setPhotoNumberView(String text) {
        this.photoNumberView.setText(text);
    }

    private static String userTextBuilder(String userName){
        if (userName.equals(User.getSelfUser().getName()))
            return "Your photos";
        else
            return userName + "\'s photos";
    }

    private static String photoTextBuilder(Integer numberOfPhotos){
        return numberOfPhotos.toString() + " photos";
    }


}
