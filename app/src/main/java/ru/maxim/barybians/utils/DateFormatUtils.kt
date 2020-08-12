package ru.maxim.barybians.utils

import android.content.Context
import android.text.format.DateUtils
import ru.maxim.barybians.R
import java.text.SimpleDateFormat
import java.util.*

object DateFormatUtils : DateUtils() {

    var currentLocale: Locale = Locale("ru","RU")
    lateinit var context: Context

    fun getSimplifiedDate(timestamp: Long): String {
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        return when {
            date.get(Calendar.DATE) == today.get(Calendar.DATE) -> context.getString(R.string.today)
            date.get(Calendar.DATE) == today.get(Calendar.DATE) - 1 -> context.getString(R.string.yesterday)
            date.get(Calendar.DATE) == today.get(Calendar.DATE) + 1 -> context.getString(R.string.tomorrow)
            date.get(Calendar.YEAR) == today.get(Calendar.YEAR) ->
                SimpleDateFormat("dd MMM", currentLocale)
                    .format(Date(timestamp)).replace(".", "")
            else ->
                SimpleDateFormat("dd MMM yyyy", currentLocale)
                    .format(Date(timestamp)).replace(".", "")
        } + " " + getTime(timestamp)
    }

    fun getDate(timestamp: Long, hasTime: Boolean) {
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        val hasYear = date.get(Calendar.YEAR) != today.get(Calendar.YEAR)
        SimpleDateFormat("dd MMM ${if (hasYear) "yyyy" else ""}", currentLocale)
            .format(Date(timestamp)).replace(".", "")
    }

    fun getTime(timestamp: Long) =
        SimpleDateFormat("HH:mm", currentLocale).format(Date(timestamp)).replace(".", "")

}