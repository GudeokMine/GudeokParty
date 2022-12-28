package io.github.lepitar.gudeokparty.plugin

import io.github.lepitar.gudeokparty.plugin.Coloseum.Coloseum
import io.github.lepitar.gudeokparty.plugin.Coloseum.ColoseumListener
import io.github.lepitar.gudeokparty.plugin.Settings.SettingListener
import io.github.lepitar.gudeokparty.plugin.TaxListener.BrewTaxListener
import io.github.lepitar.gudeokparty.plugin.TaxListener.MoneyHistory
import io.github.lepitar.gudeokparty.plugin.TaxListener.SmithingTaxListener
import io.github.lepitar.gudeokparty.plugin.command.Kommand
import io.github.lepitar.gudeokparty.plugin.emote.EmoteListener
import io.github.lepitar.gudeokparty.plugin.gamble.Indian.IndianPoker
import io.github.lepitar.gudeokparty.plugin.gamble.Indian.IndianPokerListener
import io.github.lepitar.gudeokparty.plugin.gamble.gameManager.indianPokerGame
import io.github.lepitar.gudeokparty.plugin.gamble.holjjak.holjjak
import io.github.lepitar.gudeokparty.plugin.gamble.slotMachine.slotMachine
import io.github.lepitar.gudeokparty.plugin.gatcha.GachaListener
import io.github.lepitar.gudeokparty.plugin.gatcha.Gatcha
import io.github.lepitar.gudeokparty.plugin.prefix.Nametag
import io.github.lepitar.gudeokparty.plugin.prefix.chingho.ChinghoRunnable
import io.github.lepitar.gudeokparty.plugin.prefix.chingho.chinghoListener
import io.github.lepitar.gudeokparty.plugin.prefix.chingho.chinhoInventory.loadChingHo
import io.github.lepitar.gudeokparty.plugin.prefix.prefixManager
import io.github.lepitar.gudeokparty.plugin.realtime.RealtimeSchedule
import io.github.lepitar.gudeokparty.plugin.shop.ItemcaseListener
import io.github.lepitar.gudeokparty.plugin.shop.itemManager
import io.github.lepitar.gudeokparty.plugin.shop.itemManager.init
import io.github.lepitar.gudeokparty.plugin.util.dataConfig
import io.github.lepitar.gudeokparty.plugin.util.dataConfig.customConfig
import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.ScoreboardManager
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*


class GudeokpartyPlugin : JavaPlugin() {

    companion object {
        private var manager: ScoreboardManager? = null
        var board: Scoreboard? = null
        lateinit var econ: Economy
        lateinit var instance: GudeokpartyPlugin
        var coloseum: Coloseum? = null
        lateinit var gachaInstance: Gatcha
    }

    override fun onEnable() {
        setupEconomy()
        manager = Bukkit.getScoreboardManager()
        board = manager!!.newScoreboard
        instance = this
        coloseum = Coloseum()
        dataConfig.createCustomConfig(this)
        config.options().copyDefaults(true)
        customConfig!!.options().copyDefaults(true)
        saveResource("data.yml", false)
        saveDefaultConfig()
        loadPrefixPlayers()
        setupCommands()
        setupListener()
        init(this)
        loadMachine()
        loadHoljjak()
        loadIndianPoker()
        gachaInstance = Gatcha()
        setupScheduler()
        logger.info("구덕 대통합")
    }

    private fun loadIndianPoker() {
        val poker = customConfig!!.getConfigurationSection("IndianPoker")?.getKeys(false) ?: return
        for (item in poker) {
            customConfig!!.apply {
                val x = getDouble("IndianPoker.$item.x")
                val y = getDouble("IndianPoker.$item.y")
                val z = getDouble("IndianPoker.$item.z")
                val world = getString("IndianPoker.$item.world")
                val locx = getDouble("IndianPoker.$item.loc.x")
                val locy = getDouble("IndianPoker.$item.loc.y")
                val locz = getDouble("IndianPoker.$item.loc.z")
                val locworld = getString("IndianPoker.$item.loc.world")
                indianPokerGame.add(IndianPoker(item, Location(Bukkit.getWorld(world!!)!!, x, y, z)).apply {
                    box = Location(Bukkit.getWorld(locworld!!)!!, locx, locy, locz)
                })
            }
        }
    }

    private fun loadMachine() {
        val machine = customConfig!!.getConfigurationSection("machine.loc")?.getKeys(false) ?: return
        for (item in machine) {
            customConfig!!.apply {
                val x = getDouble("machine.loc.$item.x")
                val y = getDouble("machine.loc.$item.y")
                val z = getDouble("machine.loc.$item.z")
                val world = getString("machine.loc.$item.world") ?: "world"
                slotMachine.slotMachine.add(
                    Location(
                        Bukkit.getWorld(world),
                        x,
                        y,
                        z
                    )
                )
            }
        }
    }

