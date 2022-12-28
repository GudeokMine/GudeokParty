package io.github.lepitar.gudeokparty.plugin.Settings

import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.econ
import io.github.lepitar.gudeokparty.plugin.Settings.SettingInventory.updateInventory
import io.github.lepitar.gudeokparty.plugin.TaxListener.InventorySaveTax
import io.github.lepitar.gudeokparty.plugin.util.dataConfig
import io.github.lepitar.gudeokparty.plugin.util.dataConfig.customConfig
import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class SettingListener: Listener {

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.view.title == "§e설정") {
            e.isCancelled = true
            val player = e.whoClicked as Player
            val inventory = e.clickedInventory
            val item = e.currentItem ?: return

            if (item.type == Material.AIR) return

            if (item.type == Material.DIAMOND_SWORD|| item.itemMeta.hasLore()) {
                customConfig!!.set("player.${player.uniqueId}.pvp", customConfig!!.getBoolean("player.${player.uniqueId}.pvp").not())
            }

            if (item.type == Material.PAPER || item.itemMeta.hasLore()) {
                val curr_state = customConfig!!.getBoolean("player.${player.uniqueId}.invSave")
                if (curr_state) {
                    return
                }
                if (!curr_state) {
                    if (InventorySaveTax.totalTax(player) > econ.getBalance(player)) {
                        player.sendMessage("${ChatColor.RED.bold()}돈이 부족합니다.")
                        return
                    }

                    econ.withdrawPlayer(player, InventorySaveTax.totalTax(player).toDouble())

                }
                customConfig!!.set("player.${player.uniqueId}.invSave", curr_state.not())
            }
            dataConfig.save()
            updateInventory(player, inventory!!)
        }
        if (e.view.title == "§c휴지통") {
            e.isCancelled = true
            val item = e.currentItem ?: return
            if (item.type == Material.AIR) return
            if (item.type == Material.RED_STAINED_GLASS_PANE) return

            val player = e.whoClicked as Player
            e.clickedInventory?.setItem(e.slot, ItemStack(Material.AIR))
            player.sendMessage("${ChatColor.GOLD.bold()}${item.amount * 1}원을 받았습니다")
            econ.depositPlayer(player, (item.amount * 1).toDouble())
        }
    }

}