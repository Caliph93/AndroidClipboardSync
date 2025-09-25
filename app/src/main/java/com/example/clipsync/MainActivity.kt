package com.example.clipsync

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ipEt = findViewById<EditText>(R.id.etIp)
        val tokenEt = findViewById<EditText>(R.id.etToken)
        val saveBtn = findViewById<Button>(R.id.btnSave)
        val startBtn = findViewById<Button>(R.id.btnStart)

        ipEt.setText(Prefs.serverBaseUrl(this))
        tokenEt.setText(Prefs.token(this))

        saveBtn.setOnClickListener {
            Prefs.setServerBaseUrl(this, ipEt.text.toString().trim())
            Prefs.setToken(this, tokenEt.text.toString().trim())
        }
        startBtn.setOnClickListener {
            startForegroundService(Intent(this, ClipboardSyncService::class.java))
        }
    }
}
