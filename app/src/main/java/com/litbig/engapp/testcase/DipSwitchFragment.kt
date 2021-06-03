package com.litbig.engapp.testcase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.litbig.engapp.R
import com.litbig.engapp.databinding.FragmentTcDipSwitchBinding
import com.litbig.engapp.utils.Properties
import com.litbig.engapp.utils.TCBaseFragment
import com.litbig.engapp.utils.TestManager
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class DipSwitchFragment : TCBaseFragment() {
    lateinit var binding: FragmentTcDipSwitchBinding
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTcDipSwitchBinding.inflate(inflater, container, false)
        setMode(binding.result.id, TestManager.DIP)

        Properties.set("sys.hwtest.switch_a", "1")
        job = CoroutineScope(Dispatchers.Main).launch {
            val dipswitch = listOf(
                Triple<String, View, View>(
                    "/sys/class/gpio/gpio118/value",
                    binding.tvOnDIP01.apply { tag = false },
                    binding.tvOffDIP01.apply { tag = false }
                ),
                Triple<String, View, View>(
                    "/sys/class/gpio/gpio116/value",
                    binding.tvOnDIP02.apply { tag = false },
                    binding.tvOffDIP02.apply { tag = false }
                ),
                Triple<String, View, View>(
                    "/sys/class/gpio/gpio117/value",
                    binding.tvOnDIP03.apply { tag = false },
                    binding.tvOffDIP03.apply { tag = false }
                ),
                Triple<String, View, View>(
                    "/sys/class/gpio/gpio115/value",
                    binding.tvOnDIP04.apply { tag = false },
                    binding.tvOffDIP04.apply { tag = false }
                ),
            )
            while (true) {
                var PassCnt = 0
                for (path in dipswitch) {
                    read(path.first).let {
                        if (it.isNotEmpty()) {
                            try {
                                val targetView = when (it.toInt()) {
                                    0 -> path.third
                                    1 -> path.second
                                    else -> null
                                }
                                targetView?.apply {
                                    background = resources.getDrawable(R.drawable.bg_fac_success)
                                    tag = true
                                }
                            } catch (e: java.lang.Exception) {
                            }
                        }
                    }
                    if(path.third.tag as Boolean && path.second.tag as Boolean){
                        PassCnt++
                    }
                    delay(70)
                }
                if(dipswitch.size == PassCnt){
                    selectTest(true)
                    break
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
    }

    fun read(tagetDir: String): String {
        val file = File(tagetDir)
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(file)
            val reader = InputStreamReader(fis)
            val buffer = CharArray(512)
            val len = reader.read(buffer)
            return String(buffer, 0, len)[0].toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        } finally {
            try {
                fis?.close()
            } catch (e: Exception) {
            }
        }
        return ""
    }

}