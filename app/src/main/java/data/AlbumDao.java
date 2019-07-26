package data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AlbumDao {
    @Query("SELECT * FROM Album")
    List<Album> getAll();

    @Query("SELECT * FROM Album WHERE id LIKE :album_id LIMIT 1")
    Album findById(long album_id);

    @Query("SELECT DISTINCT * FROM User WHERE name IN (SELECT user_name FROM Participant Where album_id == :album_id)")
    List<User> findUsersOfAlbum(long album_id);

    @Query("SELECT * FROM Photo WHERE album_id LIKE :album_id")
    List<Photo> findPhotosOfAlbum(long album_id);

    @Query("SELECT * FROM Photo WHERE album_id LIKE :album_id AND user_name LIKE :userName")
    List<Photo> findPhotosOfAlbumOfUser(long album_id, String userName);

    @Insert
    long insert(Album album);

    @Delete
    void delete(Album album);

    @Query("DELETE FROM Album")
    void clearAll();
}
