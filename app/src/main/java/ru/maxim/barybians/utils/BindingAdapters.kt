package ru.maxim.barybians.utils

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
    if (image.isNullOrEmpty()) return
    imageView.load(url = image, placeholder = placeholder, thumbnail = thumbnail)
}