    private fun loadHoljjak() {
        val machine = customConfig!!.getConfigurationSection("holjjak.loc")?.getKeys(false) ?: return
        for (item in machine) {
            customConfig!!.apply {
                val x = getDouble("holjjak.loc.$item.x")
                val y = getDouble("holjjak.loc.$item.y")
                val z = getDouble("holjjak.loc.$item.z")
                val world = getString("holjjak.loc.$item.world") ?: "world"
                holjjak.holjjakMachine.add(
                    Location(
                        Bukkit.getWorld(world),
                        x,
                        y,
                        z
                    )
                )
            }
        }
    }

    private fun setupScheduler() {
        val time = LocalDateTime.now()
        if (time.dayOfWeek.value == 3 || time.dayOfWeek.value == 4 || time.dayOfWeek.value == 6 || time.dayOfWeek.value == 7) {
            coloseum!!.joinable = true
        }
        var timecheck: BukkitTask? = null
        timecheck = server.scheduler.runTaskTimer(this, Runnable {
            val date = Date()
            val df: DateFormat = SimpleDateFormat(
                "HH:mm:ss"
            )
            val time: TimeZone = TimeZone.getTimeZone("Asia/Seoul")
            df.timeZone = time
            val startTime = df.parse("21:00:00")
            if (date.hours >= startTime.hours) {
                if (coloseum!!.joinable) {
                    Bukkit.broadcastMessage("§a§l[콜로세움] §f시작.")
                    Bukkit.broadcast(Component.text("${ChatColor.GOLD.bold()}[ 관전하러가기 ]").clickEvent(ClickEvent.clickEvent(
                        ClickEvent.Action.RUN_COMMAND, "/colo spectate"
                    )))
                    coloseum!!.start()
                    timecheck?.cancel()
                }
            }
        }, 20L, 20L)
        var newYYearCheck: BukkitTask? = null
        newYYearCheck = server.scheduler.runTaskTimer(this, object: Runnable {
            override fun run() {
                val date = LocalDateTime.now()
                if (date.monthValue == 1 && date.dayOfMonth == 1) {
                    for (p in Bukkit.getOnlinePlayers()) {
                        p.sendTitle("${ChatColor.AQUA}Happy ${ChatColor.GOLD}New Year${ChatColor.AQUA}!",
                            "§f새해 복 많이 받으세요!", 10, 70, 20)
                    }
                    Bukkit.broadcastMessage("${ChatColor.WHITE.bold()}지금부터 12시부터 9시까지 현실 시간으로 반영됩니다")
                    server.scheduler.runTaskTimer(this@GudeokpartyPlugin, RealtimeSchedule(), 0, 1)
                    newYYearCheck?.cancel()
                }
            }
        }, 20L * 20L, 20L * 20L)
        server.scheduler.runTaskTimer(this@GudeokpartyPlugin, ChinghoRunnable(), 10L, 10L)
        server.scheduler.runTaskTimer(this, Restarter(), 20L * 60L, 20L * 60L)
    }

    private fun loadPrefixPlayers() {
        for (p in server.onlinePlayers) {
            val prefix = Nametag(p).setPrefix(prefixManager.parsePrefix(p))
            prefixManager.prefixList.add(prefix)
            prefix.build()
            loadChingHo(p)
        }
    }

    override fun onDisable() {
        logger.info("굿바이")
        itemManager.scheduler?.cancel()
        itemManager.itemList.forEach {
            it.dropItem?.remove()
        }
        if (prefixManager.prefixList.isNotEmpty()) {
            prefixManager.prefixList.forEach {
                it.destroy()
            }
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
            registerEvents(chinghoListener(), this@GudeokpartyPlugin)
            registerEvents(SettingListener(), this@GudeokpartyPlugin)
            registerEvents(GachaListener(), this@GudeokpartyPlugin)
            registerEvents(slotMachine(), this@GudeokpartyPlugin)

            //register Game
            registerEvents(IndianPokerListener(), this@GudeokpartyPlugin)
            registerEvents(ColoseumListener(), this@GudeokpartyPlugin)
            registerEvents(IndianPokerListener(), this@GudeokpartyPlugin)
            registerEvents(holjjak(), this@GudeokpartyPlugin)
        }
    }

    private fun clearGame() {
//        gameManager.indianList.forEach {
//            it.end()
//        }
    }

    private fun setupCommands() {
        kommand {
            Kommand.register(this)
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

class Restarter: Runnable {
    override fun run() {
        if (LocalTime.now().hour == 4) {
            for (p in Bukkit.getOnlinePlayers()) {
                p.kickPlayer("서버가 재시작됩니다.")
            }
            Bukkit.shutdown()
        } else if(LocalTime.now().hour == 3 && LocalTime.now().minute == 59) {
            Bukkit.broadcastMessage("1분 뒤 서버가 재시작됩니다.")
        }
    }
}