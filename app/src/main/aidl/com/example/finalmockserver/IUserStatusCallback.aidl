// IUserStatusCallback.aidl
package com.example.finalmockserver;

interface IUserStatusCallback {
    void onUserStatusChanged(int userId, String status);
}