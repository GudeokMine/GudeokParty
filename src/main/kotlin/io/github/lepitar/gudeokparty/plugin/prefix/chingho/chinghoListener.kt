package io.github.lepitar.gudeokparty.plugin.prefix.chingho

import io.github.lepitar.gudeokparty.plugin.util.dataConfig
import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.node.Node
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.Player
import org.bukkit.entity.Wither
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent

class chinghoListener: Listener {

    @EventHandler
    fun onClickInv(e: InventoryClickEvent) {
        val player = e.whoClicked
        val inv = e.clickedInventory

        if (e.currentItem == null) return

        if (e.currentItem!!.type == Material.AIR) return

        if (chinhoInventory.invList[player] == null) return

        if (chinhoInventory.invList[player] == inv) {
            chinhoInventory.equipChingHo(player as Player, e.currentItem!!)
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onDeathBoss(e: EntityDeathEvent) {
        val entity = e.entity

        if (entity is EnderDragon) {
            val killer = dataConfig.customConfig!!.getString("permission.chingho.dragon")
            if (killer == null) {
                Bukkit.broadcastMessage("${ChatColor.GREEN.bold()}${e.entity.killer?.name}님이 처음으로 드래곤을 잡으셨습니다 !")
                e.entity.killer!!.sendMessage("${ChatColor.GREEN.bold()}칭호를 획득하셨습니다. /칭호")
                dataConfig.customConfig!!.set("permission.chingho.dragon", e.entity.killer!!.name)
                dataConfig.save()
            }
        }

        if (entity is Wither) {
            val killer = dataConfig.customConfig!!.getString("permission.chingho.wither")
            if (killer == null) {
                Bukkit.broadcastMessage("${ChatColor.GREEN.bold()}${e.entity.killer?.name}님이 처음으로 위더를 잡으셨습니다 !")
                e.entity.killer!!.sendMessage("${ChatColor.GREEN.bold()}칭호를 획득하셨습니다. /칭호")
                dataConfig.customConfig!!.set("permission.chingho.wither", e.entity.killer!!.name)
                dataConfig.save()
            }
        }
    }

}