package com.litbig.engapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.litbig.engapp.databinding.FragmentMainBinding
import com.litbig.engapp.utils.TestManager
import org.koin.android.ext.android.inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.myInstance = this
        return binding.root
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
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
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