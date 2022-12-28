package io.github.lepitar.gudeokparty.plugin.gatcha

import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin
import io.github.lepitar.gudeokparty.plugin.gatcha.item.GachaItem
import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class GachaListener: Listener {
    @EventHandler
    fun clickEvent(e: InventoryClickEvent) {
        val player = e.whoClicked
        val inventory = e.clickedInventory
        if (e.view.title == "${ChatColor.GOLD.bold()}가챠") {
            val item = e.currentItem
            e.isCancelled = true
            if (item != null && item.type != Material.AIR) {
                Gatcha.gachaList.forEach { it ->
                    if (item == it.clickedItem()) {
                        if (!it.purchase(player as Player)) {
                            return
                        }
                        val items = it.items
                        inventory!!.setItem(e.slot, ItemStack(Material.AIR))
                        //random weight gacha
                        var pitch = 1f
                        val itemChanging = Bukkit.getScheduler().runTaskTimer(GudeokpartyPlugin.instance, Runnable {
                            if (pitch > 1.5f)
                                pitch = 1f
                            inventory.setItem(e.slot, it.items.random().itemStack)
                            player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL, Sound.Source.PLAYER, 1f, pitch))
                            pitch += 0.2f
                        }, 15, 3)
                        Bukkit.getScheduler().runTaskLater(GudeokpartyPlugin.instance, Runnable {
                            itemChanging.cancel()
                            var totalWeight = 0.0
                            for (i in items) {
                                totalWeight += i.weight
                            }

                            val candidate: ArrayList<GachaItem> = arrayListOf()
                            for (i in items) {
                                candidate.add(GachaItem(i.itemStack, i.weight / totalWeight))
                            }
                            //sort candidate weight asc
                            candidate.sortedBy { it.weight }
                            val pivot = Math.random()

                            var acc = 0.0
                            for (i in candidate) {
                                acc += i.weight
                                if (acc >= pivot) {
                                    inventory.setItem(e.slot, i.itemStack)
                                    player.sendMessage("${ChatColor.GOLD.bold()}${i.itemStack.type.name} ${ChatColor.WHITE.bold()}획득!")
                                    player.world.dropItem(player.location, i.itemStack)
                                    player.playSound(Sound.sound(org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL, Sound.Source.PLAYER, 1f, 1.8f))
                                    break
                                }
                            }
                            Bukkit.getServer().scheduler.runTaskLater(GudeokpartyPlugin.instance, Runnable {
                                inventory.setItem(e.slot, it.clickedItem())
                            }, 10)
                        }, 4 * 20)
                    }
                }
            }
        }
    }
}