package com.litbig.engapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.litbig.engapp.databinding.FragmentMainBinding
import com.litbig.engapp.utils.TestManager
import org.koin.android.ext.android.inject

class MainFragment() : Fragment() {
    lateinit var binding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    val testManager: TestManager by inject()
    val map = mutableMapOf<String, View>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.myInstance = this
        TestManager.apply {
            map[this.GPS] = binding.btnGPS
            map[this.MIC] = binding.btnMic
            map[this.SPEAKER] = binding.btnSpeaker
            map[this.DISK] = binding.btnDisk
            map[this.WIFI] = binding.btnWiFi
            map[this.BT] = binding.btnBT

            map[this.LCD] = binding.btnLCD
            map[this.TOUCH] = binding.btnTouch
            map[this.HWKEY] = binding.btnHWKey
            map[this.RADIO] = binding.btnRadio
            map[this.DAB] = binding.btnDAB
            map[this.AUX] = binding.btnAux
            map[this.CAMERA] = binding.btnCamera
            map[this.SENSOR] = binding.btnSensor
//            map.put(this.LVDS, binding.btnLVDS)
//            map.put(this.DIP, binding.btnDipSwitch)
        }

        testManager.update.observe(this, Observer {
            testManager.systemInfo.apply {
                BTADDRESS?.let { createBCode(binding.ivBarCode, it) }

                binding.tvInfo.text = "Model: ${MODELNAME}\n\n" +
                            "Ver: ${APPVERSION}\n\n" +
                            "BuildDate: ${BUILDDATE}\n\n" +
                            "BT Add: ${BTADDRESS}\n\n" +
                            "BT Ver: ${BTVERSION}\n\n" +
                            "DAB Ver: ${DABVERSION}\n\n"
            }
        })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        for (item in map) {
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
//            binding.btnDipSwitch -> action = R.id.action_mainFragment_to_dipSwitchFragment

            binding.btnLCD -> action = R.id.action_mainFragment_to_dipSwitchFragment
            binding.btnTouch -> action = R.id.action_mainFragment_to_dipSwitchFragment
            binding.btnHWKey -> action = R.id.action_mainFragment_to_dipSwitchFragment
            binding.btnRadio -> action = R.id.action_mainFragment_to_dipSwitchFragment
            binding.btnDAB -> action = R.id.action_mainFragment_to_dipSwitchFragment
            binding.btnAux -> action = R.id.action_mainFragment_to_dipSwitchFragment
            binding.btnCamera -> action = R.id.action_mainFragment_to_dipSwitchFragment
            binding.btnSensor -> action = R.id.action_mainFragment_to_dipSwitchFragment
        }
        if (action > 0) {
            findNavController().navigate(action)
        }
    }

    private fun createBCode(img: ImageView, text: String) {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            img.setImageBitmap(bitmap)
        } catch (e: Exception) {
        }
    }

    fun endTest() {
        Intent("android.intent.action.MASTER_CLEAR").apply {
            setPackage("android")
            addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
            putExtra("android.intent.extra.REASON", "MasterClearConfirm")
            putExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", true)
            activity?.let {
                it.sendBroadcast(this)
            }
        }
        activity?.finish()
    }
}