package io.github.lepitar.gudeokparty.plugin.shop

import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.ChatColor
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.entity.EntityCombustEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.ItemDespawnEvent
import org.bukkit.event.player.PlayerInteractEvent


class ItemcaseListener: Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntitiyPickupItem(e: EntityPickupItemEvent) {
        val item = e.item.uniqueId
        if (itemManager.isItem(item)) {
            e.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onItemDespawn(e: ItemDespawnEvent) {
        val item = e.entity.uniqueId
        if (itemManager.isItem(item)) {
            e.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onBlockBreakEvent(e: BlockBreakEvent) {
        for (itemcase in itemManager.itemList) {
            if (itemcase.loc == e.block.location || itemcase.sign == e.block.location) {
                e.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onBlockPlaceEvent(e: BlockPlaceEvent) {
        for (location in itemManager.itemList) {
            if (location.loc == e.block.location) {
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onEntityBurn(e: EntityCombustEvent) {
        for (itemcase in itemManager.itemList) {
            if (itemcase.dropItem?.uniqueId == e.entity.uniqueId) {
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onInteract(e: PlayerInteractEvent) {
        val player = e.player
        val block = e.clickedBlock

        if (player.isSneaking) {
            // 64 구입
            itemManager.itemList.forEach {
                if (it.sign == block?.location) {
                    var component: Component? = null
                    component = if (it.buy) {
                        shopComponent(it, 64)
                    } else {
                        shopNotBuyComponent(it, 64)
                    }
                    player.sendMessage(component)
                }
            }
        } else {
            itemManager.itemList.forEach {
                if (it.sign == block?.location) {
                    var component: Component? = null
                    component = if (it.buy) {
                        shopComponent(it, 1)
                    } else {
                        shopNotBuyComponent(it, 1)
                    }
                    player.sendMessage(component!!)
                }
            }
        }
    }

    @EventHandler
    fun onSignPlace(e: SignChangeEvent) {
        val player = e.player
        val block = e.block.state

        if (itemManager.registerPlayer.containsKey(player.uniqueId)) {
            val itemcase = itemManager.registerPlayer[player.uniqueId]!!
            itemcase.sign = block.location
            e.setLine(0, "${ChatColor.BOLD}[ ${itemcase.item.itemMeta.displayName} ]")
            e.setLine(1, "${ChatColor.BOLD}쉬프트 클릭 64개 구매")
            if (itemcase.buy) {
                e.setLine(2, "${ChatColor.BOLD}구매: ${itemcase.price}원")
            } else {
                e.setLine(2, "${ChatColor.BOLD}구매: 불가")
            }
            e.setLine(3, "${ChatColor.BOLD}판매: ${itemcase.price*80/100}원")
            val sign = e.block.state as Sign
            sign.isGlowingText = true
            sign.update()
            itemManager.detachPlayer(player)
            itemManager.saveItem(itemcase)
            player.sendMessage("아이템 표지판을 등록하였습니다.")
        }
    }

    fun shopComponent(it: ItemCase, amount: Int): Component {
        return Component.text("${ChatColor.DARK_GRAY.bold()}=".repeat(30) + "\n\n")
            .append(Component.text("${ChatColor.BOLD}아이템: ${ChatColor.GOLD.bold()}${it.item.itemMeta?.displayName}\n\n"))
            .append(Component.text("${ChatColor.BOLD}구매: ${ChatColor.GOLD.bold()}${it.price}${ChatColor.WHITE.bold()}원"))
            .append(Component.text("${ChatColor.BOLD}  판매: ${ChatColor.GOLD.bold()}${it.price*80/100}${ChatColor.WHITE.bold()}원\n\n"))
            .append(
                Component.text("${ChatColor.GREEN.bold()}[ 구매 ]")
                    .hoverEvent(Component.text("${amount}개 구매"))
                    .clickEvent(ClickEvent.runCommand("/shop buy $amount ${it.dropItem?.uniqueId}"))
            )
            .append(
                Component.text("   ${ChatColor.RED.bold()}[ 판매 ]\n\n")
                    .hoverEvent(Component.text("${amount}개 판매"))
                    .clickEvent(ClickEvent.runCommand("/shop sell $amount ${it.dropItem?.uniqueId}"))
            )
            .append(Component.text("${ChatColor.DARK_GRAY.bold()}=".repeat(30)))
    }

    fun shopNotBuyComponent(it: ItemCase, amount: Int): Component {
        return Component.text("${ChatColor.DARK_GRAY.bold()}=".repeat(30) + "\n\n")
            .append(Component.text("${ChatColor.BOLD}아이템: ${ChatColor.GOLD.bold()}${it.item.itemMeta?.displayName}\n\n"))
            .append(Component.text("${ChatColor.RED.bold()}구매 불가"))
            .append(Component.text("  ${ChatColor.BOLD}판매: ${ChatColor.GOLD.bold()}${it.price*80/100}${ChatColor.WHITE.bold()}원\n\n"))
            .append(
                Component.text("${ChatColor.RED.bold()}[ 판매 ]\n")
                    .hoverEvent(Component.text("${amount}개 판매"))
                    .clickEvent(ClickEvent.runCommand("/shop sell $amount ${it.dropItem?.uniqueId}"))
            )
            .append(Component.text("${ChatColor.DARK_GRAY.bold()}=".repeat(30)))
    }
}