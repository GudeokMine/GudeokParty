package io.github.lepitar.gudeokparty.plugin.TaxListener

import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.econ
import io.github.lepitar.gudeokparty.plugin.util.dataConfig.customConfig
import org.bukkit.entity.Player

object InventorySaveTax {
    private fun calculateProgressive(player: Player): Double {
        val totalMoney = customConfig!!.getInt("player.${player.uniqueId}.totalMoney", 0)
        var progressiveMoney = 25.0
        if (totalMoney> 800000) {
            progressiveMoney += 20.0
        } else if (totalMoney > 500000) {
            progressiveMoney += 15.0
        } else if (totalMoney > 200000) {
            progressiveMoney += 10.0
        } else if (totalMoney > 100000) {
            progressiveMoney += 5.0
        }

        if (totalMoney >= 150_000 && econ.getBalance(player) < 30000 + (totalMoney * 0.08)) {
            //cheater
            progressiveMoney = -1.0
        }

        return progressiveMoney
    }

    fun totalTax(player: Player): Int {
        val progressiveMoney = calculateProgressive(player)
        val totalMoney = customConfig!!.getInt("player.${player.uniqueId}.totalMoney", 0)
        if (progressiveMoney == -1.0) {
            return 30000 + (totalMoney * 0.08).toInt()
        }
        val balance = econ.getBalance(player)
        return (balance * progressiveMoney / 100).toInt()
    }

}