package ru.maxim.barybians.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ru.maxim.barybians.App
import ru.maxim.barybians.R
import ru.maxim.barybians.di.AppComponent
import java.lang.ref.WeakReference

fun <T> weak(obj: T) = WeakReference(obj)
fun <T> WeakReference<T>.isNull() = this.get() == null
fun <T> WeakReference<T>.isNotNull() = !this.isNull()

fun Any?.isNull() = this == null
fun Any?.isNotNull() = !this.isNull()

fun CharSequence?.isNotNullOrEmpty(): Boolean = !this.isNullOrEmpty()
fun CharSequence?.isNotNullOrBlank(): Boolean = !this.isNullOrBlank()

fun TextView.setDrawableStart(drawableResource: Int) =
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        AppCompatResources.getDrawable(context, drawableResource), //start
        compoundDrawablesRelative[1],                              //top
        compoundDrawablesRelative[2],                              //end
        compoundDrawablesRelative[3]                               //bottom
    )

fun TextView.setDrawableEnd(drawableResource: Int) =
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        this.compoundDrawablesRelative[0],                         //start
        this.compoundDrawablesRelative[1],                         //top
        AppCompatResources.getDrawable(context, drawableResource), //end
        this.compoundDrawablesRelative[3]                          //bottom
    )

fun TextView.clearDrawables() =
    setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)

fun Context.toast(text: String) = Toast.makeText(this, text, LENGTH_SHORT).show()
fun Context.toast(resource: Int) = toast(getString(resource))
fun Context.longToast(text: String) = Toast.makeText(this, text, LENGTH_LONG).show()
fun Context.longToast(resource: Int) = longToast(getString(resource))

@SuppressLint("CheckResult")
fun ImageView.load(url: String?, @DrawableRes placeholder: Int? = null, thumbnail: String? = null) {
    if (url.isNullOrBlank()) return
    val requestBuilder = Glide.with(context).load(url)

    when {
        thumbnail != null -> requestBuilder.thumbnail(Glide.with(context).load(thumbnail))
        placeholder != null -> requestBuilder.placeholder(placeholder)
        else -> {
            requestBuilder.placeholder(CircularProgressDrawable(context).apply {
                strokeWidth = 3F
                centerRadius = 64F
                start()
            })
        }
    }

    requestBuilder
        .error(R.drawable.ic_broken_image)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(this)
}

inline fun <T> List<T>.indexOrNull(predicate: (T) -> Boolean): Int? {
    for ((index, item) in this.withIndex()) {
        if (predicate(item))
            return index
    }
    return null
}

inline fun <T> List<T>.contains(predicate: (T) -> Boolean): Boolean {
    for (item in this) {
        if (predicate(item))
            return true
    }
    return false
}

fun MutableLiveData<String>.isEmpty() = value?.length ?: 0 == 0

val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> appComponent
        else -> this.applicationContext.appComponent
    }