package com.beyondthehorizon.associates.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.beyondthehorizon.associates.database.ChatModel;
import com.beyondthehorizon.associates.database.CommentsModel;
import com.beyondthehorizon.associates.database.RecentChatModel;
import com.beyondthehorizon.associates.repositories.ChatsRepository;

import java.util.List;

public class ChatsViewModel extends AndroidViewModel {
    private ChatsRepository chatsRepository;

    public ChatsViewModel(@NonNull Application application) {
        super(application);
        chatsRepository = new ChatsRepository(application);
    }

    public LiveData<List<RecentChatModel>> getAllLatestChats() {
        return chatsRepository.getAllLatestChats();
    }

    public LiveData<List<ChatModel>> getFriendChats(String friendUID) {
        return chatsRepository.allChatsFromFriend(friendUID);
    }

    public LiveData<List<CommentsModel>> getQuestionComments(String message_key) {
        return chatsRepository.allChatsForQuestion(message_key);
    }

//    public LiveData<List<Visitor>> getSpecificVisitorById(String visitorId) {
//        return chatsRepository.getSpecificVisitorById(visitorId);
//    }
//
//    public LiveData<List<Resident>> getSpecificResidentById(String residentId) {
//        return chatsRepository.getSpecificResidentById(residentId);
//    }

    public void insertChat(ChatModel chatModel) {
        chatsRepository.insertChat(chatModel);
    }

    public void insertCommentt(CommentsModel commentsModel) {
        chatsRepository.insertComment(commentsModel);
    }

    public void insertLatestChat(RecentChatModel recentChatModel) {
        chatsRepository.insertLatestChat(recentChatModel);
    }

    public void updateDeliveryStatus(String message_key, String status) {
        chatsRepository.updateDeliveryStatus(message_key, status);
    }

//    public void insertResident(Resident resident) {
//        chatsRepository.insertResident(resident);
//    }
//
//    public void deleteVisitor(String visitorId) {
//        chatsRepository.deleteSpecificVisitorById(visitorId);
//    }
//
//    public void deleteAllVisitors() {
//        chatsRepository.deleteAllVisitors();
//    }
//
//    public void deleteAllResidents() {
//        chatsRepository.deleteAllResidents();
//    }

}