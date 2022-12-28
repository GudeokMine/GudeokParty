package io.github.lepitar.gudeokparty.plugin.TaxListener

import encryptsl.cekuj.net.api.enums.TransactionType
import encryptsl.cekuj.net.api.events.PlayerEconomyPayEvent
import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin
import io.github.lepitar.gudeokparty.plugin.util.dataConfig
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class MoneyHistory : Listener {

    private val economy = GudeokpartyPlugin.econ
    private val config = dataConfig.customConfig!!

    @EventHandler
    fun withdraw(e: PlayerEconomyPayEvent) {
        val sender: Player = e.sender
        val target: OfflinePlayer = e.target
        val money: Double = e.money

        if (!economy.has(sender, e.money)) {
            e.isCancelled = true
            sender.sendMessage("§c돈이 부족합니다.")
            return
        }

        if (sender.uniqueId == target.uniqueId) {
            e.isCancelled = true
            return
        }

        if (e.transactionType == TransactionType.PAY) {
            recordMoney(target as Player, money.toInt())
        }

    }

    fun recordMoney(player: Player, money: Int) {
        val target = player.uniqueId
        config.set("player.${target}.totalMoney", config.getInt("player.${target}.totalMoney", 0) + money)
        dataConfig.save()
    }
}