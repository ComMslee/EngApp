package com.litbig.engapp.testcase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.litbig.engapp.R
import com.litbig.engapp.databinding.FragmentTcBtBinding
import com.litbig.engapp.databinding.FragmentTcGpsBinding
import com.litbig.engapp.utils.TCBaseFragment
import com.litbig.engapp.utils.TestManager

class BTFragment : TCBaseFragment() {
    lateinit var binding : FragmentTcBtBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTcBtBinding.inflate(inflater, container, false)
        setMode(binding.result.id, TestManager.BT)
        return binding.root
    }
}