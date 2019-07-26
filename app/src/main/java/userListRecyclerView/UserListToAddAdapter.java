package userListRecyclerView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import data.User;

public class UserListToAddAdapter extends UserListAdapter {
    private final static int AVAILABLE_USER = UserViewHolder.ADD_ICON;
    private final static int CHOOSEN_USER = 4;

    List<User> movedUserList;

    public UserListToAddAdapter(UserListController userListController, RecyclerView view, List<User> baseUserList) {
        super(userListController, view, baseUserList);
        this.movedUserList = new ArrayList<User>(){};
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    protected User getItemOnPosition(int position) {
        return super.getItemOnPosition(position);
    }

    protected int getItemPosition(User user){
        return getBaseUserList().indexOf(user);
    }

    @Override
    public int getItemViewType(int position) {
       return AVAILABLE_USER;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserViewHolder userViewHolder = (UserViewHolder) holder;
        userViewHolder.setViewHolder(getItemOnPosition(position), getItemViewType(position));
    }


    public void moveUser(User user){
        int position = getItemPosition(user);


        if (getBaseUserList().remove(user)){
            movedUserList.add(user);
            notifyItemRemoved(position);
        }
        else{
            //TODO create costume expection
            throw new RuntimeException("User to add not on list");
        }
    }

    public void moveUserBack(User user){
        if (movedUserList.remove(user)){
            getBaseUserList().add(0,user);
            notifyItemInserted(0);
            layoutView.smoothScrollToPosition(0);
        }
        else{
            //TODO create costume expection
            throw new RuntimeException("User to be added back not on the moved list");
        }
    }


}
