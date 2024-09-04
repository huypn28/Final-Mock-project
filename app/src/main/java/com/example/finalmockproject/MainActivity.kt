package com.example.finalmockproject

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.finalmockproject.ui.theme.FinalMockProjectTheme
import com.example.finalmockproject.viewmodel.ChatAppViewModel
import com.example.finalmockserver.IMyAidlInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: ChatAppViewModel by viewModels()
    private var aidlService: IMyAidlInterface? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            aidlService = IMyAidlInterface.Stub.asInterface(service)
            viewModel.setUserService(aidlService!!)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            aidlService = null
        }
    }

    @SuppressLint("LogNotTimber")
    private fun bindToChatAppService() {
        val intent = Intent().apply {
            component = ComponentName(
                "com.example.finalmockserver",
                "com.example.finalmockserver.service.ChatAppService"
            )
        }
        val isBound = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        if (!isBound) {
            Log.d("MainActivity", "Service binding failed")
        }
    }

    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        bindToChatAppService()

        setContent {
            FinalMockProjectTheme {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}