package com.litbig.engapp.utils

import androidx.fragment.app.Fragment
import com.litbig.engapp.testcase.ResultFragment

open class TCBaseFragment : Fragment(){
    fun setMode(id: Int, mode: String) {
        childFragmentManager.findFragmentById(id)?.let { resultfragment ->
            if (resultfragment is ResultFragment) {
                resultfragment.setMode(TestManager.GPS)
            }
        }
    }
}