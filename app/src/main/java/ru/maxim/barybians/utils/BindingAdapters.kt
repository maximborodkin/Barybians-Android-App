package ru.maxim.barybians.utils

import com.google.android.material.textfield.TextInputLayout
import androidx.databinding.BindingAdapter

@BindingAdapter("errorText")
fun setErrorText(view: TextInputLayout, errorMessage: String?) {
    view.error = errorMessage
}