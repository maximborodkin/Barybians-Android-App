package ru.maxim.barybians.utils

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