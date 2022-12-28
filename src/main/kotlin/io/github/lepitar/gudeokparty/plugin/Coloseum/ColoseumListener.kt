package io.github.lepitar.gudeokparty.plugin.Coloseum

import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.coloseum
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.checkerframework.checker.units.qual.C

class ColoseumListener: Listener {

    @EventHandler
    fun onPlayerDamaged(e: EntityDamageByEntityEvent) {
        val damager = e.damager
        val entity = e.entity
        if (damager is Player && entity is Player) {
            if (e.damage >= entity.health) {
                e.isCancelled = true
                if (coloseum!!.isInColoseum(entity) && coloseum!!.start) {
                    e.entity.teleport(Bukkit.getWorld("world")!!.spawnLocation)
                    coloseum!!.loose(entity)
                }
            }
        }
    }

    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        if (coloseum!!.isInColoseum(e.player) && coloseum!!.bettable) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        if (coloseum!!.isInColoseum(e.player) && coloseum!!.start) {
            coloseum!!.loose(e.player)
        }
    }

    @EventHandler
    fun onShoot(e: EntityShootBowEvent) {
        if (e.entity is Player) {
            if (coloseum!!.isInColoseum(e.entity as Player) && coloseum!!.bettable) {
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (coloseum!!.isInColoseum(e.player) && coloseum!!.bettable) {
            e.isCancelled = true
        }
    }

}