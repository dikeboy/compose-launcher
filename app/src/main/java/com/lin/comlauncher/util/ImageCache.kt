package com.lin.comlauncher.util

import android.content.Context
import android.util.LruCache
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap

class ImageCache private constructor(var mContext: Context) {
    companion object {
        var KEY_WALL_PAPER = "wallpaper";
        private var instance: ImageCache? = null
        fun getInstance(mContext: Context? = null): ImageCache? {
            if (instance == null) {
                synchronized(ImageCache::class.java) {
                    if (instance == null) {
                        if (mContext != null)
                            instance = ImageCache(mContext)
                    }
                }
            }
            return instance
        }
    }

    private lateinit var memoryCache: LruCache<String, ImageBitmap>

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8

        memoryCache = object : LruCache<String, ImageBitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: ImageBitmap): Int {
                return bitmap.asAndroidBitmap().byteCount / 1024
            }
        }
    }

    @Composable
    fun getCache(key: String): ImageBitmap {
        if (key == KEY_WALL_PAPER) {
            if (memoryCache.get(key) == null) {
                var imgBitmap = LauncherUtils.getCurrentWallPaper(mContext = mContext).asImageBitmap();
                memoryCache.put(key, imgBitmap)
            }
            return memoryCache.get(key)
        }
        return ImageBitmap(1, 1)
    }

}