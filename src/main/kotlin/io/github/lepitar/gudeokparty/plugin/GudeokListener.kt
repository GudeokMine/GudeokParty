package io.github.lepitar.gudeokparty.plugin

import de.epiceric.shopchest.event.ShopBuySellEvent
import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.board
import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.coloseum
import io.github.lepitar.gudeokparty.plugin.TaxListener.MoneyHistory
import io.github.lepitar.gudeokparty.plugin.prefix.Nametag
import io.github.lepitar.gudeokparty.plugin.prefix.chingho.chinhoInventory
import io.github.lepitar.gudeokparty.plugin.prefix.prefixManager
import io.github.lepitar.gudeokparty.plugin.util.dataConfig
import io.github.lepitar.gudeokparty.plugin.util.dataConfig.customConfig
import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.time.LocalDateTime

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
        val time = LocalDateTime.now()
        val path = "player.${e.player.uniqueId}"
        val valid = config.getString("${path}.name")
        val prefix = Nametag(e.player).setPrefix(prefixManager.parsePrefix(e.player))
        if (time.dayOfMonth == 28 && time.monthValue == 12) {
            customConfig!!.set("permission.chingho.tester", customConfig!!.getStringList("permission.chingho.tester").apply { if (!this.contains(e.player.name)) add(e.player.name) })
            dataConfig.save()
        }
        prefixManager.prefixList.apply {
            add(prefix)
            for (p in Bukkit.getOnlinePlayers()) {
                if (p.uniqueId != e.player.uniqueId) {
                    prefix.fakeServer.addPlayer(p)
                }
            }
            this.forEach {
                if (it.player.uniqueId != e.player.uniqueId) {
                    it.fakeServer.addPlayer(e.player)
                }
            }
        }
        prefix.build()
        chinhoInventory.loadChingHo(e.player)

        if (coloseum!!.joinable) {
            e.player.sendMessage("${ChatColor.GREEN.bold()}콜로세움에 참가가능합니다! 명령어 /colo join")
            e.player.sendMessage("${ChatColor.GREEN.bold()}9시마다 경기진행됩니다.")
        }
        if (valid == null) {
            config.set("${path}.name", e.player.name)
            config.set("${path}.pvp", false)
            dataConfig.save()
        }
    }

    @EventHandler
    fun onBuySell(e: ShopBuySellEvent) {
        if (e.type == ShopBuySellEvent.Type.SELL) {
            MoneyHistory().recordMoney(e.player, e.newPrice.toInt())
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

    @EventHandler
    fun onDead(e: PlayerDeathEvent) {
        val invSave = customConfig!!.getBoolean("player.${e.player.uniqueId}.invSave", false)

        if (invSave) {
            e.drops.clear()
            e.keepInventory = true
            customConfig!!.set("player.${e.player.uniqueId}.invSave", invSave.not())
        }
    }

    @EventHandler
    fun onAttack(e: EntityDamageByEntityEvent) {
        if (e.entity is Player && e.damager is Player) {
            val player = e.entity as Player
            val damager = e.damager as Player
            val playerPvp = customConfig!!.getBoolean("player.${player.uniqueId}.pvp")
            val damagerPvp = customConfig!!.getBoolean("player.${damager.uniqueId}.pvp")
            if (!damagerPvp) {
                e.isCancelled = true
                damager.sendMessage("${ChatColor.RED.bold()}PVP를 허용하지 않았습니다.")
                return
            }
            if (!playerPvp) {
                e.isCancelled = true
                damager.sendMessage("${ChatColor.RED.bold()}상대방이 PVP를 허용하지 않습니다")
                return
            }
        }
    }
}