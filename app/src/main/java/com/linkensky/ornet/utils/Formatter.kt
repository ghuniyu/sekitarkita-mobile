package com.linkensky.ornet.utils

import com.linkensky.ornet.Const
import java.text.SimpleDateFormat
import java.util.*

class Formatter {

    /**
     * Interface used to format dates before they were displayed (e.g. dialogs time, messages date headers etc.).
     */
    interface Formatter {

        /**
         * Formats an string representation of the date object.
         *
         * @param date The date that should be formatted.
         * @return Formatted text.
         */
        fun format(date: Date): String
    }

    enum class Template private constructor(private val template: String) {
        STRING_DAY_MONTH_YEAR("d MMMM yyyy"),
        STRING_DAY_MONTH("d MMMM"),
        STRING_WEEK_OF_DAY_DAY_MONTH_YEAR("E, dd MMM yyyy"),
        TIME("HH:mm");

        fun get(): String {
            return template
        }
    }

    companion object {
        fun datetify(date: String): String? {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            return date2str(format.parse(date))
        }

        fun dateFormat(date: String, template: Template): String {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            return dateFormat(format.parse(date), template.get())
        }

        fun dateFormat(date: Date, template: Template): String {
            return dateFormat(date, template.get())
        }

        fun dateFormat(date: Date?, format: String): String {
            return if (date == null) "" else SimpleDateFormat(format, Locale.getDefault())
                .format(date)
        }

        fun date2str(date: Date): String? {
            val format = SimpleDateFormat("E, dd MMM yyyy - HH:mm", Locale.getDefault())
            return format.format(date)
        }

        fun formatDate(stringDate: String): String {
            val dateFormatted =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(stringDate)
            return SimpleDateFormat("d MMM yyyy HH:mm", Locale.getDefault()).format(dateFormatted)
        }

        fun epoch2str(epoch: String): String? {
            val format = SimpleDateFormat("E, dd MMM yyyy - HH:mm", Locale.getDefault())
            val date = Date(epoch.toLong())
            return format.format(date)
        }

        fun redacted(content: Array<Int>): String {
            return content.joinToString(separator = "", transform = {
                it.toChar().toString()
            })
        }

        fun low(what: String): String {
            return what.toLowerCase(Locale.getDefault())
        }

        fun String?.statusify(): String {
            return when (this) {
                "positive" -> "Pengguna Positif"
                "odp" -> "Pengguna ODP"
                "pdp" -> "Pengguna PDP"
                "otg" -> "Pengguna OTG"
                "traveler" -> "Pelaku Perjalanan"
                "healthy" -> "Pengguna Sehat"
                else -> ""
            }
        }
    }
}