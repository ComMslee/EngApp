package com.litbig.engapp.testcase

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.GpsStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.litbig.engapp.R
import com.litbig.engapp.databinding.FragmentTcGpsBinding
import com.litbig.engapp.testcase.adapter.AdapterGps
import com.litbig.engapp.utils.ModelGps
import com.litbig.engapp.utils.TCBaseFragment
import com.litbig.engapp.utils.TestManager
import java.util.*
import kotlin.collections.ArrayList

class GpsFragment() : TCBaseFragment() {
    lateinit var binding: FragmentTcGpsBinding
    var mLocationManager: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTcGpsBinding.inflate(inflater, container, false)
        setMode(binding.result.id, TestManager.GPS)

        mLocationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocationManager?.let {
            it.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10000,
                10.0f,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        Log.e("mslee", "log...")
                    }
                })
            it.addGpsStatusListener(listener)
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        mLocationManager?.apply {
            removeGpsStatusListener(listener)
        }
    }

    val listener = object : GpsStatus.Listener {
        override fun onGpsStatusChanged(event: Int) {
            if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
                val model: ArrayList<ModelGps> = ArrayList<ModelGps>()
                var satellites = 0
                var satellitesInFix = 0
                mLocationManager?.let {
                    if (ActivityCompat.checkSelfPermission(
                            activity!!.applicationContext,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    it.getGpsStatus(null)?.satellites?.let { gpsItems ->
                        for (sat in gpsItems) {
                            if (sat.usedInFix()) {
                                satellitesInFix++
                            }
                            satellites++
                            if (model.size <= 12) model.add(
                                ModelGps(
                                    sat.prn,
                                    sat.snr,
                                    sat.elevation,
                                    sat.azimuth
                                )
                            )
                        }
                    }

                    binding.gpsList?.let {
                        it.adapter = AdapterGps(activity!!.applicationContext, model)
                    }
                    val timeToFirstFix = it.getGpsStatus(null)!!.timeToFirstFix
                    if (timeToFirstFix > 0) {
                        binding.gpsFirstFix?.let {
                            it.text = "FIXED"
                            it.setTextColor(
                                resources.getColor(R.color.btn_text_success)
                            )
                        }
//                    if (isShow()) {
//                        getMyFunctionItem().getSubItem(getTagString(mFixedTextView)).setSuccess()
//                        onUpdateView()
//                    }
                    }
                    binding.gpsSatellites.text =
                        String.format(Locale.US, "%d/%d", satellitesInFix, satellites)

                }
            }
        }
    }
}