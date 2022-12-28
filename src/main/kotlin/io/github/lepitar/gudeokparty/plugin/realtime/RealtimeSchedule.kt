package io.github.lepitar.gudeokparty.plugin.realtime

import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.instance
import io.github.lepitar.gudeokparty.plugin.realtime.SunSet.getSunriseTime
import io.github.lepitar.gudeokparty.plugin.realtime.SunSet.getSunsetTime
import org.bukkit.Bukkit
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


class RealtimeSchedule : Runnable {

    private val latitude = instance.config.getDouble("latitude")

    private val longitude = instance.config.getDouble("longitude")

    private val timezone = instance.config.getInt("timezone")

    private var timeAdapter: TimeAdapter? = null

    init {
        updateTimeAdapter()
    }

    private fun updateTimeAdapter() {
        val calendar = Calendar.getInstance()
        val time = calendar.timeInMillis
        var sunrise: Date = getSunriseTime(calendar, latitude, longitude, timezone)

        if (time < sunrise.time) //자정 이후 일출 이전
        {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            val sunset: Date = getSunsetTime(calendar, latitude, longitude, timezone)
            timeAdapter = TimeAdapter(sunset, sunrise, TimeAdapter.Type.NIGHT)
            println("자정 이후 일출 이전 (새벽) " + timeAdapter!!.getFrom() + " " + timeAdapter!!.getTo())
            return
        }

        val sunset: Date = getSunsetTime(calendar, latitude, longitude, timezone)

        if (time < sunset.time) //해돋이 이후 일몰 이전
        {
            timeAdapter = TimeAdapter(sunrise, sunset, TimeAdapter.Type.DAY)
            println("일출 이후 일몰 이전 (낮)" + timeAdapter!!.getFrom() + " " + timeAdapter!!.getTo())
            return
        }

        //일몰 이후
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        sunrise = getSunriseTime(calendar, latitude, longitude, timezone)
        timeAdapter = TimeAdapter(sunset, sunrise, TimeAdapter.Type.NIGHT)
        println("일몰 이후 일출 이전 (밤)" + timeAdapter!!.getFrom() + " " + timeAdapter!!.getTo())
    }

    private var lastTick: Long = 0

    override fun run() {
        if (!timeAdapter!!.isValid) updateTimeAdapter()

        val tick = timeAdapter!!.currentTick

        if (lastTick != tick) {
            this.lastTick = tick
            val date = LocalDateTime.now()
            if (date.monthValue != 1 && date.dayOfMonth != 1 && date.hour < 9) {
                return
            }
            for (world in Bukkit.getWorlds()) {
                world.time = tick
            }
        }
    }
}