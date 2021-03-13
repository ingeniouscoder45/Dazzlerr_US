package com.dazzlerr_usa.views.fragments.currentlocation


import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log

import java.io.IOException
import java.util.Locale

class LocationAddress {
    private val TAG = "LocationAddress"

    fun getAddressFromLocation(latitude: Double, longitude: Double,
                               context: Context, handler: Handler)
    {
        val thread = object : Thread() {
            override fun run() {
                val geocoder = Geocoder(context, Locale.getDefault())
                var result: String? = null
                try {
                    val addressList = geocoder.getFromLocation(
                            latitude, longitude, 1)
                    if (addressList != null && addressList.size > 0) {
                        val address = addressList[0]
                        val sb = StringBuilder()
                        for (i in 0 until address.maxAddressLineIndex) {
                            sb.append(address.getAddressLine(i)).append("\n")
                        }
                        sb.append(address.locality).append(", ")
                        // sb.append(address.getLocality()).append("\n");
                        // sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.countryName)
                        result = sb.toString()
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Unable connect to Geocoder", e)
                } finally {
                    val message = Message.obtain()
                    message.target = handler
                    if (result != null) {
                        message.what = 1
                        val bundle = Bundle()
                        /*result = "Latitude: " + latitude + " Longitude: " + longitude +
                                "\n\nAddress:\n" + result;*/
                        bundle.putString("address", result)
                        message.data = bundle
                    } else {
                        message.what = 0
                        // Bundle bundle = new Bundle();
                        /*result = "Latitude: " + latitude + " Longitude: " + longitude +
                                "\n Unable to get address for this lat-long.";*/
                        //bundle.putString("address", "");
                        // message.setData(bundle);
                    }
                    message.sendToTarget()
                }
            }
        }
        thread.start()
    }
}