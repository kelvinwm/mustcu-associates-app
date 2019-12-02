package com.beyondthehorizon.testfirebasefunctionstest.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.beyondthehorizon.testfirebasefunctionstest.ChatModel;

import java.util.List;

@Dao
public interface ChatsDao {


    //GET Specific chats by senderUID
    @Query("SELECT * FROM chats_table WHERE senderUID=:senderUID")
    LiveData<List<ChatModel>> allChatsFromFriend(String senderUID);

//    //GET ALL Specific VISITOR BY ID
//    @Query("SELECT * FROM chats_table WHERE visitorId=:visitorId")
//    LiveData<List<ChatModel>> allSpecificVisitorById(String visitorId);
//
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChat(ChatModel... chatModels);

//
//    //GET ALL Specific VISITOR BY ID
//    @Query("DELETE FROM chats_table WHERE visitorId=:visitorId")
//    void deleteSpecificVisitorById(String visitorId);
//
//    @Query("DELETE FROM chats_table")
//    void deleteAllVisitors();
}
