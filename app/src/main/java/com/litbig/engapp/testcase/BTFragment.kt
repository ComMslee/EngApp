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
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTcBtBinding.inflate(inflater, container, false)
        binding.myInstance = this
        setMode(binding.result.id, TestManager.BT)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter().apply {
            if (isEnabled) {
                startDiscovery()
            }
            binding.btnOnOff.isChecked = isEnabled
        }

        context?.let {
            it.registerReceiver(mReceiver,
                IntentFilter().apply {
                    addAction(BluetoothDevice.ACTION_FOUND)
                    addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
                })
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

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
        }
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    mDeviceList.add("${device!!.name} :: ${device.address}")
                    Log.i("BT", "${device.name} ${device.address}")
                    binding.lvbt.adapter = context?.let {
                        ArrayAdapter(
                            it,
                            android.R.layout.simple_list_item_1, mDeviceList
                        )
                    }
                    binding.tvScan.background = resources.getDrawable(R.drawable.bg_fac_success)
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