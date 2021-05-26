package com.litbig.engapp.testcase

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.litbig.engapp.R
import com.litbig.engapp.databinding.FragmentTcWifiBinding
import com.litbig.engapp.testcase.adapter.OnItemClickListener
import com.litbig.engapp.testcase.adapter.WifiAdapter
import com.litbig.engapp.utils.TCBaseFragment
import com.litbig.engapp.utils.TestManager
import java.util.*
import kotlin.concurrent.timer

class WiFiFragment : TCBaseFragment() {
    lateinit var binding: FragmentTcWifiBinding
    lateinit var wifiManager: WifiManager
    var timer: Timer? = null

    private fun connectedSSID() = run {
        if (wifiManager != null) {
            wifiManager.connectionInfo.ssid.replace(
                "\"", ""
            )
        } else {
            ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wifiManager = context?.let {
            it.getSystemService(Context.WIFI_SERVICE) as WifiManager
        }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTcWifiBinding.inflate(inflater, container, false)
        binding.myInstance = this
        setMode(binding.result.id, TestManager.WIFI)

        binding.btnOnOff.isChecked = wifiManager.isWifiEnabled

        context?.let {
            binding.rvWifi.layoutManager = GridLayoutManager(it, 1)
            binding.rvWifi.adapter = WifiAdapter().also { adapter ->
                adapter.listener = object : OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        adapter.mData?.get(position)?.let { item ->
//                        getWifiConfig().find {
//                            it.SSID.replace("\"", "") == item.SSID
                        }?.let {
//                            makeSelectConnectPopup(it)
                        } ?: run {
//                            makeInputPopup(item)
                        }
                    }
                }
            }

            val filter = IntentFilter()
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            it.registerReceiver(wifiReciver, filter)
        }
        return binding.root
    }

    override fun onResume() {
        startTimer()
        super.onResume()
    }

    override fun onPause() {
        stopTimer()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        context?.unregisterReceiver(wifiReciver)
    }

    fun startTimer() {
        stopTimer()
        timer = timer(period = 7000) {
            startScan()
            Log.e("mslee", "call...")
        }
    }

    fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    private fun startScan() {
        wifiManager.startScan()
    }

    private val wifiReciver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.apply {
                when (this.action) {
                    WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                        val wifistate = intent.getIntExtra(
                            WifiManager.EXTRA_WIFI_STATE,
                            WifiManager.WIFI_STATE_UNKNOWN
                        )
                        when (wifistate) {
                            WifiManager.WIFI_STATE_DISABLED -> {
                                binding.tvOff.background =
                                    resources.getDrawable(R.drawable.bg_fac_success)
                                stopTimer()
                                binding.rvWifi.adapter?.let {
                                    (it as WifiAdapter).apply {
                                        mData = arrayOf()
                                        notifyDataSetChanged()
                                    }
                                }
                            }
                            WifiManager.WIFI_STATE_ENABLED -> {
                                binding.tvOn.background =
                                    resources.getDrawable(R.drawable.bg_fac_success)
                                startTimer()
                            }
                        }
                    }
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> {
                        if (this.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)) {
                            scanSuccess()
                            binding.tvScan.background =
                                resources.getDrawable(R.drawable.bg_fac_success)
                        } else {
                            scanFailure()
                        }
                    }
                }

            }
        }
    }

    fun scanSuccess() {    // Wifi검색 성공
        synchronized(this) {
            binding.rvWifi.adapter?.let { adapter ->
                (adapter as WifiAdapter).apply {
                    val results = wifiManager.scanResults.toMutableList()
                    val filterList = mutableListOf<ScanResult>()
                    for (item in results) {
                        if (item.SSID.isNotEmpty()) {
                            filterList.add(item)
                        }
                    }

                    connectSSID =
                        if (wifiManager.connectionInfo.supplicantState == SupplicantState.COMPLETED) {
                            connectedSSID()
                        } else {
                            ""
                        }

                    mData = filterList.sortedBy { it.level }.reversed().toMutableList().apply {
                        find {
                            it.SSID == connectSSID
                        }?.let {
                            remove(it)
                            add(0, it)
                        }
                    }.toTypedArray().also {
                        for (item in it) {
                            Log.e("mslee", item.SSID)
                        }
                        Log.e("mslee", "--------------------------")
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun scanFailure() {    // Wifi검색 실패
    }

    fun onClick(view: View) {
        when (view) {
            binding.btnOnOff -> {
                wifiManager?.let {
                    it.isWifiEnabled = binding.btnOnOff.isChecked
                }
            }
        }
    }
}