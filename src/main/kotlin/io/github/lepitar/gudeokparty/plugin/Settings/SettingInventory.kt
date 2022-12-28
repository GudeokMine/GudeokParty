package io.github.lepitar.gudeokparty.plugin.Settings

import io.github.lepitar.gudeokparty.plugin.TaxListener.InventorySaveTax
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import io.github.lepitar.gudeokparty.plugin.util.dataConfig.customConfig
import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import org.bukkit.ChatColor
import org.bukkit.inventory.Inventory

object SettingInventory {

    fun openSetting(player: Player) {
        val inventory = player.server.createInventory(null, 27, "§e설정")
        inventory.setItem(11, ItemStack(
            Material.PLAYER_HEAD
        ).apply {
            val meta = (itemMeta as SkullMeta).apply {
                setOwningPlayer(player)
                setDisplayName("§e${player.name}님의 정보")
            }
            itemMeta = meta
        })
        inventory.setItem(13, ItemStack(Material.DIAMOND_SWORD).apply {
            itemMeta = itemMeta.clone().apply {
                val pvp = customConfig!!.getBoolean("player.${player.uniqueId}.pvp", false)
                if (pvp) {
                    setDisplayName("${ChatColor.GREEN.bold()}PvP: §a활성화")
                    lore = listOf("${ChatColor.RESET}§7클릭하여 비활성화")
                } else {
                    setDisplayName("${ChatColor.RED.bold()}PvP: §c비활성화")
                    lore = listOf("${ChatColor.RESET}§7클릭하여 활성화")
                }
            }
        })
        inventory.setItem(15, ItemStack(Material.PAPER).apply {
            itemMeta = itemMeta.clone().apply {
                val invSave = customConfig!!.getBoolean("player.${player.uniqueId}.invSave", false)
                if (invSave) {
                    setDisplayName("${ChatColor.GREEN.bold()}인벤토리 저장: §a활성화")
                    lore = listOf("${ChatColor.RESET}${ChatColor.WHITE.bold()}가격: ${InventorySaveTax.totalTax(player)}원")
                } else {
                    setDisplayName("${ChatColor.RED.bold()}인벤토리 저장: §c비활성화")
                    lore = listOf("${ChatColor.RESET}${ChatColor.WHITE.bold()}가격: ${InventorySaveTax.totalTax(player)}원")
                }
            }
        })
        player.openInventory(inventory)
    }

    fun updateInventory(player: Player, inventory: Inventory) {
        inventory.setItem(11, ItemStack(
            Material.PLAYER_HEAD
        ).apply {
            val meta = (itemMeta as SkullMeta).apply {
                setOwningPlayer(player)
                setDisplayName("§e${player.name}님의 정보")
            }
            itemMeta = meta
        })
        inventory.setItem(13, ItemStack(Material.DIAMOND_SWORD).apply {
            itemMeta = itemMeta.clone().apply {
                val pvp = customConfig!!.getBoolean("player.${player.uniqueId}.pvp", false)
                if (pvp) {
                    setDisplayName("${ChatColor.GREEN.bold()}PvP: §a활성화")
                } else {
                    setDisplayName("${ChatColor.RED.bold()}PvP: §c비활성화")
                }
            }
        })
        inventory.setItem(15, ItemStack(Material.PAPER).apply {
            itemMeta = itemMeta.clone().apply {
                val invSave = customConfig!!.getBoolean("player.${player.uniqueId}.invSave", false)
                if (invSave) {
                    setDisplayName("${ChatColor.GREEN.bold()}인벤토리 저장: §a활성화")
                    lore = listOf("${ChatColor.RESET}${ChatColor.WHITE.bold()}가격: ${InventorySaveTax.totalTax(player)}원")
                } else {
                    setDisplayName("${ChatColor.RED.bold()}인벤토리 저장: §c비활성화")
                    lore = listOf("${ChatColor.RESET}${ChatColor.WHITE.bold()}가격: ${InventorySaveTax.totalTax(player)}원")
                }
            }
        })
    }
}