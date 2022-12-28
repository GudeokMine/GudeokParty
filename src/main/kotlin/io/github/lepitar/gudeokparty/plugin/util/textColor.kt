package io.github.lepitar.gudeokparty.plugin.util

import org.bukkit.ChatColor
import net.md_5.bungee.api.ChatColor as BungeeChatColor

object textColor {

    fun ChatColor.bold(): String {
        return "${this}${ChatColor.BOLD}"
    }

    fun BungeeChatColor.bold(): String {
        return "${this}${ChatColor.BOLD}"
    }

}