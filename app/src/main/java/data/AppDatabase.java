package data;

import android.content.Context;
import android.os.Handler;

import androidx.room.Database;
import androidx.room.Ignore;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import serverConnection.Server;

@Database(entities = {User.class, Photo.class, Participant.class, Album.class}, version = 13, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract AlbumDao albumDao();
    public abstract PhotoDao photoDao();
    public abstract ParticipantDao participantDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "database")
                            // allow queries on the main thread.
                            // Don't do this on a real app ! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
        }

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }



    @Ignore
    public static void clearAll(AppDatabase db){
        db.userDao().clearAll();
    }

    @Ignore
    public static void populateWithTestData(final AppDatabase db, final Context context) {

        Server.getAllUsers(context, "E");

        final User user = new User("Ruesga", context);
        final User user2 = new User("Nickevic", context);
        final User user3 = new User("Asanin", context);
        final User user4 = new User("Chiuva", context);

        db.userDao().insertAll(user, user2, user3, user4);
        User.setSelfUser(user);

        final Album album = new Album("Ferias Cacem", user);
        Album.addAlbumToDb(db, album, context);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Object o = db.albumDao().getAll();
                album.addParticipant(db, context, user3,null);
                album.addParticipant(db, context, user2, null);

                Photo photo1 = new Photo(user, album, "null");
                Photo photo2 = new Photo(user, album, "null");
                Photo photo3 = new Photo(user, album, "null");

                Photo photo4 = new Photo(user2, album, "null");
                Photo photo5 = new Photo(user2, album, "null");
                Photo photo6 = new Photo(user2, album, "null");
                Photo photo7 = new Photo(user2, album, "null");

                db.photoDao().insertAll(photo1,photo2,photo3,photo4,photo5,photo6,photo6,photo7);
            }
        }, 5000);


    }
}
