package com.beyondthehorizon.associates.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ChatModel.class, RecentChatModel.class, CommentsModel.class}, exportSchema = false, version = 6)
public abstract class AssociatesDatabase extends RoomDatabase {

    private static final String DB_NAME = "chats_db";
    private static AssociatesDatabase instance;

    public abstract ChatsDao chatsDao();
    public abstract LatestChatsDao latestChatsDao();
    public abstract CommentsDao commentsDao();

    public static synchronized AssociatesDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AssociatesDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
