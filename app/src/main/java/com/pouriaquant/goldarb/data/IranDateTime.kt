package com.pouriaquant.goldarb.data

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

object IranDateTime {
    private val iranOffset = ZoneOffset.ofHoursMinutes(3, 30)

    fun jalaliToIso(rawDate: String?, rawTime: String?): String? {
        val date = normalizeDigits(rawDate.orEmpty()).trim()
            .matchEntire(Regex("^(\\d{4})[/-](\\d{1,2})[/-](\\d{1,2})$"))
            ?: return null
        val time = normalizeDigits(rawTime.orEmpty()).trim()
            .matchEntire(Regex("^(\\d{1,2}):(\\d{2}):(\\d{2})$"))
            ?: return null
        val (jy, jm, jd) = date.destructured.toList().map(String::toInt)
        val (hour, minute, second) = time.destructured.toList().map(String::toInt)
        if (hour !in 0..23 || minute !in 0..59 || second !in 0..59) return null
        val gregorian = jalaliToGregorian(jy, jm, jd) ?: return null
        return runCatching {
            LocalDateTime.of(
                gregorian.year,
                gregorian.month,
                gregorian.day,
                hour,
                minute,
                second,
            ).toInstant(iranOffset).toString()
        }.getOrNull()
    }

    fun isFresh(sourceTimestamp: String?, receivedAt: Instant, maxSkewSeconds: Long = 120): Boolean {
        val source = runCatching { Instant.parse(sourceTimestamp) }.getOrNull() ?: return false
        return kotlin.math.abs(receivedAt.epochSecond - source.epochSecond) <= maxSkewSeconds
    }

    internal fun jalaliToGregorian(year: Int, month: Int, day: Int): GregorianDate? {
        if (year < 1 || month !in 1..12 || day !in 1..if (month <= 6) 31 else 30) return null
        val shiftedYear = year + 1_595
        var days = -355_668 + 365 * shiftedYear + (shiftedYear / 33) * 8 +
            ((shiftedYear % 33 + 3) / 4) + day +
            if (month < 7) (month - 1) * 31 else (month - 7) * 30 + 186
        var gregorianYear = 400 * (days / 146_097)
        days %= 146_097
        if (days > 36_524) {
            days -= 1
            gregorianYear += 100 * (days / 36_524)
            days %= 36_524
            if (days >= 365) days += 1
        }
        gregorianYear += 4 * (days / 1_461)
        days %= 1_461
        if (days > 365) {
            gregorianYear += (days - 1) / 365
            days = (days - 1) % 365
        }
        var gregorianDay = days + 1
        val leap = (gregorianYear % 4 == 0 && gregorianYear % 100 != 0) || gregorianYear % 400 == 0
        val monthLengths = intArrayOf(0, 31, if (leap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var gregorianMonth = 1
        while (gregorianDay > monthLengths[gregorianMonth]) {
            gregorianDay -= monthLengths[gregorianMonth]
            gregorianMonth += 1
        }
        return GregorianDate(gregorianYear, gregorianMonth, gregorianDay)
    }

    private fun normalizeDigits(value: String): String = buildString(value.length) {
        value.forEach { character ->
            append(
                when (character) {
                    in '۰'..'۹' -> '0' + (character - '۰')
                    in '٠'..'٩' -> '0' + (character - '٠')
                    else -> character
                },
            )
        }
    }
}

data class GregorianDate(val year: Int, val month: Int, val day: Int)
