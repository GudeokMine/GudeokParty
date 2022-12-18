package io.github.lepitar.gudeokparty.plugin.gemble.Indian

import io.github.lepitar.gudeokparty.plugin.gemble.gameManager.indianList
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

class IndianPokerListener: Listener {

    @EventHandler
    fun onInteract(e: PlayerInteractEvent) {
        val player = e.player
        indianList.forEach { game ->
            if (game.p1.uniqueId == player.uniqueId || game.p2.uniqueId == player.uniqueId) {
                val goDie = player.inventory.itemInMainHand ?: return
                if (goDie.type == Material.AIR) return
                if (goDie.itemMeta.displayName == "좌: 고 / 우: 다이") {
                    if (Action.LEFT_CLICK_AIR == e.action || Action.LEFT_CLICK_BLOCK == e.action) {
                        game.go(player)
                    } else if (Action.RIGHT_CLICK_AIR == e.action || Action.RIGHT_CLICK_BLOCK == e.action) {
                        game.die(player)
                    }
                }
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val player = e.player
        indianList.forEach { game ->
            if (game.p1.uniqueId == player.uniqueId || game.p2.uniqueId == player.uniqueId) {
                game.end()
            }
        }
    }

}