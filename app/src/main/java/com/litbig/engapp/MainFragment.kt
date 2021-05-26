package com.litbig.engapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.litbig.engapp.databinding.FragmentMainBinding
import com.litbig.engapp.utils.TestManager
import org.koin.android.ext.android.inject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MainFragment() : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: FragmentMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    val testManager: TestManager by inject()
    val map = mutableMapOf<String, View>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.myInstance = this
        TestManager.apply {
            map.put(this.GPS, binding.btnGPS)
            map.put(this.MIC, binding.btnMic)
            map.put(this.SPEAKER, binding.btnSpeaker)
            map.put(this.DISK, binding.btnDisk)
            map.put(this.WIFI, binding.btnWiFi)
            map.put(this.BT, binding.btnBT)
            map.put(this.LVDS, binding.btnLVDS)
            map.put(this.DIP, binding.btnDipSwitch)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        for(item in map){
            testManager.map.get(item.key)?.let {
                item.value.setBackgroundColor(if (it) Color.GREEN else Color.RED)
            }
        }
    }

    fun onClick(view: View) {
        var action: Int = 0
        when (view) {
            binding.btnGPS -> action = R.id.action_mainFragment_to_gpsFragment
            binding.btnMic -> action = R.id.action_mainFragment_to_micFragment
            binding.btnSpeaker -> action = R.id.action_mainFragment_to_speakerFragment
            binding.btnDisk -> action = R.id.action_mainFragment_to_diskFragment
            binding.btnWiFi -> action = R.id.action_mainFragment_to_wiFiFragment
            binding.btnBT -> action = R.id.action_mainFragment_to_BTFragment
            binding.btnLVDS -> action = R.id.action_mainFragment_to_LVDSFragment
            binding.btnDipSwitch -> action = R.id.action_mainFragment_to_dipSwitchFragment
        }
        if (action > 0) {
            findNavController().navigate(action)
        }
    }

    fun endTest() {
        if (testManager.bMesterClear) {
            Intent("android.intent.action.MASTER_CLEAR").apply {
                setPackage("android")
                addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                putExtra("android.intent.extra.REASON", "MasterClearConfirm")
                putExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", true)
                activity?.let {
                    it.sendBroadcast(this)
                }
            }
        } else {

        }
        activity?.finish()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}