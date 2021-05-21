package com.litbig.engapp.utils

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
    }

    var bMesterClear = false
    val map = mutableMapOf<String, Boolean>()
}