package com.dazzlerr_usa.views.activities.setprimarydevice

import android.content.Context

interface SetPrimaryDevicePresenter
{

    fun setPrimaryDevice(user_id:String , device_id: String, device_brand: String, device_model: String, device_type: String, operating_system: String)
    fun isOtpValidate(context: Context, otp: String)
    fun verifyDeviceOtp(context: Context, user_id: String, otp :String)

    fun cancelRetrofitRequest()
}
