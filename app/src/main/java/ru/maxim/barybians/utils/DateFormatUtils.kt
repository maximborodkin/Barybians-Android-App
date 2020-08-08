package ru.maxim.barybians.utils

import android.content.Context
import android.text.format.DateUtils
import ru.maxim.barybians.R
import java.text.SimpleDateFormat
import java.util.*

object DateFormatUtils : DateUtils() {

    var currentLocale: Locale = Locale("ru","RU")

    fun simplifyDate(timestamp: Long, context: Context, hasTime: Boolean): String {
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        return when {
            date.get(Calendar.DATE) == today.get(Calendar.DATE) -> context.getString(R.string.today)
            date.get(Calendar.DATE) == today.get(Calendar.DATE) - 1 -> context.getString(R.string.yesterday)
            date.get(Calendar.DATE) == today.get(Calendar.DATE) + 1 -> context.getString(R.string.tomorrow)
            date.get(Calendar.YEAR) == today.get(Calendar.YEAR) ->
                SimpleDateFormat("dd MMM", currentLocale).format(Date(timestamp)).replace(".", "")
            else ->
                SimpleDateFormat("dd MMM yyyy", currentLocale).format(Date(timestamp)).replace(".", "")
        } + if (hasTime) " ${getTime(timestamp)}" else ""
    }

    fun getTime(timestamp: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        return "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
    }

    fun dateToString(date: Long) =
        SimpleDateFormat("dd MMM yyyy", currentLocale).format(Date(date)).replace(".", "")

    fun getCurrentDay(): String = SimpleDateFormat("dd", currentLocale).format(Date())

    fun getCurrentMonth(): String = SimpleDateFormat("MMM", currentLocale).format(Date())
}