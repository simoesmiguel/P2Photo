package data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ParticipantDao {
    @Query("SELECT * FROM Participant")
    List<Participant> getAll();

    @Query("SELECT * FROM Participant WHERE album_id LIKE :albumId AND user_name LIKE :userName AND album_slice_link LIKE :albumSliceLink LIMIT 1")
    Participant findByKey(Long albumId, String userName, String albumSliceLink);

    @Query("SELECT album_slice_link FROM Participant WHERE album_id LIKE :albumId AND user_name LIKE :userName")
    String getLink(Long albumId, String userName);

    @Query("SELECT * FROM Participant WHERE album_slice_link LIKE :link LIMIT 1")
    Participant findByLink(String link);

    @Insert
    void insertAll(Participant... participant);

    @Delete
    void delete(Participant participant);

    @Query("DELETE FROM Participant")
    void clearAll();

    @Query("SELECT DISTINCT * FROM User WHERE name NOT IN " +
            "(SELECT DISTINCT user_name FROM Participant WHERE album_id LIKE :albumId)")
    List<User> getUsersNotParticipatingInAlbum(Long albumId);
}


