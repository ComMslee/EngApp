package com.litbig.engapp.testcase

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.litbig.engapp.R
import com.litbig.engapp.databinding.FragmentTcBtBinding
import com.litbig.engapp.utils.TCBaseFragment
import com.litbig.engapp.utils.TestManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class BTFragment : TCBaseFragment() {
    lateinit var binding: FragmentTcBtBinding

    private val mDeviceList = ArrayList<String>()
    private var mBluetoothAdapter: BluetoothAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTcBtBinding.inflate(inflater, container, false)
        binding.myInstance = this
        setMode(binding.result.id, TestManager.BT)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter().apply {
            if (isEnabled) {
                startDiscovery()
                binding.tvOn.background =
                    resources.getDrawable(R.drawable.bg_fac_success)
            }
            binding.btnOnOff.isChecked = isEnabled

        }

        binding.tvGuide.text = resources.getString(R.string.deviceid)

        context?.let {
            it.registerReceiver(mReceiver,
                IntentFilter().apply {
                    addAction(BluetoothDevice.ACTION_FOUND)
                    addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
                    addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                    addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
                })
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        context?.let {
            it.unregisterReceiver(mReceiver)
        }
    }

    fun onClick(view: View) {
        when (view) {
            binding.btnOnOff -> {
                mBluetoothAdapter?.let {
                    if (binding.btnOnOff.isChecked) {
                        it.enable()
                    } else {
                        it.disable()
                    }
                }
            }
            binding.btnConnect -> {
                startActivity(Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
            }
        }
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    device?.apply {
                        val inputString = "$name :: $address"
                        if (name != null) {
                            mDeviceList.add(0, inputString)
                        } else {
                            mDeviceList.add(inputString)
                        }
                        Log.i("BT", inputString)
//                        if (name == resources.getString(R.string.deviceid)) {
//                            if (bondState != BOND_BONDED) {
//                                device.createBond()
//                            }
//                        }
//                        mBluetoothAdapter?.let {
//                            for(device in it.bondedDevices){
//                                if(device.name == resources.getString(R.string.deviceid)){
//                                    binding.tvBonded.background = resources.getDrawable(R.drawable.bg_fac_success)
//                                }
//                            }
//                        }
                    }
                    binding.lvbt.adapter = context?.let {
                        ArrayAdapter(
                            it,
                            android.R.layout.simple_list_item_1, mDeviceList.distinct()
                        )
                    }
                    binding.tvScan.background = resources.getDrawable(R.drawable.bg_fac_success)
                }
                "android.bluetooth.device.action.PAIRING_REQUEST" -> {
                    Log.i("BT", "android.bluetooth.device.action.PAIRING_REQUEST")
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.i("BT", "ACTION_DISCOVERY_STARTED")

                    mDeviceList.clear()
                    binding.lvbt.adapter = context?.let {
                        ArrayAdapter(
                            it,
                            android.R.layout.simple_list_item_1, mDeviceList.distinct()
                        )
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.i("BT", "ACTION_DISCOVERY_FINISHED")
                    mBluetoothAdapter?.let {
                        CoroutineScope(Main).launch {
                            delay(10 * 1000)
                            it.startDiscovery()
                        }
                    }
                }
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val state =
                        intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    if (state == BluetoothAdapter.STATE_ON) {
                        mBluetoothAdapter?.let {
                            it.startDiscovery()
                            binding.tvOn.background =
                                resources.getDrawable(R.drawable.bg_fac_success)
                        }
                    } else if (state == BluetoothAdapter.STATE_OFF) {
                        binding.lvbt.adapter = null
                        mDeviceList.clear()
                        binding.tvOff.background = resources.getDrawable(R.drawable.bg_fac_success)
                    }
                }
            }
        }
    }
}