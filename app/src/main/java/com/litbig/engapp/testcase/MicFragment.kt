package com.litbig.engapp.testcase

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.litbig.engapp.R
import com.litbig.engapp.databinding.FragmentTcMicBinding
import com.litbig.engapp.utils.TCBaseFragment
import com.litbig.engapp.utils.TestManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MicFragment : TCBaseFragment() {
    lateinit var binding: FragmentTcMicBinding

    private val PATH = Environment.getExternalStorageDirectory().path + "/text.mp3"
    private var mediaRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTcMicBinding.inflate(inflater, container, false)
        binding.myInstance = this
        setMode(binding.result.id, TestManager.MIC)

        val bMic = context?.let {
            it.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
        }!!

        binding.btnRec.isEnabled = bMic
        binding.btnPlay.isEnabled = File(PATH).isAbsolute

        CoroutineScope(Main).launch {
            val par = 100.0f / 32768.0f
            while (true) {
                delay(200L)
                mediaRecorder?.apply {
                    binding.pbAmp.progress = (maxAmplitude * par).toInt()
                    if (binding.pbAmp.progress > 50) {
                        binding.tvSound.background =
                            resources.getDrawable(R.drawable.bg_fac_success)
                    }
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopPlayer()
        mediaRecorder?.apply {
            stop()
            release()
            mediaRecorder = null
        }
    }

    fun onClick(view: View) {
        when (view) {
            binding.btnRec -> {
                mediaRecorder?.apply {
                    stop()
                    release()
                    mediaRecorder = null
                    binding.pbAmp.progress = 0
                    binding.btnRec.text = "REC"
                    if (File(PATH).isAbsolute) {
                        binding.btnPlay.isEnabled = true
                    }
                } ?: run {
                    binding.btnPlay.isEnabled = false
                    binding.btnRec.text = "STOP"
                    mediaRecorder = MediaRecorder().apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                        setOutputFile(PATH)
                        try {
                            prepare()
                            start()
                        } catch (e: Exception) {
                            Log.e("mslee", "prepare() failed")
                        }
                    }
                }
            }
            binding.btnPlay -> {
                mPlayer?.apply {
                    stopPlayer()
                } ?: run {
                    playPlayer()
                }
            }
        }
    }

    private fun playPlayer() {
        if (null == mPlayer) {
            mPlayer = MediaPlayer()
            mPlayer?.let { player ->
                player.setOnPreparedListener {
                    it.isLooping = true
                    it.start()
                    binding.btnPlay.text = resources.getString(R.string.fact_speaker_playing)
                }
                try {
                    player.setDataSource(PATH)
                    player.prepareAsync()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun stopPlayer() {
        mPlayer?.let { player ->
            if (player.isPlaying) {
                binding.btnPlay.text = resources.getString(R.string.fact_speaker_stop)
                player.stop()
            }
            player.release()
        }
        mPlayer = null
    }
}