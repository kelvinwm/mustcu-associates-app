package com.beyondthehorizon.associates.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LatestChatsDao {
    //GET ALL Chats
    @Query("SELECT * FROM latest_chats")
    LiveData<List<RecentChatModel>> allLatestChats();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLatestChat(RecentChatModel... recentChatModels);
}
