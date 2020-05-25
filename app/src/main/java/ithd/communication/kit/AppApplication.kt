package ithd.communication.kit

import android.app.Application
import android.content.Context

class AppApplication: Application() {


    companion object{
        var mContext: Context? = null
    }


    override fun onCreate() {
        super.onCreate()
        mContext = this
    }
}