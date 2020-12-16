package com.zeropercenthappy.retrofitutilsample

import android.app.Application
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import com.zeropercenthappy.retrofitutilsample.tool.AlbumImageLoader

class CustomApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initAlbum()
    }

    private fun initAlbum() {
        val imageLoaderConfig = ImageLoaderConfiguration.Builder(this).build()
        ImageLoader.getInstance().init(imageLoaderConfig)
        Album.initialize(AlbumConfig.newBuilder(this).setAlbumLoader(AlbumImageLoader()).build())
    }

}