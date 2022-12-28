package io.github.lepitar.gudeokparty.plugin.Coloseum

import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.coloseum
import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.econ
import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.instance
import io.github.lepitar.gudeokparty.plugin.TaxListener.MoneyHistory
import io.github.lepitar.gudeokparty.plugin.util.dataConfig
import io.github.lepitar.gudeokparty.plugin.util.dataConfig.customConfig
import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.node.Node
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.scheduler.BukkitTask
import java.lang.Math.ceil
import java.lang.Math.round

class Coloseum {
    private var bettingSchedule: BukkitTask? = null
    private var p1Inv = Bukkit.createInventory(null, InventoryType.PLAYER)
    private var p2Inv = Bukkit.createInventory(null, InventoryType.PLAYER)
    private val hat = ItemStack(Material.IRON_HELMET)
    private val armor = ItemStack(Material.IRON_CHESTPLATE)
    private val leg = ItemStack(Material.IRON_LEGGINGS)
    private val shoe = ItemStack(Material.IRON_BOOTS)
    private val sword = ItemStack(Material.IRON_SWORD)
    private val bow = ItemStack(Material.BOW)
    private val arrow = ItemStack(Material.ARROW, 64)
    private val food = ItemStack(Material.GOLDEN_APPLE, 3)
    private val water = ItemStack(Material.WATER_BUCKET)
    private val lava = ItemStack(Material.LAVA_BUCKET)
    private val blocks = ItemStack(Material.OAK_PLANKS, 64)
    private val axe = ItemStack(Material.IRON_AXE)
    private var shield = ItemStack(Material.SHIELD).apply {
        itemMeta = itemMeta.clone().apply {
            (this as Damageable).damage = 320
        }
    }

    private var playerList = ArrayList<ColoseumPlayer>()
    private var winPlayer = ArrayList<ColoseumPlayer>()
    var joinable = false
    var bettable = false
    var currPlayers = ArrayList<ColoseumPlayer>()
    var bettingPlayer = ArrayList<Player>()
    var start = false


    private fun betStart() {
        bettable = true
        for (player in Bukkit.getOnlinePlayers())
            player.sendMessage("${ChatColor.GREEN.bold()}30초간 베팅을 할 수 있습니다. /bet [플레이어] [금액] 으로 베팅해주세요.")
        bettingSchedule = instance.server.scheduler.runTaskTimer(instance, Runnable {
            for (player in Bukkit.getOnlinePlayers()) {
                val p1 = currPlayers[0]
                val p2 = currPlayers[1]
                val totalBet = p1.bet + p2.bet + 1
                try {
                    player.sendActionBar("${ChatColor.GREEN.bold()}1 : ${totalBet / p1.bet} / 1 : ${totalBet / p2.bet}")
                    player.sendTitle(
                        "${ChatColor.GOLD.bold()}${round((p1.bet * 100 / totalBet).toDouble()).toInt()} : ${round((p2.bet * 100 / totalBet).toDouble()).toInt()}",
                        "${p1.player.name} : ${p2.player.name}", 0, 20, 0
                    )
                } catch (e: Exception) {
                    player.sendActionBar("${ChatColor.GREEN.bold()}1 : 0 / 1 : 0")
                    player.sendTitle(
                        "${ChatColor.GOLD.bold()}0 : 0",
                        "${p1.player.name} : ${p2.player.name}", 0, 20, 0
                    )
                }
            }
        }, 0, 1L)
        instance.server.scheduler.runTaskLater(instance, Runnable {
            bettingSchedule!!.cancel()
            bettable = false
        },30 * 20L)
    }

