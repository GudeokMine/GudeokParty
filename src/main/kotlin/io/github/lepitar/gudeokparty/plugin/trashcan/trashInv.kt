package io.github.lepitar.gudeokparty.plugin.trashcan

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object trashInv {
    val inventory = Bukkit.createInventory(null, 27, "§c휴지통").apply {
        for (i in 0..26) {
            setItem(i, ItemStack(Material.RED_STAINED_GLASS_PANE))
        }
        setItem(13, ItemStack(Material.AIR))
    }

    fun openInventory(player: Player) {
        player.openInventory(inventory)
    }
}