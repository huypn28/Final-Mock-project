// IMyAidlInterface.aidl
package com.example.finalmockserver;

import com.example.finalmockserver.model.Message;
import com.example.finalmockserver.model.User;
import com.example.finalmockserver.model.RecentBox;
import com.example.finalmockserver.IUserStatusCallback;
import java.util.List;

interface IMyAidlInterface {
    List<Message> getMessagesForUser(int userId);
    Message getMessageById(int messageId);
    List<Message> getMessagesBetweenUsers(int senderId,int receiverId);
    List<RecentBox> getRecentBoxesForUser(int userId);
    User getUserById(int userId);
    List<User> getAllUsers();
    List<RecentBox> getAllRecentBox();
    List<Message> getAllMessage();
    int addUser(in User user);
    void updateMessage(in Message message);
    void addRecentBox(in RecentBox recentBox);
    void updateUserStatus(int userId, String status);
    void registerUserStatusCallback(IUserStatusCallback callback);
    void unregisterUserStatusCallback(IUserStatusCallback callback);
}