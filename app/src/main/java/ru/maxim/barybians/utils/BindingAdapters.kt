package ru.maxim.barybians.utils

import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import ru.maxim.barybians.ui.view.AvatarView

@BindingAdapter("errorText")
fun setErrorText(view: TextInputLayout, errorMessage: String?) {
    view.error = errorMessage
}

@BindingAdapter("errorText")
fun setErrorText(view: TextInputLayout, @StringRes errorMessage: Int?) {
    view.error = if (errorMessage != null) view.context.getString(errorMessage) else null
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

@BindingAdapter("bindText")
fun TextView.bindText(@StringRes stringRes: Int?) {
    text = if (stringRes != null) context.getString(stringRes) else String()
}

@BindingAdapter("iconEnd")
fun TextView.drawableEnd(@DrawableRes drawableResId: Int) {
    if (drawableResId <= 0x0) return
    this.setDrawableEnd(drawableResId)
}

@BindingAdapter("startEnd")
fun TextView.drawableStart(@DrawableRes drawableResId: Int) {
    if (drawableResId <= 0x0) return
    this.setDrawableStart(drawableResId)
}

@BindingAdapter("isOnline")
fun AvatarView.isOnline(isOnline: Boolean) {
    this.isOnline = isOnline
}