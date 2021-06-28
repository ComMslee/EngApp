package com.litbig.engapp.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlin.properties.Delegates

class TestManager {
    companion object {
        val GPS = "gps"
        val MIC = "mic"
        val SPEAKER = "speaker"
        val DISK = "disk"
        val WIFI = "wifi"
        val BT = "bt"
        val LVDS = "lvds"
        val DIP = "dip"

        val LCD = "lcd"
        val TOUCH = "touch"
        val HWKEY = "hwkey"
        val RADIO = "radio"
        val DAB = "dab"
        val AUX = "aux"
        val CAMERA = "camera"
        val SENSOR = "sensor"
    }

    val map = mutableMapOf<String, Boolean>()
    val systemInfo = SystemInfo()

    val update = MutableLiveData<Int>()
}