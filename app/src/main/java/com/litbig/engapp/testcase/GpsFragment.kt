package com.litbig.engapp.testcase

import android.annotation.SuppressLint
import android.content.Context
import android.location.GpsStatus
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.litbig.engapp.R
import com.litbig.engapp.databinding.FragmentTcGpsBinding
import com.litbig.engapp.testcase.adapter.AdapterGps
import com.litbig.engapp.utils.ModelGps
import com.litbig.engapp.utils.TCBaseFragment
import com.litbig.engapp.utils.TestManager
import java.util.*
import kotlin.collections.ArrayList

class GpsFragment : TCBaseFragment() {
    lateinit var binding: FragmentTcGpsBinding
    private var mLocationManager: LocationManager? = null
    private val localListener = LocationListener { }

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
                localListener
            )
            it.addGpsStatusListener(listener)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLocationManager?.apply {
            removeGpsStatusListener(listener)
            removeUpdates(localListener)
        }
    }

    @SuppressLint("MissingPermission")
    val listener = GpsStatus.Listener { event ->
        if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            val model: ArrayList<ModelGps> = ArrayList()
            var satellites = 0
            var satellitesInFix = 0
            mLocationManager?.let { manager ->
                manager.getGpsStatus(null)?.satellites?.let { gpsItems ->
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

                binding.gpsList.let { list ->
                    list.adapter =
                        context?.let { context -> AdapterGps(context.applicationContext, model) }
                }
                val timeToFirstFix = manager.getGpsStatus(null)!!.timeToFirstFix
                if (timeToFirstFix > 0) {
                    binding.gpsFirstFix?.apply {
                        text = "FIXED"
                        setTextColor(
                            resources.getColor(R.color.btn_text_success)
                        )
                    }
                }
                binding.gpsSatellites.text =
                    String.format(Locale.US, "%d/%d", satellitesInFix, satellites)

                if (satellitesInFix > 3 && timeToFirstFix > 0) {
                    selectTest(true)
                }
            }

        }
    }
}