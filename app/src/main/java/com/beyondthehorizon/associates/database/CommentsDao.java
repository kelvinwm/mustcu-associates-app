package com.beyondthehorizon.associates.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CommentsDao {

    //GET Specific chats by senderUID
    @Query("SELECT * FROM comments_table WHERE message_key=:message_key")
    LiveData<List<CommentsModel>> allCommentsForChat(String message_key);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertComment(CommentsModel... commentsModels);

//    //GET ALL Specific VISITOR BY ID
//    @Query("DELETE FROM chats_table WHERE visitorId=:visitorId")
//    void deleteSpecificVisitorById(String visitorId);
//
//    @Query("DELETE FROM chats_table")
//    void deleteAllVisitors();
}
