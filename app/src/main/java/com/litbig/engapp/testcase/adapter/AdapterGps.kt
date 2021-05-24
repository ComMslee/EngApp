package com.litbig.engapp.testcase.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.litbig.engapp.R
import com.litbig.engapp.utils.ModelGps
import java.lang.String

class AdapterGps(private val mContext: Context, private val mData : ArrayList<ModelGps>) : BaseAdapter() {
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
        val prn = view.findViewById<View>(R.id.gps_model_prn) as TextView
        val snr = view.findViewById<View>(R.id.gps_model_snr) as TextView
        try {
            prn.setText(String.valueOf(mData[position].prn))
            val nSnr = mData[position].snr.toInt()
            snr.text = nSnr.toString()
            if (nSnr > 20) {
                snr.background = mContext.resources.getDrawable(R.drawable.bg_fac_success)
            } else {
                snr.background = mContext.resources.getDrawable(R.drawable.bg_fac_text)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }
}