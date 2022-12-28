package io.github.lepitar.gudeokparty.plugin.gatcha

import com.artemis.the.gr8.lib.kyori.adventure.text.Component
import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.econ
import io.github.lepitar.gudeokparty.plugin.gatcha.item.GachaItem
import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class GachaClass(val name: String, val description: String, val price: Int) {
    var items: List<GachaItem> = mutableListOf()
//    var inventory = Bukkit.createInventory(null, 24, name)
    var clickedItem: ItemStack = ItemStack(Material.ENDER_CHEST).apply {
        this.itemMeta = this.itemMeta.clone().apply {
            this.setDisplayName(name)
        }
    }

    fun setListItem(list: List<GachaItem>) {
        items = list.apply {
            sortedByDescending { it.weight }
        }
        clickedItem.apply {
            this.itemMeta = this.itemMeta.clone().apply {
                val lore = mutableListOf<String>()
                lore.add("$description ${ChatColor.GOLD.bold()}$price${ChatColor.WHITE.bold()}원")
                lore.add("\n")
                lore.add("${ChatColor.RESET}${ChatColor.WHITE.bold()}획득가능한 아이템")
                for (i in 1..4) {
                    lore.add("${ChatColor.RESET}${ChatColor.WHITE.bold()}- ${items[i].itemStack.type.name}")
                }
                this.lore = lore
            }
        }
    }

    fun clickedItem(): ItemStack {
        return clickedItem
    }

    fun purchase(player: Player): Boolean {
        //deposit money
        if (econ.getBalance(player) < price) {
            player.sendMessage("${ChatColor.RED.bold()}돈이 부족합니다!")
            return false
        }
        econ.withdrawPlayer(player, price.toDouble())
        return true
    }
}