    fun start() {
        start = true
        playerList.forEach {
            if (!it.player.isOnline)
                playerList.remove(it)
        }
        //깍두기
        if (playerList.size == 1) {
            playerList.addAll(winPlayer)
            winPlayer.clear()
        }
        if (playerList.isEmpty()) {
            if (winPlayer.size == 1) {
                try {
                    for (player in Bukkit.getOnlinePlayers()) {
                        player.sendMessage("${ChatColor.GREEN.bold()}콜로세움이 종료되었습니다.")
                        player.sendTitle("${ChatColor.GOLD.bold()}승자는 ${winPlayer[0].player.name} 입니다.", "", 20, 60, 20)
                    }
                    val winner = winPlayer[0].player
                    winner.teleport(winner.world.spawnLocation)
                    econ.depositPlayer(winner, winPlayer[0].totalBet.toDouble() + 70000.0)
                    winner.sendMessage("${ChatColor.GREEN.bold()}콜로세움에서 승리하여 ${winPlayer[0].totalBet.toDouble() + 70000.0}원을 획득하였습니다.")
                    val target = winner.uniqueId
                    customConfig!!.set("permission.chingho.coloseum", winner.name)
                    instance.config.set("player.${target}.totalMoney", instance.config.getInt("player.${target}.totalMoney", 0) + winPlayer[0].totalBet.toDouble() + 70000.0)
                    dataConfig.save()
                    start = false
                } catch (e: Exception) {
                    println("NULL ERROR")
                }
                endGame()
                return
            }
            if (winPlayer.isEmpty()) {
                for (player in Bukkit.getOnlinePlayers())
                    player.sendMessage("${ChatColor.RED.bold()}콜로세움에 참가자가 없습니다.")
                endGame()
                return
            }
            playerList = winPlayer
            winPlayer.clear()
        }
        lateinit var p1: ColoseumPlayer
        lateinit var p2: ColoseumPlayer
        playerList.apply {
            shuffle()
            p1 = this.random()
            this.remove(p1)
            p2 = this.random()
            this.remove(p2)
        }
        currPlayers.add(p1)
        currPlayers.add(p2)
        setPlayer()
        betStart()
    }

    private fun endGame() {
        bettingSchedule?.cancel()
        currPlayers.clear()
        playerList.clear()
        winPlayer.clear()
        start = false
    }

    private fun setPlayer() {
        val p1 = currPlayers[0]
        val p2 = currPlayers[1]
        p1.player.health = 20.0
        p2.player.health = 20.0
        p1Inv.contents = p1.player.inventory.contents
        p2Inv.contents = p2.player.inventory.contents

        p1.player.inventory.apply {
            clear()
            helmet = hat
            chestplate = armor
            leggings = leg
            boots = shoe
            setItemInOffHand(shield)
            setItem(0, sword)
            setItem(1, axe)
            setItem(2, bow)
            setItem(9, arrow)
            setItem(3, food)
            setItem(4, water)
            setItem(5, lava)
            setItem(6, blocks)
        }
        p2.player.inventory.apply {
            clear()
            helmet = hat
            chestplate = armor
            leggings = leg
            boots = shoe
            setItemInOffHand(shield)
            setItem(0, sword)
            setItem(1, axe)
            setItem(2, bow)
            setItem(9, arrow)
            setItem(3, food)
            setItem(4, water)
            setItem(5, lava)
            setItem(6, blocks)
        }
        val p1LocSec = customConfig!!.getConfigurationSection("coloseum.one")
        val p2ocSec = customConfig!!.getConfigurationSection("coloseum.two")
        val p1loc = Location(Bukkit.getWorld(p1LocSec!!.getString("world")!!),
            p1LocSec.getDouble("x"),
            p1LocSec.getDouble("y"),
            p1LocSec.getDouble("z"),
            p1LocSec.getDouble("yaw").toFloat(),
            p1LocSec.getDouble("pitch").toFloat()
        )
        val p2loc = Location(Bukkit.getWorld(p2ocSec!!.getString("world")!!),
            p2ocSec.getDouble("x"),
            p2ocSec.getDouble("y"),
            p2ocSec.getDouble("z"),
            p2ocSec.getDouble("yaw").toFloat(),
            p2ocSec.getDouble("pitch").toFloat()
        )
        p1.player.teleport(p1loc)
        p2.player.teleport(p2loc)
    }

