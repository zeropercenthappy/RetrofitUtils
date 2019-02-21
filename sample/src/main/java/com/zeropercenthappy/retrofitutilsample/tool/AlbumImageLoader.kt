package com.zeropercenthappy.retrofitutilsample.tool

import android.widget.ImageView
import com.nostra13.universalimageloader.core.ImageLoader
import com.yanzhenjie.album.AlbumFile
import com.yanzhenjie.album.AlbumLoader

class AlbumImageLoader : AlbumLoader {
    override fun load(imageView: ImageView, albumFile: AlbumFile) {
        load(imageView, albumFile.path)
    }

    override fun load(imageView: ImageView, url: String) {
        val imageLoader = ImageLoader.getInstance()
        imageLoader.displayImage("file://$url", imageView)
    }
}