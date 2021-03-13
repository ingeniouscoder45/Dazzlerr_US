package com.dazzlerr_usa.utilities

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.dazzlerr_usa.BuildConfig
import com.dazzlerr_usa.utilities.dagger.DaggerModule
import com.dazzlerr_usa.utilities.dagger.DaggerMyComponent
import com.dazzlerr_usa.utilities.dagger.MyComponent
import com.github.nkzawa.socketio.client.Socket
import com.github.nkzawa.socketio.client.IO
import com.google.android.gms.ads.MobileAds
import ss.com.bannerslider.Slider
import timber.log.Timber
import java.net.URISyntaxException

class MyApplication : MultiDexApplication()
{

    lateinit var myComponent: MyComponent

    private var mSocket: Socket? = null


    fun getSocket(): Socket? {
        return mSocket
    }

    override fun onCreate()
    {
        super.onCreate()

        MobileAds.initialize(this)

        try {
            mSocket = IO.socket(Constants.SOCKET_CHAT_SERVER_URL)
        } catch (e : URISyntaxException) {
            throw  RuntimeException(e)
        }

        //----------Dagger object builder--------
        myComponent = DaggerMyComponent.builder()
                .daggerModule(DaggerModule(this))
                .build()

        //---------Timber object build-----------
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Slider.init(PicassoImageLoadingService(this))

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }
}
