package ru.maxim.barybians.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import ru.maxim.barybians.App
import ru.maxim.barybians.R
import ru.maxim.barybians.di.AppComponent
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.DATE
import java.util.Calendar.YEAR
import java.util.Calendar.MONTH

fun Any?.isNull() = this == null
fun Any?.isNotNull() = !this.isNull()

fun CharSequence?.isNotNullOrBlank(): Boolean = !this.isNullOrBlank()

fun TextView.setDrawableStart(@DrawableRes drawableResource: Int) =
    TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
        this,
        AppCompatResources.getDrawable(context, drawableResource), //start
        compoundDrawablesRelative[1],                              //top
        compoundDrawablesRelative[2],                              //end
        compoundDrawablesRelative[3]                               //bottom
    )

fun TextView.setDrawableEnd(@DrawableRes drawableResource: Int) =
    TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
        this,
        this.compoundDrawablesRelative[0],                         //start
        this.compoundDrawablesRelative[1],                         //top
        AppCompatResources.getDrawable(context, drawableResource), //end
        this.compoundDrawablesRelative[3]                          //bottom
    )

fun TextView.clearDrawables() =
    TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(this, 0, 0, 0, 0)

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
                setColorSchemeColors(ContextCompat.getColor(context, R.color.colorOnPrimary))
                strokeWidth = 3F
                centerRadius = 64F
                start()
            })
        }
    }

    requestBuilder
        .error(R.drawable.ic_broken_image)
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

fun View.show() {
    this.isVisible = true
}

fun View.hide() {
    this.isGone = true
}

fun MutableLiveData<String>.isEmpty() = value?.isEmpty()

fun <T> List<T>.transform(action: (T) -> Unit): List<T> {
    this.forEach(action)
    return this
}

fun Date.simple(hasTime: Boolean = true): String {
    val today = Calendar.getInstance()
    val date = Calendar.getInstance().also { it.timeInMillis = this.time }
    return when {
        date[YEAR] == today[YEAR] && date[MONTH] == date[MONTH] && date[DATE] == today[DATE] ->
            SimpleDateFormat("HH:mm", Locale.getDefault())
        date[YEAR] == today[YEAR] -> SimpleDateFormat("dd MMM${if (hasTime) " HH:mm" else ""}", Locale.getDefault())
        else -> SimpleDateFormat("dd MMM yyyy${if (hasTime) " HH:mm" else ""}", Locale.getDefault())
    }.format(this)
}

fun Date.date(): String = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(this)

fun Date.time(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(this)

fun simpleDate(timestamp: Long, hasTime: Boolean = true) = Date(timestamp).simple(hasTime)
fun date(timestamp: Long) = Date(timestamp).date()
fun time(timestamp: Long) = Date(timestamp).time()

val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> appComponent
        else -> this.applicationContext.appComponent
    }