package ru.maxim.barybians.utils

import android.content.res.ColorStateList
import android.graphics.ColorFilter
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("errorText")
fun setErrorText(view: TextInputLayout, errorMessage: String?) {
    view.error = errorMessage
}

@BindingAdapter("errorText")
fun setErrorText(view: TextInputLayout, @StringRes errorMessage: Int?) {
    view.error = if (errorMessage != null) {
        view.context.getString(errorMessage)
    } else {
        null
    }
}

@BindingAdapter(value = ["image", "placeholder", "thumbnail"], requireAll = false)
fun loadImage(
    imageView: ImageView,
    image: String?,
    @DrawableRes placeholder: Int?,
    thumbnail: String?
) {
    if (image.isNullOrBlank()) return
    imageView.load(url = image, placeholder = placeholder, thumbnail = thumbnail)
}

@BindingAdapter("tintColor")
fun ImageView.setTintColor(@ColorRes color: Int) {
    setColorFilter(color)
}

@BindingAdapter("drawableTintColor")
fun TextView.drawableTintColor(@ColorInt tintColor: Int) {
    TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(tintColor))
}