package com.litbig.engapp.testcase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.litbig.engapp.databinding.FragmentTcDipSwitchBinding
import com.litbig.engapp.utils.TCBaseFragment
import com.litbig.engapp.utils.TestManager

class DipSwitchFragment : TCBaseFragment() {
    lateinit var binding: FragmentTcDipSwitchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTcDipSwitchBinding.inflate(inflater, container, false)
        setMode(binding.result.id, TestManager.GPS)
        return binding.root
    }
}