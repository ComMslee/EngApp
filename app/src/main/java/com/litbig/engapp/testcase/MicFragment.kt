package com.litbig.engapp.testcase

import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.litbig.engapp.databinding.FragmentTcMicBinding
import com.litbig.engapp.utils.TCBaseFragment
import com.litbig.engapp.utils.TestManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MicFragment : TCBaseFragment() {
    lateinit var binding: FragmentTcMicBinding

    private var mediaRecorder: MediaRecorder? = null
    private var thread: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTcMicBinding.inflate(inflater, container, false)
        setMode(binding.result.id, TestManager.MIC)

        val bMic = context?.let {
            it.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
        }!!

        if (bMic) {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(Environment.getExternalStorageDirectory().path + "/text.mp3")
                try {
                    prepare()
                    start()
                } catch (e: Exception) {
                    Log.e("mslee", "prepare() failed")
                }

                setOnInfoListener(object : MediaRecorder.OnInfoListener {
                    override fun onInfo(mr: MediaRecorder?, what: Int, extra: Int) {

                    }

                })
            }
        } else {

        }

        CoroutineScope(Main).launch {
            while (true) {
                delay(300L)
                mediaRecorder?.apply {
                    binding.pbAmp.setProgress((maxAmplitude * 100.0f / 32768.0f).toInt())
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaRecorder?.apply {
            stop()
            release()
            mediaRecorder = null
        }
    }
}