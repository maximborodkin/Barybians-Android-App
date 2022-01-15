package ru.maxim.barybians.utils

import android.content.res.Resources
import android.util.TypedValue

fun dpToPx(resources: Resources, dp: Int) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics)
        .toInt()