package data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PhotoDao {
    @Query("SELECT * FROM Photo")
    List<Photo> getAll();

    @Query("SELECT * FROM Photo WHERE user_name LIKE :user")
    Photo findByUser(String user);

    @Query("SELECT * FROM Photo WHERE album_id LIKE :album_id")
    Photo findByAlbum(long album_id);

    @Query("SELECT * FROM Photo WHERE link LIKE :link LIMIT 1")
    Photo findByLink(String link);


    @Insert
    void insertAll(Photo... photo);

    @Delete
    void delete(Photo photo);

    @Query("DELETE FROM Photo")
    void clearAll();
}
