package com.litbig.engapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.litbig.engapp.utils.TestManager
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    val testManager: TestManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intentFilter = IntentFilter()
        intentFilter.addAction("com.litbig.intent.action.factory.receiver")
        registerReceiver(broadcastReceiver, intentFilter)

        sendBroadcast(Intent().apply { action = "com.litbig.intent.action.factory.deviceinfo" })
    }

    override fun onBackPressed() {

    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.apply {
                getStringExtra("RECEIVE_TPYE")?.let {
                    when (it) {
                        "deviceinfo" -> {
                            testManager.systemInfo.APPVERSION = intent.getStringExtra("APPVERSION")
                            testManager.systemInfo.MODELNAME = intent.getStringExtra("MODELNAME")
                            testManager.systemInfo.BTADDRESS = intent.getStringExtra("BTADDRESS")
                            testManager.systemInfo.BTVERSION = intent.getStringExtra("BTVERSION")
                            testManager.systemInfo.DABVERSION = intent.getStringExtra("DABVERSION")
                            testManager.systemInfo.BUILDDATE = intent.getStringExtra("BUILDDATE")

                            testManager.update.value = 0
                        }
                    }
                }
            }
        }
    }
}