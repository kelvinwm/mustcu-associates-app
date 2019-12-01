package com.beyondthehorizon.testfirebasefunctionstest.Repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.beyondthehorizon.testfirebasefunctionstest.ChatModel;
import com.beyondthehorizon.testfirebasefunctionstest.database.AssociatesDatabase;
import com.beyondthehorizon.testfirebasefunctionstest.database.ChatsDao;

import java.util.List;

public class ChatsRepository {
    LiveData<List<ChatModel>> allChats;
    private ChatsDao chatsDao;
    private static final String TAG = "Repository";

    public ChatsRepository(Application application) {
        AssociatesDatabase residenceDatabase = AssociatesDatabase.getInstance(application);
        chatsDao = residenceDatabase.chatsDao();
        allChats = chatsDao.allChats();
    }

    public void insertChat(ChatModel chatModel) {
        new InsertChatAsyncTask(chatsDao).execute(chatModel);
    }

//    public void insertResident(Resident resident) {
//        new InsertResidentAsyncTask(residentDao).execute(resident);
//    }

    //Get all chats from friend
    public LiveData<List<ChatModel>> allChatsFromFriend(String friendUID) {
        return chatsDao.allChatsFromFriend(friendUID);
    }

    //Get all Residents
    public LiveData<List<ChatModel>> getAllChats() {
        return allChats;
    }

//    //Get Specific visitors
//    public LiveData<List<Chat>> getSpecificVisitors(String status) {
//        return chatsDao.allSpecificVisitors(status);
//
//    }
//
//    //Get Specific visitorByID
//    public LiveData<List<Chat>> getSpecificVisitorById(String visitorId) {
//        return chatsDao.allSpecificVisitorById(visitorId);
//    }

//    //Get Specific residentByID
//    public LiveData<List<Resident>> getSpecificResidentById(String residentId) {
//        return residentDao.getSpecificResidentById(residentId);
//    }

//    //Delete all visitors
//    public void deleteAllVisitors() {
//        new DeleteAllVisitorsAsyncTask(chatsDao).execute();
//    }
//
//    //Delete all residents
//    public void deleteAllResidents() {
//        new DeleteAllResidentsAsyncTask(residentDao).execute();
//    }
//
//    //Get Specific residentByID
//    public void deleteSpecificVisitorById(String visitorId) {
//        new DeleteVisitorAsyncTask(chatsDao).execute(visitorId);
//    }

    // INSERT CHAT
    private static class InsertChatAsyncTask extends AsyncTask<ChatModel, Void, Void> {
        private ChatsDao chatsDao;

        private InsertChatAsyncTask(ChatsDao chatsDao) {
            this.chatsDao = chatsDao;
        }

        @Override
        protected Void doInBackground(ChatModel... chatModels) {
            Log.d(TAG, "doInBackground: "+chatModels[0].receiverUID);
            chatsDao.insertChat(chatModels[0]);
            return null;
        }
    }
//
//    //INSERT RESIDENT
//    private static class InsertResidentAsyncTask extends AsyncTask<Resident, Void, Void> {
//        private ResidentDao residentDao;
//
//        private InsertResidentAsyncTask(ResidentDao residentDao) {
//            this.residentDao = residentDao;
//        }
//
//        @Override
//        protected Void doInBackground(Resident... residents) {
//            residentDao.insertResident(residents);
//            return null;
//        }
//    }
//
//    //DELETE VISITOR
//    private static class DeleteVisitorAsyncTask extends AsyncTask<String, Void, Void> {
//        private ChatsDao chatsDao;
//
//        private DeleteVisitorAsyncTask(ChatsDao chatsDao) {
//            this.chatsDao = chatsDao;
//        }
//
//        @Override
//        protected Void doInBackground(String... params) {
//            String visitorId = params[0];
//            chatsDao.deleteSpecificVisitorById(visitorId);
//            return null;
//        }
//    }
//
//    //DELETE ALL VISITORS
//    private static class DeleteAllVisitorsAsyncTask extends AsyncTask<Void, Void, Void> {
//        private ChatsDao chatsDao;
//
//        private DeleteAllVisitorsAsyncTask(ChatsDao chatsDao) {
//            this.chatsDao = chatsDao;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            chatsDao.deleteAllVisitors();
//            return null;
//        }
//    }
//
//    //DELETE ALL RESIDENTS
//    private static class DeleteAllResidentsAsyncTask extends AsyncTask<Void, Void, Void> {
//        private ResidentDao residentDao;
//
//        private DeleteAllResidentsAsyncTask(ResidentDao residentDao) {
//            this.residentDao = residentDao;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            residentDao.deleteAllResidents();
//            return null;
//        }
//    }

}
