package com.litbig.engapp.testcase

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.litbig.engapp.R
import com.litbig.engapp.databinding.FragmentTcSpeakerBinding
import com.litbig.engapp.utils.TCBaseFragment
import com.litbig.engapp.utils.TestManager

class SpeakerFragment : TCBaseFragment() {
    lateinit var binding: FragmentTcSpeakerBinding
    private var mPlayer: MediaPlayer? = null
    private var mAudioManager: AudioManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAudioManager = context?.let {
            it.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTcSpeakerBinding.inflate(inflater, container, false)
        setMode(binding.result.id, TestManager.SPEAKER)
        binding.myInstance = this

        if (null != mPlayer && mPlayer?.isPlaying == true) {
            binding.btnSpeakerPlay.text = resources.getString(R.string.fact_speaker_playing)
        } else {
            binding.btnSpeakerPlay.text = resources.getString(R.string.fact_speaker_stop)
        }
        binding.factorySubSpeakerSeekbarValue.apply {
            setOnSeekBarChangeListener(seekbarListener)
            mAudioManager?.let {
                progress = it.getStreamVolume(AudioManager.STREAM_MUSIC);
                max = it.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            }
        }
        binding.factorySubSpeakerSeekbar.apply {
            setOnSeekBarChangeListener(seekbarListener)
            progress = 10
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopPlayer()
    }

    private fun playPlayer() {
        if (null == mPlayer) {
            mPlayer = MediaPlayer()
            mPlayer?.let { player ->
                player.setOnPreparedListener {
                    it.isLooping = true
                    it.start()
                    binding.btnSpeakerPlay.text = resources.getString(R.string.fact_speaker_playing)
                }
                try {
                    context?.let {
                        it.assets.openFd("remembrance.mp3")
                    }?.let {
                        player.setDataSource(
                            it.fileDescriptor,
                            it.startOffset,
                            it.length
                        )
                        it.close()
                        player.prepareAsync()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun stopPlayer() {
        mPlayer?.let { player ->
            if (player.isPlaying) {
                binding.btnSpeakerPlay.text = resources.getString(R.string.fact_speaker_stop)
                player.stop()
            }
            player.release()
        }
        mPlayer = null
    }

    fun onClick(view: View) {
        mPlayer?.apply {
            stopPlayer()
        } ?: run {
            playPlayer()
        }
    }

    private val seekbarListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            when (seekBar) {
                binding.factorySubSpeakerSeekbar -> {
                    var left = 1.0f
                    var right = 1.0f
                    if (progress == 10) {
                        left = 1.0f
                        right = 1.0f
                    } else if (progress < 10) {
                        right = 0.1f * progress.toFloat()
                    } else {
                        left = 1.0f - ((0.1f * progress.toFloat()) - 1.0f)
                    }
                    mPlayer?.setVolume(left, right)
                }
                binding.factorySubSpeakerSeekbarValue -> {
                    mAudioManager?.let {
                        it.setStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            progress,
                            AudioManager.FLAG_PLAY_SOUND
                        )
                    }
                }
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }

    }
}