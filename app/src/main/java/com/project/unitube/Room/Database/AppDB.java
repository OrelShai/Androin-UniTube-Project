package com.project.unitube.Room.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.project.unitube.Room.Dao.CommentDao;
import com.project.unitube.Room.Dao.UserDao;
import com.project.unitube.Room.Dao.VideoDao;
import com.project.unitube.entities.Comment;
import com.project.unitube.entities.User;
import com.project.unitube.entities.Video;

/**
 * AppDB is the main database class for the application.
 * It integrates all DAOs and connects them to the ROOM database.
 */
@Database(entities = {Video.class, Comment.class, User.class}, version = 1)
public abstract class AppDB extends RoomDatabase {

    // Singleton instance of the AppDB
    private static volatile AppDB INSTANCE;

    // Abstract methods to get the DAOs
    public abstract UserDao userDao();
    public abstract VideoDao videoDao();
    public abstract CommentDao commentDao();

    /**
     * Returns the singleton instance of the AppDB.
     * If the instance is null, it initializes it.
     *
     * @param context The application context
     * @return The singleton instance of the AppDB
     */
    public static AppDB getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDB.class, "appDB")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

