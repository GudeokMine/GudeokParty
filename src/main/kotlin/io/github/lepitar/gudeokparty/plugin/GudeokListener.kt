package io.github.lepitar.gudeokparty.plugin

import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.board
import io.github.lepitar.gudeokparty.plugin.prefix.Nametag
import io.github.lepitar.gudeokparty.plugin.prefix.prefixManager
import io.github.lepitar.gudeokparty.plugin.util.dataConfig
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class GudeokListener: Listener {

    val config = dataConfig.customConfig!!

    @EventHandler
    fun onChat(e: AsyncPlayerChatEvent) {
        //format <%1$s> %2$s
//        e.format = e.format.replace("<", "").replace(">", "")
//            .replace("\$s", "")
//            .replace("%1",
//                ChatColor.translateAlternateColorCodes('&', prefixManager.getPrefix(e.player)
//                        + "${e.player.name}:"))
//            .replace("%2",
//                ChatColor.translateAlternateColorCodes('&', e.message))
        e.format = "${ChatColor.translateAlternateColorCodes('&', prefixManager.getPrefix(e.player))}${e.player.name}: ${ChatColor.translateAlternateColorCodes('&', e.message)}"
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val path = "player.${e.player.uniqueId}"
        val valid = config.getString("${path}.name")
        val prefix = Nametag(e.player).setPrefix(prefixManager.parsePrefix(e.player)).apply {
            build()
        }
        prefixManager.prefixList.add(prefix)

        if (valid == null) {
            config.set("${path}.name", e.player.name)
            dataConfig.save()
        }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val prefix = prefixManager.prefixList.find { it.player.uniqueId == e.player.uniqueId }
        prefix.apply { this!!
            destroy()
            prefixManager.prefixList.remove(this)
        }
    }
}