    fun isInColoseum(entity: Player): Boolean {
        var contains = false
        for (player in currPlayers) {
            if (player.player.uniqueId == entity.uniqueId) {
                contains = true
            }
        }
        return contains
    }

    private fun getInColoseum(entity: Player): ColoseumPlayer? {
        return currPlayers.find { it.player.uniqueId == entity.uniqueId }
    }

    fun loose(player: Player) {
        val p = getInColoseum(player)
        var total = 0
        Location(Bukkit.getWorld("world"), -296.0, 24.0, 313.0).apply {
            world.setBlockData(this, Bukkit.createBlockData(Material.REDSTONE_BLOCK))
        }
        Bukkit.broadcastMessage(("${ChatColor.RED.bold()}${player.name}님이 패배하셨습니다."))
        currPlayers[0].apply {
            total += bet
            this.player.inventory.clear()
            this.player.teleport(Bukkit.getWorld("world")!!.spawnLocation)
            this.player.inventory.contents = p1Inv.contents
        }
        currPlayers[1].apply {
            total += bet
            this.player.inventory.clear()
            this.player.teleport(Bukkit.getWorld("world")!!.spawnLocation)
            this.player.inventory.contents = p2Inv.contents
        }
        currPlayers.apply {
            remove(p)
            this[0].apply {
                winPlayer.add(this)
                calculate(total)
            }
        }
        start()
    }

    fun join(player: Player) {
        if (joinable) {
            val contains = playerList.find { it.player.uniqueId == player.uniqueId }
            if (contains != null) {
                player.sendMessage("${ChatColor.RED.bold()}이미 콜로세움에 참가중입니다.")
                return
            }
            playerList.add(ColoseumPlayer(player, 0, 0))
            player.sendMessage("${ChatColor.GREEN.bold()}참가하였습니다.")
            player.sendMessage("${ChatColor.GREEN.bold()}PVP가 허용으로 변경됩니다")
            customConfig!!.set("player.${player.uniqueId}.pvp", true)
            dataConfig.save()
            return
        }
        player.sendMessage("${ChatColor.RED.bold()}콜로세움 참가 기간이 아닙니다.")
    }

}

class ColoseumPlayer(val player: Player, var bet: Int = 1, var totalBet: Int, private var betPlayers: HashMap<Player, Int> = HashMap()) {
    fun bet(amount: Int, player: Player) {
        if (betPlayers.containsKey(player)) {
            betPlayers[player] = betPlayers[player]!! + amount
            player.sendMessage("${ChatColor.GREEN.bold()}총 ${betPlayers[player]}원 베팅 되었습니다.")
            return
        }
        if (coloseum!!.bettingPlayer.contains(player)) {
            player.sendMessage("${ChatColor.RED.bold()}이미 베팅중입니다.")
            return
        }
        if (!coloseum!!.bettable) {
            player.sendMessage("${ChatColor.RED.bold()}베팅 기간이 아닙니다.")
            return
        }
        bet += amount
        betPlayers[player] = amount
        coloseum!!.bettingPlayer.add(player)
        player.sendMessage("${ChatColor.GREEN.bold()}${amount}원 베팅이 완료되었습니다.")
    }

    fun calculate(total: Int) {
        betPlayers.forEach {
            val reward = (total / bet) * it.value
            econ.depositPlayer(it.key, reward.toDouble())
            MoneyHistory().recordMoney(player, reward)
            player.sendMessage("${ChatColor.GREEN.bold()}정산금 ${reward}원을 받았습니다.")
            val target = player.uniqueId
            instance.config.set("player.${target}.totalMoney", instance.config.getInt("player.${target}.totalMoney", 0) + reward)
            dataConfig.save()
        }
        totalBet = bet
        bet = 0
        betPlayers.clear()
    }
}