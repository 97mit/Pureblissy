package com.stimednp.roommvvm.utils

import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pureblissy.android.R

/**
 * Created by rivaldy on Oct/19/2020.
 * Find me on my lol Github :D -> https://github.com/im-o
 */

object UtilFunctions {
    fun loge(message: String) {
        Log.e("THIS ERROR", "ERROR -> $message")
    }

    fun setTimeStamp() =  System.currentTimeMillis().toString();
    fun ImageView.loadImageFromGlide(url: String?) {
        if(url!=null) {
            Glide.with(this)
                .load(url)
                .error(R.drawable.ic_baseline_broken_image)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_hourglass)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(this)
        }

    }

    fun Fragment.LogData(message:String){
        Log.d(this.javaClass.simpleName, "Log -->: "+ message)
    }
}