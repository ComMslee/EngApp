package com.litbig.engapp.utils

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import com.litbig.engapp.testcase.ResultFragment

open class TCBaseFragment : Fragment() {
    var resultFragment: ResultFragment? = null
    fun setMode(id: Int, mode: String) {
        childFragmentManager.findFragmentById(id)?.let { resultfragment ->
            if (resultfragment is ResultFragment) {
                this.resultFragment = resultfragment
                resultfragment.setMode(mode)
            }
        }
    }

    fun selectTest(bPass: Boolean) {
        Handler(Looper.getMainLooper()).postDelayed({
            this.resultFragment?.let {
                it.selectTest(bPass)
            }
        }, 1000)
    }
}