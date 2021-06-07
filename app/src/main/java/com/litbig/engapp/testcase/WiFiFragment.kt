package com.litbig.engapp.testcase

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.SupplicantState
import android.net.wifi.WifiConfiguration
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WiFiFragment : TCBaseFragment() { //test
    lateinit var binding: FragmentTcWifiBinding
    lateinit var wifiManager: WifiManager
    var job: Job? = null

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
        binding.tvGuide.text =
            "SSID : ${resources.getString(R.string.ssid)} PASS : ${resources.getString(R.string.pass)}"
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
        startJob()
        super.onResume()
    }

    override fun onPause() {
        stopJob()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        context?.unregisterReceiver(wifiReciver)
    }

    fun startJob() {
        if (job != null) {
            job?.cancel()
        }
        job = CoroutineScope(Main).launch {
            while (true) {
                startScan()
                delay(10*1000)
                Log.e("mslee", "startScan()!!")
            }
        }
        job?.start()
    }

    fun stopJob() {
        Log.e("mslee", "stopTimer()!!")
        job?.cancel()
        job = null
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
                                stopJob()
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
                                startJob()
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
                    val tagetssid = resources.getString(R.string.ssid)
                    val results = wifiManager.scanResults.toMutableList()
                    val filterList = mutableListOf<ScanResult>()
                    for (item in results) {
                        if (item.SSID.isNotEmpty()) {
                            filterList.add(item)
                        }
                        if (item.SSID == tagetssid && wifiManager.connectionInfo.supplicantState != SupplicantState.COMPLETED) {
                            connectToAP(tagetssid, resources.getString(R.string.pass))
                        }
                    }

                    connectSSID =
                        if (wifiManager.connectionInfo.supplicantState == SupplicantState.COMPLETED) {
                            connectedSSID()
                        } else {
                            ""
                        }

                    if (connectSSID == tagetssid) {
                        binding.tvConnect.background =
                            resources.getDrawable(R.drawable.bg_fac_success)
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

    fun connectToAP(ssid: String, passkey: String) {
        val scanResultList = wifiManager.scanResults
        scanResultList.find {
            it.SSID == ssid
        }?.let {
            val securityMode = getScanResultSecurity(it)
            WifiConfiguration().apply {
                if (securityMode.equals("OPEN", ignoreCase = true)) {
                    this.SSID = "\"" + ssid + "\""
                    this.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                    val res = wifiManager.addNetwork(this)
                    wifiManager.enableNetwork(res, true)
                    wifiManager.isWifiEnabled = true
                } else if (securityMode.equals("WEP", ignoreCase = true)) {
                    this.SSID = "\"" + ssid + "\""
                    this.wepKeys[0] = "\"" + passkey + "\""
                    this.wepTxKeyIndex = 0
                    this.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                    this.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                    val res = wifiManager.addNetwork(this)
                    val b = wifiManager.enableNetwork(res, true)
                    wifiManager.isWifiEnabled = true
                }
                this.SSID = "\"" + ssid + "\""
                this.preSharedKey = "\"" + passkey + "\""
                this.hiddenSSID = true
                this.status = WifiConfiguration.Status.ENABLED
                this.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                this.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                this.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                this.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                this.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                this.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                this.allowedProtocols.set(WifiConfiguration.Protocol.WPA)

                //connect...
                val res = wifiManager.addNetwork(this)
                wifiManager.enableNetwork(res, true)
                val changeHappen = wifiManager.saveConfiguration()
                if (res != -1 && changeHappen) {
                } else {
                    //Log.d(TAG, "*** Change NOT happen");
                }
                wifiManager.isWifiEnabled = true
            }
        }
    }

    fun getScanResultSecurity(scanResult: ScanResult): String {
        Log.i("test", "* getScanResultSecurity")
        val cap = scanResult.capabilities
        val securityModes = arrayOf("WEP", "PSK", "EAP")
        for (i in securityModes.indices.reversed()) {
            if (cap.contains(securityModes[i])) {
                return securityModes[i]
            }
        }
        return "OPEN"
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