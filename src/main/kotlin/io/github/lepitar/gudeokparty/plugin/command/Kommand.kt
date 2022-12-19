package io.github.lepitar.gudeokparty.plugin.command

import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin
import io.github.lepitar.gudeokparty.plugin.TaxListener.MoneyHistory
import io.github.lepitar.gudeokparty.plugin.gemble.Indian.IndianPoker
import io.github.lepitar.gudeokparty.plugin.gemble.gameManager
import io.github.lepitar.gudeokparty.plugin.shop.ItemCase
import io.github.lepitar.gudeokparty.plugin.shop.itemManager
import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import io.github.monun.kommand.PluginKommand
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

object Kommand {

    val econ = GudeokpartyPlugin.econ
    fun register(plugin: GudeokpartyPlugin, kommand: PluginKommand) {
        kommand.register("test") {
            then("player" to player()) {
                executes {
                    val player = sender as Player
                    val enemy = it.get<Player>("player")

                    gameManager.indianList.add(
                        IndianPoker(player, enemy).apply {
                            start()
                        }
                    )

                }
            }
        }
        kommand.register("shop") {
            then("buy") {
                then("amount" to int(1, 64)) {
                    then("item" to string(StringType.GREEDY_PHRASE)) {
                        executes {
                            val player = sender as Player
                            val qun = it.get<Int>("amount")
                            itemManager.itemList.forEach { item ->
                                if (item.dropItem?.uniqueId.toString() == it["item"]) {
                                    item.item.amount = qun
                                    val meta = item.item.itemMeta
                                    meta.setDisplayName(null)
                                    item.item.itemMeta = meta
                                    if (!econ.has(player, (item.price * qun).toDouble())) {
                                        player.sendActionBar("${ChatColor.RED.bold()}돈이 부족합니다")
                                        return@forEach
                                    }
                                    player.inventory.addItem(item.item)
                                    player.sendActionBar("${ChatColor.GREEN.bold()}구매 완료")
                                    econ.withdrawPlayer(player as OfflinePlayer, (item.price * qun).toDouble())
                                }
                            }
                        }
                    }
                }
            }
            then("sell") {
                then("amount" to int(1, 64)) {
                    then("item" to string(StringType.GREEDY_PHRASE)) {
                        executes {
                            val player = sender as Player
                            val amount = it.get<Int>("amount")
                            itemManager.itemList.forEach { item ->
                                if (item.dropItem?.uniqueId.toString() == it["item"]) {
                                    for (inv in player.inventory.contents) {
                                        if (inv?.type == item.item.type && inv.amount >= amount) {
                                            inv.amount -= amount
                                            econ.depositPlayer(player as OfflinePlayer, ((item.price*80/100) * amount).toDouble())
                                            player.sendActionBar("${ChatColor.GREEN.bold()}판매 완료")
                                            MoneyHistory().recordMoney(player, item.price * amount)
                                            return@forEach
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            then("register") {
                permission("shop.commands.register")
                then("price" to int()) {
                    then("buy" to bool()) {
                        then("name" to string(StringType.GREEDY_PHRASE)) {
                            executes {
                                val player = sender as Player
                                val item = player.inventory.itemInMainHand.clone()
                                val block = player.getTargetBlock(5)
                                val buy = it.get<Boolean>("buy")

                                if (item.type == Material.AIR) {
                                    player.sendMessage("등록할 아이템을 들어주세요.")
                                    return@executes
                                }
                                if (block == null || block.type == Material.AIR) {
                                    player.sendMessage("블럭을 선택해주세요.")
                                    return@executes
                                }
                                item.itemMeta = item.itemMeta.apply { setDisplayName(it["name"]) }
                                val case = ItemCase(item, block.location, null, it["price"], buy)
                                if (itemManager.addItem(case, player)) {
                                    player.sendMessage("표지판을 설치해주세요.")
                                } else {
                                    player.sendMessage("이미 등록된 상점입니다.")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}