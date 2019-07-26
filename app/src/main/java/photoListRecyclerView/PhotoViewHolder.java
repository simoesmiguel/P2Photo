package photoListRecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import data.Photo;
import pt.ulisboa.tecnico.cmov.p2photo.R;
import pt.ulisboa.tecnico.cmov.p2photo.ViewPhotoActivity;

public class PhotoViewHolder extends RecyclerView.ViewHolder {

    protected static int numberOfPhotosPerLine = 3;


    //UI references
    private ImageView photoLeft;
    private ImageView photoCenter;
    private ImageView photoRight;

    protected PhotoViewHolder(@NonNull View itemView) {
        super(itemView);
        photoLeft = itemView.findViewById(R.id.image_left);
        photoCenter = itemView.findViewById(R.id.image_center);
        photoRight = itemView.findViewById(R.id.image_right);
    }

    protected void setViewHolder(List<Photo> photoList){
        setAllPhotos(photoList);
    }

    private void setAllPhotos(List<Photo> photoList){
        if (photoList.size() > 3 || photoList.size() < 1) {
            throw new RuntimeException("PHOTO LIMIT 3");
            //TODO create costum exception for wrong number of photos
        }

        if (photoList.size() == 1) {
            setPhoto(this.photoLeft, photoList.get(0));
            setPhoto(this.photoCenter, null);
            setPhoto(this.photoRight, null);
        }
        else if (photoList.size() == 2){
            setPhoto(this.photoLeft, photoList.get(0));
            setPhoto(this.photoCenter, photoList.get(1));
            setPhoto(this.photoRight, null);
        }else{
            setPhoto(this.photoLeft, photoList.get(0));
            setPhoto(this.photoCenter, photoList.get(1));
            setPhoto(this.photoRight, photoList.get(2));
        }

    }

    private void setPhoto(ImageView photoView, Photo photo) {
        photoView.setImageResource(R.drawable.ic_photo_black_24dp);
        if (photo != null) {
            new DownloadImageTask(photoView)
                    .execute(photo.getLink());
        }
        boolean visibility = photo != null;
        togglePhotoIsVisibility(photoView, visibility);
        setClickListenerPhoto(photoView, photo);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    private void togglePhotoIsVisibility(View photoView, boolean T_on__F_off){
        if (T_on__F_off){
            photoView.setVisibility(View.VISIBLE);
        }else{
            photoView.setVisibility(View.INVISIBLE);
        }
    }

    private void setClickListenerPhoto(View photoView, final Photo photo){
        if (photo == null){
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ;
                }
            });

        }else {

            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = itemView.getContext();
                    Intent intent = new Intent(context, ViewPhotoActivity.class);
                    intent.putExtra(ViewPhotoActivity.EXTRA_PHOTO , (Serializable) photo);
                    context.startActivity(intent);
                }
            });

        }
    }

    //TODO 5 photos not appearing
}
