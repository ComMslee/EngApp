package com.litbig.engapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.litbig.engapp.databinding.ActivityLanguageBinding
import java.util.*

class LanguageActivity : AppCompatActivity() {
    lateinit var binding: ActivityLanguageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        binding.myInstance = this
        setContentView(binding.root)
    }

    fun onClick(view: View) {
        Intent().apply {
            action = "com.litbig.receiver.language"
            var language = "ko"
            var country = "kr"
            when (view) {
                binding.btnEN -> {
                    language = "en"
                    country = "EN"
                }
                binding.btnKR -> {
                    language = "ko"
                    country = "KR"
                }
                binding.btnAR -> {
                    language = "ar"
                    country = "AE"
                }
                binding.btnRU -> {
                    language = "ru"
                    country = "RU"
                }
            }
            putExtra("language", language)
            putExtra("country", country)
            sendBroadcast(this)
        }
    }
}