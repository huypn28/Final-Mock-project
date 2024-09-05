// IMessageReceivedCallback.aidl
package com.example.finalmockserver;

import com.example.finalmockserver.model.Message;

interface IMessageReceivedCallback {
    void onMessageReceived(in Message message);
}