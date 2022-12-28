package io.github.lepitar.gudeokparty.plugin.gamble.Indian

import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.econ
import io.github.lepitar.gudeokparty.plugin.gamble.gameManager.indianPokerGame
import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import org.bukkit.ChatColor
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
        val clickedBlock = e.clickedBlock

        indianPokerGame.find { it.loc == clickedBlock?.location }?.let {
            e.isCancelled = true
            if (econ.getBalance(player) < 1000.0) {
                player.sendMessage("${ChatColor.RED.bold()}최소금액 1000원이 필요합니다.")
                return
            }
            val alreadyJoin = indianPokerGame.find { it.players.contains(player) }
            if (alreadyJoin != null) {
                player.sendMessage("${ChatColor.RED.bold()}이미 참가중인 방이 있습니다")
                return
            }
            player.sendMessage("${ChatColor.GREEN.bold()}입장되었습니다")
            it.join(player)
            return
        }

        for (game in indianPokerGame) {
            if (game.players.contains(player) && game.start) {
                e.isCancelled = true
                val goDie = player.inventory.itemInMainHand ?: return
                if (goDie.type == Material.AIR) return
                if (goDie.itemMeta.displayName == "좌: 고 / 우: 다이") {
                    if (Action.LEFT_CLICK_AIR == e.action || Action.LEFT_CLICK_BLOCK == e.action) {
                        game.progress?.go(player)
                    } else if (Action.RIGHT_CLICK_AIR == e.action || Action.RIGHT_CLICK_BLOCK == e.action) {
                        game.progress?.die(player)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val player = e.player
        indianPokerGame.find { IndianPoker::players == player }?.progress?.forceQuit(player)
    }

}