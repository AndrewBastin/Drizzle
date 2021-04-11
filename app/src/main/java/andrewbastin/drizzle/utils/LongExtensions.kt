package andrewbastin.drizzle.utils

import java.text.SimpleDateFormat
import java.util.*

fun Long.epochToDateString(dateFormat: String, locale: Locale = Locale.ROOT): String = SimpleDateFormat(dateFormat, locale).format(Date(this))