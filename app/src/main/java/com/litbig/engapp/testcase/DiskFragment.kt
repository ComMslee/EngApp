package com.litbig.engapp.testcase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.litbig.engapp.databinding.FragmentTcDiskBinding
import com.litbig.engapp.utils.TCBaseFragment
import com.litbig.engapp.utils.TestManager

class DiskFragment : TCBaseFragment() {
    lateinit var binding: FragmentTcDiskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTcDiskBinding.inflate(inflater, container, false)
        setMode(binding.result.id, TestManager.DISK)
        return binding.root
    }
}