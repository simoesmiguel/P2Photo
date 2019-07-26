package albumListRecyclerView;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import pt.ulisboa.tecnico.cmov.p2photo.AlbumCreationActivity;

public class AddButtonViewHolder extends RecyclerView.ViewHolder{

    public AddButtonViewHolder(@NonNull View itemView) {
        super(itemView);
        setClickListener_view();
    }

    private void setClickListener_view(){
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, AlbumCreationActivity.class);
                context.startActivity(intent);
            }
        });
    }

}
