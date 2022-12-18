package io.github.lepitar.gudeokparty.plugin.util

import org.bukkit.ChatColor

object textColor {

    fun ChatColor.bold(): String {
        return "${this}${ChatColor.BOLD}"
    }

}