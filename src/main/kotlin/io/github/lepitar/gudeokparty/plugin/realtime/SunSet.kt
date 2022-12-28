package io.github.lepitar.gudeokparty.plugin.realtime

import java.util.*


/**
 * @author 괴도군
 * 출처 https://rinear.tistory.com/entry/javaandroid
 */
object SunSet {
    private const val PI = 3.141592
    private fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0
    }

    private fun getLastDay(uiYear: Int, ucMonth: Int): Int {
        when (ucMonth) {
            2 -> {
                return if (uiYear % 4 == 0) {        // 4로 나누어 떨어지는 해는 윤년임.
                    if (uiYear % 100 == 0) {    // 그중에서 100으로 나누어 떨어지는 해는 평년임
                        if (uiYear % 400 == 0) 29 else 28 // 그중에서 400으로 나누어 떨어지는 해는 윤년임.
                        // 평년
                    } else 29
                    // 윤년
                } else 28
                // else 평년
            }

            4, 6, 9, 11 -> return 30 // 30일
        }
        return 31 // 그외 31일
    }

    private fun calcJulianDay(uiYear: Int, ucMonth: Int, ucDay: Int): Int {
        var i: Int
        var iJulDay: Int
        iJulDay = 0
        i = 1
        while (i < ucMonth) {
            iJulDay += getLastDay(uiYear, i)
            i++
        }
        iJulDay += ucDay
        return iJulDay
    }

    private fun calcGamma(iJulDay: Int): Double {
        return 2.0 * PI / 365.0 * (iJulDay - 1)
    }

    private fun calcGamma2(iJulDay: Int, hour: Int): Double {
        return 2.0 * PI / 365.0 * (iJulDay - 1 + hour / 24.0)
    }

    // Return the equation of time value for the given date.
    private fun calcEqofTime(gamma: Double): Double {
        return 229.18 * (0.000075 + 0.001868 * Math.cos(gamma) - 0.032077 * Math.sin(gamma) - 0.014615 * Math.cos(2 * gamma) - 0.040849 * Math.sin(
            2 * gamma
        ))
    }

    // Return the solar declination angle (in radians) for the given date.
    private fun calcSolarDec(gamma: Double): Double {
        return 0.006918 - 0.399912 * Math.cos(gamma) + 0.070257 * Math.sin(gamma) - 0.006758 * Math.cos(2 * gamma) + 0.000907 * Math.sin(
            2 * gamma
        )
    }

    private fun degreeToRadian(angleDeg: Double): Double {
        return PI * angleDeg / 180.0
    }

    private fun radianToDegree(angleRad: Double): Double {
        return 180 * angleRad / PI
    }

    private fun calcHourAngle(latitude: Double, solarDec: Double, time: Int): Double {
        val latRad = degreeToRadian(latitude)
        val hour_angle = Math.acos(
            Math.cos(degreeToRadian(90.833)) / (Math.cos(latRad) * Math.cos(solarDec)) - Math.tan(latRad) * Math.tan(
                solarDec
            )
        )
        if (time == 1) {
            return hour_angle
        } else if (time == 0) {
            return -hour_angle
        }
        return 0.0
    }

    private fun calcSunriseGMT(iJulDay: Int, latitude: Double, longitude: Double): Double {
        val gamma = calcGamma(iJulDay)
        var eqTime = calcEqofTime(gamma)
        var solarDec = calcSolarDec(gamma)
        var hourAngle = calcHourAngle(latitude, solarDec, 1)
        var delta = longitude - radianToDegree(hourAngle)
        var timeDiff = 4.0 * delta
        var timeGMT = 720.0 + timeDiff - eqTime
        val gamma_sunrise = calcGamma2(iJulDay, (timeGMT / 60.0).toInt())
        eqTime = calcEqofTime(gamma_sunrise)
        solarDec = calcSolarDec(gamma_sunrise)
        hourAngle = calcHourAngle(latitude, solarDec, 1)
        delta = longitude - radianToDegree(hourAngle)
        timeDiff = 4.0 * delta
        timeGMT = 720.0 + timeDiff - eqTime
        return timeGMT
    }

    private fun calcSunsetGMT(iJulDay: Int, latitude: Double, longitude: Double): Double {
        // First calculates sunrise and approx length of day
        val gamma = calcGamma(iJulDay + 1)
        var eqTime = calcEqofTime(gamma)
        var solarDec = calcSolarDec(gamma)
        var hourAngle = calcHourAngle(latitude, solarDec, 0)
        var delta = longitude - radianToDegree(hourAngle)
        var timeDiff = 4.0 * delta
        var setTimeGMT = 720.0 + timeDiff - eqTime
        // first pass used to include fractional day in gamma calc
        val gamma_sunset = calcGamma2(iJulDay, (setTimeGMT / 60.0).toInt())
        eqTime = calcEqofTime(gamma_sunset)
        solarDec = calcSolarDec(gamma_sunset)
        hourAngle = calcHourAngle(latitude, solarDec, 0)
        delta = longitude - radianToDegree(hourAngle)
        timeDiff = 4.0 * delta
        setTimeGMT = 720.0 + timeDiff - eqTime // in minutes
        return setTimeGMT
    }

    fun getSunriseTime(
        year: Int,
        month: Int,
        day: Int,
        latitude: Double,
        longitude: Double,
        zone: Int,
        daySavings: Int
    ): Date {
        val julday = calcJulianDay(year, month, day)
        val timeLST = calcSunriseGMT(julday, latitude, longitude) - 60.0 * zone + daySavings // minutes
        val floatHour = timeLST / 60.0
        val hour = Math.floor(floatHour).toInt()
        val floatMinute = 60.0 * (floatHour - Math.floor(floatHour))
        val minute = Math.floor(floatMinute).toInt()
        val floatSec = 60.0 * (floatMinute - Math.floor(floatMinute))
        val second = Math.floor(floatSec).toInt()
        val calendar = Calendar.getInstance()
        calendar[year, month - 1, day, hour, minute] = second
        return calendar.time
    }

    fun getSunsetTime(
        year: Int,
        month: Int,
        day: Int,
        latitude: Double,
        longitude: Double,
        zone: Int,
        daySavings: Int
    ): Date {
        val julday = calcJulianDay(year, month, day)
        val timeLST = calcSunsetGMT(julday, latitude, longitude) - 60.0 * zone + daySavings // minutes
        val floatHour = timeLST / 60.0
        val hour = Math.floor(floatHour).toInt()
        val floatMinute = 60.0 * (floatHour - Math.floor(floatHour))
        val minute = Math.floor(floatMinute).toInt()
        val floatSec = 60.0 * (floatMinute - Math.floor(floatMinute))
        val second = Math.floor(floatSec).toInt()
        val calendar = Calendar.getInstance()
        calendar[year, month - 1, day, hour, minute] = second
        return calendar.time
    }

    fun getSunsetTime(calendar: Calendar, latitude: Double, longitude: Double, timeZone: Int): Date {
        return getSunsetTime(
            calendar[Calendar.YEAR], calendar[Calendar.MONTH] + 1,
            calendar[Calendar.DAY_OF_MONTH], latitude, longitude, timeZone, 0
        )
    }

    fun getSunriseTime(calendar: Calendar, latitude: Double, longitude: Double, timeZone: Int): Date {
        return getSunriseTime(
            calendar[Calendar.YEAR], calendar[Calendar.MONTH] + 1,
            calendar[Calendar.DAY_OF_MONTH], latitude, longitude, timeZone, 0
        )
    }
}