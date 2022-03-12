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
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import ru.maxim.barybians.App
import ru.maxim.barybians.R
import ru.maxim.barybians.di.AppComponent
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR
import java.util.Calendar.DATE

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

fun TextView.clearDrawables() = TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(this, 0, 0, 0, 0)

fun Context?.toast(text: String?) {
    if (this != null && text.isNotNullOrBlank()) Toast.makeText(this, text, LENGTH_SHORT).show()
}
fun Context?.toast(@StringRes resource: Int?) {
    if (this != null && resource != null) Toast.makeText(this, resource, LENGTH_SHORT).show()
}
fun Context?.longToast(text: String?) {
    if (this != null && text.isNotNullOrBlank()) Toast.makeText(this, text, LENGTH_LONG).show()
}
fun Context?.longToast(@StringRes resource: Int?) {
    if (this != null && resource != null) Toast.makeText(this, resource, LENGTH_LONG).show()
}

@SuppressLint("CheckResult")
fun ImageView.load(
    url: String?,
    thumbnail: String? = null,
    @DrawableRes placeholder: Int? = null,
    blur: Int? = null
) {
    if (url.isNullOrBlank()) return
    val requestBuilder = Glide.with(context).load(url)

    when {
        thumbnail != null -> requestBuilder.thumbnail(Glide.with(context).load(thumbnail))
        placeholder != null -> requestBuilder.placeholder(placeholder)
        else -> {
            requestBuilder.placeholder(CircularProgressDrawable(context).apply {
                setColorSchemeColors(ContextCompat.getColor(context, R.color.colorPrimary))
                strokeWidth = 3F
                centerRadius = 64F
                start()
            })
        }
    }

    if (blur != null && blur > 0) {
        requestBuilder.apply(RequestOptions.bitmapTransform(BlurTransformation(blur)))
    }

    requestBuilder
        .error(R.drawable.ic_broken_image)
        .into(this)
}

inline fun <reified T> List<T>.contains(predicate: (T) -> Boolean): Boolean {
    for (item in this) {
        if (predicate(item))
            return true
    }
    return false
}

fun View?.show() { this?.isVisible = true }
fun View?.hide() { this?.isGone = true }
fun View?.enable() { this?.isEnabled = true }
fun View?.disable() { this?.isEnabled = false }

inline fun <reified T> List<T>.transform(action: (T) -> Unit): List<T> {
    this.forEach(action)
    return this
}

fun simpleDate(date: Date?, hasTime: Boolean = true): String {
    if (date == null) return String()
    val today = Calendar.getInstance()
    val calendar = Calendar.getInstance().also { it.timeInMillis = date.time }
    return when {
        calendar[YEAR] == today[YEAR] &&
        calendar[MONTH] == calendar[MONTH] &&
        calendar[DATE] == today[DATE] -> SimpleDateFormat("HH:mm", Locale.getDefault())
        calendar[YEAR] == today[YEAR] -> SimpleDateFormat("dd MMM${if (hasTime) " HH:mm" else ""}", Locale.getDefault())
        else -> SimpleDateFormat("dd MMM yyyy${if (hasTime) " HH:mm" else ""}", Locale.getDefault())
    }.format(date)
}

fun years(date: Date?): Int {
    val now = Calendar.getInstance()
    val birthDate = Calendar.getInstance().apply { time = date ?: Date() }
    return if (now[Calendar.DAY_OF_YEAR] < birthDate[Calendar.DAY_OF_YEAR]) now[YEAR] - birthDate[YEAR] - 1
    else now[YEAR] - birthDate[YEAR]
}
fun date(date: Date): String = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
fun time(date: Date): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)

val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> appComponent
        else -> this.applicationContext.appComponent
    }