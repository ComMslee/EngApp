/*
 * Copyright (c) 2021, Litbig Limited
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.litbig.engapp.testcase.adapter

import android.content.res.TypedArray
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.litbig.engapp.R
import com.litbig.engapp.databinding.ItemWifiBinding

class WifiAdapter : RecyclerView.Adapter<WifiAdapter.WifiHolder>() {
    class WifiHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var listener: OnItemClickListener? = null
        var tvName: TextView = itemView.findViewById(R.id.tvSSID)

        init {
            itemView.setOnClickListener { view ->
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(view, adapterPosition)
                }
            }
        }
    }

    var connectSSID: String = ""
    var mData: Array<ScanResult>? = null
    var listener: OnItemClickListener? = null
    val securityMode = listOf("WEP", "PSK", "EAP")
    lateinit var array: TypedArray

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiHolder {
        val binding = ItemWifiBinding.inflate(LayoutInflater.from(parent.context))
        return WifiHolder(binding.root).also { holder ->
            listener?.let { holder.listener = it }
        }
    }

    override fun onBindViewHolder(holder: WifiHolder, position: Int) {
        val scanResult = mData?.get(position)
        scanResult?.let {
            holder.tvName.text = it.SSID
            holder.tvName.isSelected = (it.SSID == connectSSID)
            val signal = WifiManager.calculateSignalLevel(it.level, 5)
        }
    }

    override fun getItemCount(): Int {
        return mData?.size ?: 0
    }
}