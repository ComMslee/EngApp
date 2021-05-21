package com.litbig.engapp.testcase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.litbig.engapp.R
import com.litbig.engapp.databinding.FragmentUnitResultBinding
import com.litbig.engapp.utils.TestManager
import org.koin.android.ext.android.inject

class ResultFragment : Fragment() {
    lateinit var binding: FragmentUnitResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    val testManager: TestManager by inject()
    var strMode = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUnitResultBinding.inflate(inflater, container, false)
        binding.myInstance = this
        return binding.root
    }

    fun onClick(view: View) {
        var bPass = false
        when (view) {
            binding.btnOK -> bPass = true
            binding.btnNG -> bPass = false
        }
        testManager.map.put(strMode, bPass)
        findNavController().navigate(R.id.action_global_mainFragment)
    }

    fun setMode(mode: String) {
        strMode = mode
    }
}