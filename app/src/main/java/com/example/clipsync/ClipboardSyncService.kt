package com.example.clipsync

import android.app.*
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicLong

class ClipboardSyncService : Service(), ClipboardManager.OnPrimaryClipChangedListener {

    private lateinit var cm: ClipboardManager
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val lastAppliedRev = AtomicLong(0)
    private var lastPushedHash: String = ""

    override fun onCreate() {
        super.onCreate()
        startForegroundWithNotification()
        cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.addPrimaryClipChangedListener(this)
        scope.launch { pollWindowsLoop() }
    }

    private fun startForegroundWithNotification() {
        val chId = "clip_sync"
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            val ch = NotificationChannel(chId, "Clipboard Sync", NotificationManager.IMPORTANCE_LOW)
            nm.createNotificationChannel(ch)
        }
        val pi = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        val n = Notification.Builder(this, chId)
            .setContentTitle("Clipboard Sync")
            .setContentText("در حال همگام‌سازی کلیپ‌بورد")
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setContentIntent(pi)
            .build()
        startForeground(42, n)
    }

    override fun onPrimaryClipChanged() {
        val base = Prefs.serverBaseUrl(this)
        val token = Prefs.token(this)
        val text = (cm.primaryClip?.getItemAt(0)?.coerceToText(this) ?: "").toString()
        val hash = text.hashCode().toString()
        if (hash == lastPushedHash) return
        scope.launch {
            runCatching { NetworkClient.postClip(base, text, token) }.onSuccess {
                if (it) lastPushedHash = hash
            }
        }
    }

    private suspend fun pollWindowsLoop() {
        val base = Prefs.serverBaseUrl(this@ClipboardSyncService)
        val token = Prefs.token(this@ClipboardSyncService)
        var lastSeenRev = 0L
        while (isActive) {
            runCatching {
                val pair = NetworkClient.getClip(base)
                if (pair != null) {
                    val (txt, rev) = pair
                    if (rev > lastSeenRev) {
                        lastSeenRev = rev
                        applyToAndroidClipboard(txt)
                    }
                }
            }
            delay(1500)
        }
    }

    private fun applyToAndroidClipboard(t: String) {
        val clip = ClipData.newPlainText("remote", t)
        cm.setPrimaryClip(clip)
        lastPushedHash = t.hashCode().toString()
    }

    override fun onDestroy() {
        cm.removePrimaryClipChangedListener(this)
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
