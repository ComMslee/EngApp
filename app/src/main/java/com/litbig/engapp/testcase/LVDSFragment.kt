package com.litbig.engapp.testcase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.litbig.engapp.databinding.FragmentTcLvdsBinding
import com.litbig.engapp.utils.TCBaseFragment
import com.litbig.engapp.utils.TestManager

class LVDSFragment : TCBaseFragment() {
    lateinit var binding: FragmentTcLvdsBinding

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTcLvdsBinding.inflate(inflater, container, false)
        setMode(binding.result.id, TestManager.LVDS)
        return binding.root
    }
}