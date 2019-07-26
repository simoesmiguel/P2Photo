package userListRecyclerView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import data.User;

public class UserListAddedAdapter extends UserListAdapter {

    private final static int LOCKED_USER = UserViewHolder.NO_ICON;
    private final static int NEW_ADDED_USER = UserViewHolder.REMOVE_ICON;

    private List<User> movedUserList;

    public UserListAddedAdapter(UserListController userListController, RecyclerView view, List<User> baseUserList) {
        super(userListController, view, baseUserList);
        this.movedUserList = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < movedUserList.size()){
            return NEW_ADDED_USER;
        }else{
            return LOCKED_USER;
        }
    }

    @Override
    public int getItemCount() {
        return getBaseUserList().size() + movedUserList.size();
    }

    protected int getItemPosition(User user){
        int positionBase = movedUserList.indexOf(user);
        if (positionBase > -1)
            return positionBase;
        return movedUserList.size() + getBaseUserList().indexOf(user);
    }


    @Override
    protected User getItemOnPosition(int position) {
        //If the user already exited
        if (position < movedUserList.size()){
            return movedUserList.get(position);
        }else{
            return super.getItemOnPosition(position-movedUserList.size());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserViewHolder userViewHolder = (UserViewHolder) holder;
        userViewHolder.setViewHolder(getItemOnPosition(position), getItemViewType(position));
    }

    public void moveUser(User user){
        movedUserList.add(0,user);
        notifyItemInserted(0);
        layoutView.smoothScrollToPosition(0);
    }

    public void moveUserBack(User user){

        int position = getItemPosition(user);
        if (movedUserList.remove(user)) {
            notifyItemRemoved(position);
        }
        else{
            //TODO create costume expection
            throw new RuntimeException("User to be added back not on the moved list");
        }
    }
}
