package io.github.lepitar.gudeokparty.plugin.realtime

import java.util.*

/**
 * @author Nemo
 */
class TimeAdapter(from: Date, to: Date, type: Type) {
    private val from: Date
    private val to: Date
    val tickOffset: Int
    val tickDuration: Int

    init {
        this.from = from
        this.to = to
        tickOffset = type.offset
        tickDuration = type.period
    }

    val isValid: Boolean
        get() {
            val time = System.currentTimeMillis()
            return from.time <= time && time < to.time
        }

    fun getFrom(): Date {
        return from
    }

    fun getTo(): Date {
        return to
    }

    val currentTick: Long
        get() {
            val from: Long = from.time
            val period: Long = to.time - from
            val time = System.currentTimeMillis()
            val current = time - from
            val tick = tickDuration * current / period
            return tickOffset + tick
        }

    enum class Type(val offset: Int, val period: Int) {
        DAY(22835, 14315), NIGHT(37150, 9685)
    }
}