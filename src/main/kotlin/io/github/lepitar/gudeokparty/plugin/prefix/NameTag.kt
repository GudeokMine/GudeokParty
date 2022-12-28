package io.github.lepitar.gudeokparty.plugin.prefix

import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.board
import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.instance
import io.github.monun.tap.fake.FakeEntityServer
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.util.*


class Nametag(val player: Player) {


    val name = UUID.randomUUID().toString().replace("-", "").substring(0, 12)
    var prefix = ""
    private var team = board!!.registerNewTeam(name)
    val fakeServer = FakeEntityServer.create(instance)
    var armorStand = fakeServer.spawnEntity(player.eyeLocation.add(0.0,0.5, 0.0), ArmorStand::class.java).apply {
        updateMetadata {
            isVisible = false
            isSmall = true
            isMarker = true
            customName(Component.text(""))
            isCustomNameVisible = false
        }
    }

    private var scheduler: BukkitTask? = null

    init {
        team.addPlayer(player)
        scheduler = instance.server.scheduler.runTaskTimer(instance, Runnable {
            fakeServer.update()
            armorStand.moveTo(player.eyeLocation.add(0.0, 0.5, 0.0))
        }, 0L, 0L)
    }

    fun setPrefix(prefix: String): Nametag {
        this.prefix = prefix
        team.prefix(Component.text(ChatColor.translateAlternateColorCodes('&', prefix)))
        return this
    }

    fun build() {
        for (p: Player in Bukkit.getOnlinePlayers()) {
            p.scoreboard = board!!
            if (p.uniqueId != player.uniqueId) {
                fakeServer.addPlayer(p)
            }
        }
    }

    fun destroy() {
        team.unregister()
        fakeServer.clear()
        for (p: Player in Bukkit.getOnlinePlayers()) {
            if (p.uniqueId != player.uniqueId) {
                fakeServer.removePlayer(p)
            }
        }
        scheduler?.cancel()
    }
}