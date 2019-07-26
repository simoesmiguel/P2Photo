package userListRecyclerView;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import data.User;
import pt.ulisboa.tecnico.cmov.p2photo.R;

public abstract class UserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<User> baseUserList;
    protected UserListController userListController;
    protected RecyclerView layoutView;

    UserListAdapter(UserListController userListController, RecyclerView layoutView, List<User> userList) {
        this.baseUserList = userList;
        this.userListController = userListController;
        this.layoutView = layoutView;
    }

    protected List<User> getBaseUserList() {
        return baseUserList;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return baseUserList.size();
    }

    protected User getItemOnPosition(final int position){
        return baseUserList.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the album custom layout
        View userView = inflater.inflate(R.layout.item_user_list, parent, false);
        return new UserViewHolder(userView, userListController);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
          UserViewHolder userViewHolder = (UserViewHolder) holder;
          userViewHolder.setViewHolder(getItemOnPosition(position), UserViewHolder.NO_ICON);
    }

    public abstract void moveUser(User user);
    public abstract void moveUserBack(User user);

}

