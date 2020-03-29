package id.ghuniyu.sekitar.utils

import java.text.SimpleDateFormat
import java.util.*

class Formatter {
    companion object {
        fun datetify(date: String): String? {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            return date2str(format.parse(date))
        }

        fun date2str(date: Date): String? {
            val format = SimpleDateFormat("E, dd MMM yyyy - HH:mm", Locale.getDefault())
            return format.format(date)
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
    }
}