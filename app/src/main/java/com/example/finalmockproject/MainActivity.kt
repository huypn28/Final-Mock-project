package com.example.finalmockproject

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.compose.rememberNavController
import com.example.finalmockproject.notification.CreateReplyNotification
import com.example.finalmockproject.ui.theme.FinalMockProjectTheme
import com.example.finalmockproject.viewmodel.ChatAppViewModel
import com.example.finalmockserver.IMyAidlInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: ChatAppViewModel by viewModels()
    private var aidlService: IMyAidlInterface? = null
    private var hasInitializedMessageObserver = false
    private val displayedMessageIds = mutableSetOf<Int>()
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            showNotificationPermissionDialog()
        }
    }
    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val senderId = intent?.getIntExtra("senderId", -1) ?: return
            val receiverId = intent.getIntExtra("receiverId", -1)
            val message = intent.getStringExtra("message") ?: return

            viewModel.sendMessage(senderId, receiverId, message)
        }
    }
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            aidlService = IMyAidlInterface.Stub.asInterface(service)
            viewModel.setUserService(aidlService!!)
            viewModel.loadUsers()

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

        CreateReplyNotification.createNotificationChannel(applicationContext)

        checkAndRequestNotificationPermission()

        viewModel.userId.observe(this) { userId ->
            if (userId != null) {
                Log.d("MainActivity", "User ID: $userId")
                if (!hasInitializedMessageObserver) {
                    CreateReplyNotification.observeMessagesForNotifications(
                        this,
                        applicationContext,
                        viewModel,
                        userId,
                        displayedMessageIds // Pass the set of displayed message IDs
                    ) { userId -> viewModel.getUserById(userId)?.username ?: "Unknown" }
                    hasInitializedMessageObserver = true
                }
            } else {
                Log.d("MainActivity", "No user ID found in ViewModel")
            }
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
            messageReceiver,
            IntentFilter("SEND_MESSAGE_ACTION")
        )

        setContent {
            FinalMockProjectTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController, viewModel = viewModel)
            }
        }
    }
    @SuppressLint("ObsoleteSdkInt")
    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showNotificationPermissionDialog() {
        setContent {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Notification") },
                text = { Text("Notifications are currently disabled. Would you like to enable them?") },
                confirmButton = {
                    Button(onClick = {
                        openNotificationSettings()
                    }) {
                        Text("Enable Notifications")
                    }
                },
                dismissButton = {
                    Button(onClick = { }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    private fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        viewModel.updateUserStatus(viewModel.currentUserId, "Offline")
    }
    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}