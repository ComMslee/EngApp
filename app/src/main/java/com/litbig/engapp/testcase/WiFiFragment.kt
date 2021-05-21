package com.litbig.engapp.testcase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.litbig.engapp.databinding.FragmentTcWifiBinding
import com.litbig.engapp.utils.TCBaseFragment
import com.litbig.engapp.utils.TestManager

class WiFiFragment : TCBaseFragment() {
    lateinit var binding: FragmentTcWifiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTcWifiBinding.inflate(inflater, container, false)
        setMode(binding.result.id, TestManager.WIFI)
        return binding.root
    }
}