package com.litbig.engapp.testcase

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.litbig.engapp.R
import com.litbig.engapp.databinding.FragmentTcDiskBinding
import com.litbig.engapp.utils.TCBaseFragment
import com.litbig.engapp.utils.TestManager
import java.io.*

class DiskFragment : TCBaseFragment() {
    private val SD1 = "/storage/sdcard00"
    private val USB0 = "/storage/usbdisk01"
    private val USB1 = "/storage/usbdisk02"

    lateinit var binding: FragmentTcDiskBinding

    lateinit var storageItems: Array<StorageViewGroup>
    lateinit var handler: Handler

    class StorageViewGroup(
        val title: TextView,
        val mount: TextView,
        val read: TextView,
        val write: TextView
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handler = context?.let {
            Handler(it.mainLooper, callback)
        }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTcDiskBinding.inflate(inflater, container, false)
        setMode(binding.result.id, TestManager.DISK)
        storageItems = arrayOf(
            StorageViewGroup(
                binding.tvStorage01.apply { tag = SD1 },
                binding.tvStorage01Mount.apply { tag = false },
                binding.tvStorage01Read.apply { tag = false },
                binding.tvStorage01Write.apply { tag = false }
            ),
            StorageViewGroup(
                binding.tvStorage02.apply { tag = USB0 },
                binding.tvStorage02Mount.apply { tag = false },
                binding.tvStorage02Read.apply { tag = false },
                binding.tvStorage02Write.apply { tag = false }
            ),
            StorageViewGroup(
                binding.tvStorage03.apply { tag = USB1 },
                binding.tvStorage03Mount.apply { tag = false },
                binding.tvStorage03Read.apply { tag = false },
                binding.tvStorage03Write.apply { tag = false }
            )
        )
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_MEDIA_MOUNTED)
            addAction(Intent.ACTION_MEDIA_UNMOUNTED)
            addAction(Intent.ACTION_MEDIA_EJECT)
            addDataScheme("file")
        }
        context?.apply {
            registerReceiver(mReceiver, intentFilter)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        context?.apply {
            unregisterReceiver(mReceiver)
        }
    }

    private val callback = Handler.Callback { msg ->
        Log.e("disk", "handleMessage ") //disk add log
        (msg.obj as StorageViewGroup).let {
            val path: String = getTagString(it.title)
            it.write.let { view ->
                var id = R.drawable.bg_fac_fail
                if (writeTest(path)) {
                    id = R.drawable.bg_fac_success
                    view.tag = true
                }
                view.background = resources.getDrawable(id)
            }
            it.read.let { view ->
                var id = R.drawable.bg_fac_fail
                if (readTest(path)) {
                    view.tag = true
                    id = R.drawable.bg_fac_success
                }
                view.background = resources.getDrawable(id)
            }
        }

        synchronized(this){
            var cnt = 0
            for (item in storageItems) {
                if(item.mount.tag as Boolean && item.read.tag as Boolean && item.write.tag as Boolean){
                    cnt++;
                }
            }
            if (storageItems.size == cnt){
                selectTest(true)
            }
        }
        true
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val mountPath = intent.data.toString()
            if (action == Intent.ACTION_MEDIA_MOUNTED) {
                if (mountPath != null) {
                    for (item in storageItems) {
                        val path = getTagString(item.title)
                        if (mountPath.contains(path)) {
                            item.mount.apply {
                                background = resources.getDrawable(R.drawable.bg_fac_success)
                                tag = true
                            }
                            Log.e("disk", "sendMessageDelayed ") //disk add log
                            handler.sendMessageDelayed(Message().apply {
                                what = 0
                                obj = item
                            }, (1000 * 1).toLong())
                        }
                    }
                }
            } else if (action == Intent.ACTION_MEDIA_UNMOUNTED || action == Intent.ACTION_MEDIA_EJECT) {
                if (mountPath != null) {
//                    for (item in storageItems) {
//                        val path = getTagString(item.title)
//                        if (mountPath.contains(path)) {
//                        }
//                    }
                }
            } else {
                Log.e("disk", "Not Match Action is $action") //disk add log
            }
        }
    }

    fun writeTest(tagetDir: String): Boolean {
        val file = File("$tagetDir/testreadwrite.txt")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            OutputStreamWriter(fos).apply {
                write("testword")
                flush()
            }
            Log.e("disk", "writer end") //disk add log
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            try {
                fos?.close()
            } catch (e: Exception) {
            }
        }
        return true
    }

    fun readTest(tagetDir: String): Boolean {
        val file = File("$tagetDir/testreadwrite.txt")
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(file)
            val reader = InputStreamReader(fis)
            val buffer = CharArray(512)
            val len = reader.read(buffer)
            Log.e("disk", "reader buffer " + String(buffer, 0, len)) //disk add log
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            try {
                fis?.close()
            } catch (e: Exception) {
            }
        }
        return true
    }

    private fun getTagString(tv: TextView): String {
        return tv.tag as String
    }
}