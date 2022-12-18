package io.github.lepitar.gudeokparty.plugin

import com.artemis.the.gr8.playerstats.api.PlayerStats
import io.github.lepitar.gudeokparty.plugin.TaxListener.BrewTaxListener
import io.github.lepitar.gudeokparty.plugin.TaxListener.MoneyHistory
import io.github.lepitar.gudeokparty.plugin.TaxListener.SmithingTaxListener
import io.github.lepitar.gudeokparty.plugin.command.Kommand
import io.github.lepitar.gudeokparty.plugin.emote.EmoteListener
import io.github.lepitar.gudeokparty.plugin.gemble.Indian.IndianPokerListener
import io.github.lepitar.gudeokparty.plugin.gemble.gameManager
import io.github.lepitar.gudeokparty.plugin.prefix.Nametag
import io.github.lepitar.gudeokparty.plugin.prefix.prefixManager
import io.github.lepitar.gudeokparty.plugin.shop.ItemcaseListener
import io.github.lepitar.gudeokparty.plugin.shop.itemManager
import io.github.lepitar.gudeokparty.plugin.shop.itemManager.init
import io.github.lepitar.gudeokparty.plugin.util.dataConfig
import io.github.lepitar.gudeokparty.plugin.util.dataConfig.customConfig
import io.github.monun.kommand.kommand
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.ScoreboardManager


class GudeokpartyPlugin : JavaPlugin() {

    companion object {
        private var manager: ScoreboardManager? = null
        var board: Scoreboard? = null
        lateinit var econ: Economy
        lateinit var instance: GudeokpartyPlugin
    }

    override fun onEnable() {
        setupEconomy()
        manager = Bukkit.getScoreboardManager()
        board = manager!!.newScoreboard
        instance = this
        dataConfig.createCustomConfig(this)
        config.options().copyDefaults(true)
        customConfig!!.options().copyDefaults(true)
        saveResource("data.yml", false)
        saveDefaultConfig()
        setupCommands()
        setupListener()
        itemManager.loadItem()
        init(this)
        loadPrefixPlayers()
        val playerStats = PlayerStats.getAPI()
        logger.info("구덕 대통합")
    }

    private fun loadPrefixPlayers() {
        for (p in server.onlinePlayers) {
            val prefix = Nametag(p).setPrefix(prefixManager.parsePrefix(p)).apply {
                build()
            }
            prefixManager.prefixList.add(prefix)
        }
    }

    override fun onDisable() {
        logger.info("굿바이")
        itemManager.scheduler?.cancel()
        itemManager.itemList.forEach {
            it.dropItem?.remove()
        }
        prefixManager.prefixList.forEach {
            it.destroy()
        }
        clearGame()
    }

    private fun setupListener() {
        server.pluginManager.apply {
            registerEvents(GudeokListener(), this@GudeokpartyPlugin)
            registerEvents(BrewTaxListener(), this@GudeokpartyPlugin)
            registerEvents(SmithingTaxListener(), this@GudeokpartyPlugin)
            registerEvents(MoneyHistory(), this@GudeokpartyPlugin)
            registerEvents(EmoteListener(), this@GudeokpartyPlugin)
            registerEvents(ItemcaseListener(), this@GudeokpartyPlugin)

            //register Game
            registerEvents(IndianPokerListener(), this@GudeokpartyPlugin)
        }
    }

    private fun clearGame() {
        gameManager.indianList.forEach {
            it.end()
        }
    }

    private fun setupCommands() {
        kommand {
            Kommand.register(this@GudeokpartyPlugin, this)
        }
    }

    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            logger.warning("Vault를 찾을 수 없습니다")
            return false
        }
        val rsp = server.servicesManager.getRegistration(
            Economy::class.java
        ) ?: return false
        econ = rsp.provider
        return econ != null
    }

}