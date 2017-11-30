package lv.rigadevday.android.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import cz.eman.android.devfest.R

private fun ImageView.getFetcherInstance(url: String) = Glide
        .with(this.context)
        .load(url)

private val defaultOptions get() = RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA)

fun ImageView.loadCircleAvatar(url: String): Target<Drawable> = getFetcherInstance(url)
        .apply(defaultOptions
                .centerCrop()
                .transform(CircleCrop())
        )
        .into(this)

fun ImageView.loadSquareAvatar(url: String): Target<Drawable> = getFetcherInstance(url)
        .apply(defaultOptions
                .centerCrop()
                .placeholder(R.drawable.vector_speaker_placeholder)
        )
        .into(this)

fun ImageView.loadLogo(url: String, onDone: () -> Unit): Target<Drawable> = getFetcherInstance(url)
        .apply(defaultOptions.fitCenter())
        .into(object : ViewTarget<ImageView, Drawable>(this) {
            override fun onResourceReady(resource: Drawable?, transition: Transition<in Drawable>?) {
                view.setImageDrawable(resource)
                onDone()
            }
        })

fun ImageView.loadVenueImage(url: String): Target<Drawable> = getFetcherInstance(url)
        .apply(defaultOptions.centerCrop())
        .into(this)
