package hgh.project.shopping.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

internal fun ImageView.loadCenterCrop(url: String, corner:Float =0F){
    Glide.with(this)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply {
            if (corner >0) transforms(CenterCrop(), RoundedCorners(corner.fromDpToPx()))
        }
        .into(this)
}

internal fun ImageView.load(url: String, corner:Float =0F){
    Glide.with(this)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply {
            if (corner >0) transforms( RoundedCorners(corner.fromDpToPx()))
        }
        .into(this)
}