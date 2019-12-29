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
    @Query("SELECT * FROM comments_table WHERE parent_message_key=:message_key")
    LiveData<List<CommentsModel>> allCommentsForChat(String message_key);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertComment(CommentsModel... commentsModels);

    //Update delivery status
    @Query("UPDATE comments_table SET deliveryState = :delivery_status  WHERE message_key= :message_key")
    void updateDeliveryState(String message_key, String delivery_status);
}
