package com.litbig.engapp.testcase.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.litbig.engapp.R
import com.litbig.engapp.utils.ModelGps

class AdapterGps(private val mContext: Context, private val mData: ArrayList<ModelGps>) :
    BaseAdapter() {
    var mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun getCount(): Int {
        return mData.size
    }

    override fun getItem(position: Int): Any? {
        return position
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view: View = mLayoutInflater.inflate(R.layout.gps_model, null)
        try {
            val prn: TextView = view.findViewById(R.id.gps_model_prn)
            val snr: TextView = view.findViewById(R.id.gps_model_snr)

            prn.text = mData[position].prn.toString()
            snr.text = mData[position].snr.toInt().apply {
                var color = R.drawable.bg_fac_text
                if (this > 20) {
                    color = R.drawable.bg_fac_success
                }
                snr.background = mContext.resources.getDrawable(color)
            }.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }
}