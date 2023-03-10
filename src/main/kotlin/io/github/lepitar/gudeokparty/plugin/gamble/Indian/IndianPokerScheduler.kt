package io.github.lepitar.gudeokparty.plugin.gamble.Indian

import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.econ
import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.instance
import io.github.lepitar.gudeokparty.plugin.gamble.gameManager
import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import io.github.monun.tap.fake.FakeEntity
import io.github.monun.tap.fake.FakeEntityServer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import java.lang.Math.ceil

class IndianPokerScheduler(private val parent: IndianPoker, private val p1: Player, private val p2: Player) {
    private var perMoney = 0
    private val p1Inv = Bukkit.createInventory(null, InventoryType.PLAYER).apply { contents = p1.inventory.contents }
    private val p2Inv = Bukkit.createInventory(null, InventoryType.PLAYER).apply { contents = p2.inventory.contents }
    private var totalMoney = 0
    private var tick = 0
    private var dieEnd = false

    private val p1Server = FakeEntityServer.create(instance)
    private val p2Server = FakeEntityServer.create(instance)
    private var p1Card: FakeEntity<ArmorStand>? = null
    private var p2Card: FakeEntity<ArmorStand>? = null

    private val players = HashMap<Player, Boolean>().apply {
        put(p1, false)
        put(p2, false)
    }
    private var round = 1

    private val emeraldItem = ItemStack(Material.EMERALD).apply {
        itemMeta = itemMeta.clone().apply {
            setDisplayName("좌: 고 / 우: 다이")
        }
    }

    private var task: BukkitTask? = null

    private val materials = arrayOf(Material.STICK, Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD)
    private var choice = materials.clone().toMutableList().apply {
        shuffle()
    }

    fun start() {
        p1Server.addPlayer(p2)
        p2Server.addPlayer(p1)
        p1.inventory.clear()
        p2.inventory.clear()
        if (econ.getBalance(p1) < econ.getBalance(p2)) {
            perMoney = ceil(econ.getBalance(p1)/10).toInt()
        } else {
            perMoney = ceil(econ.getBalance(p2)/10).toInt()
        }
        for (p in players) {
            p.key.sendMessage("${ChatColor.GREEN.bold()}고 한번당 $perMoney 원입니다")
        }
        p1.inventory.setItem(0, emeraldItem)
        p2.inventory.setItem(0, emeraldItem)

        p1Card = p1Server.spawnEntity(p1.location, ArmorStand::class.java).apply {
            updateMetadata {
                isVisible = false
                isMarker = true
            }
            updateEquipment {
                val item = choice.random()
                choice.remove(item)
                helmet = ItemStack(item)
            }
        }

        p2Card = p2Server.spawnEntity(p2.location, ArmorStand::class.java).apply {
            updateMetadata {
                isVisible = false
                isMarker = true
            }
            updateEquipment {
                val item = choice.random()
                choice.remove(item)
                helmet = ItemStack(item)
            }
        }
        instance.server.scheduler.runTaskTimer(instance, Runnable {
            p1Server.update()
            p2Server.update()
            p1Card!!.moveTo(p1.location.add(0.0, 0.4 , 0.0))
            p2Card!!.moveTo(p2.location.add(0.0, 0.4 , 0.0))
        }, 0L, 1L)

        task = instance.server.scheduler.runTaskTimer(instance, gameSchedule, 20L, 1L)
    }

    fun die(player: Player) {
        if (tick >= 100) {
            choice = materials.clone().toMutableList().apply { shuffle() }
            p1Card!!.updateEquipment {
                val item = choice.random()
                choice.remove(item)
                helmet = ItemStack(item)
            }
            p2Card!!.updateEquipment {
                val item = choice.random()
                choice.remove(item)
                helmet = ItemStack(item)
            }

            players.forEach {
                it.key.sendMessage("${ChatColor.RED.bold()}${player.name}${ChatColor.WHITE.bold()}님이 다이하셨습니다...")
                if (it.key != player) {
                    econ.depositPlayer(it.key, totalMoney.toDouble())
                }
            }
            players.forEach {
                players[it.key] = false
            }
            totalMoney = 0
            tick = 0
            round++

            if (round > 3) {
                dieEnd = true
                end()
                return
            }
        }
    }

    private val gameSchedule = Runnable {
        var title = "${ChatColor.GOLD.bold()}$totalMoney"
        var subtitle = ""

        if (tick == 0) {
            players.forEach {
                it.key.sendTitle("ROUND $round", "", 20, 60, 20)
            }
        }

        if (tick >= 100) {
            players.forEach {
                if (it.value) {
                    subtitle += it.key.name + " "
                }
            }
            players.forEach {
                it.key.sendTitle(title, subtitle, 0, 20, 0)
                it.key.sendActionBar("현재 소지금: ${econ.getBalance(it.key).toInt()}")
            }

            // 둘다 레디인지 확인
            var ready = true
            players.forEach {
                if (!it.value) {
                    ready = false
                }
            }
            if (ready) {
                players.forEach {
                    players[it.key] = false
                    totalMoney += perMoney
                    econ.withdrawPlayer(it.key, perMoney.toDouble())
                }
            }

            if (econ.getBalance(p1) < perMoney || econ.getBalance(p2) < perMoney || round > 3) {
                // 끝
                var p1Num = 0
                var p2Num = 0
                p1Card!!.updateEquipment {
                    p1Num = materials.indexOf(helmet.type)
                }
                p2Card!!.updateEquipment {
                    p2Num = materials.indexOf(helmet.type)
                }
                if (p1Num > p2Num) {
                    players.forEach {
                        it.key.sendTitle(
                            "${ChatColor.GREEN.bold()}${p1.name} 승리",
                            "${ChatColor.GOLD.bold()}$totalMoney 원",
                            20,
                            100,
                            20
                        )
                    }
                    econ.depositPlayer(p1, totalMoney.toDouble())
                } else {
                    players.forEach {
                        it.key.sendTitle(
                            "${ChatColor.GREEN.bold()}${p2.name} 승리",
                            "${ChatColor.GOLD.bold()}$totalMoney 원",
                            20,
                            100,
                            20
                        )
                    }
                    econ.depositPlayer(p2, totalMoney.toDouble())
                }
                end()
            }
        }

        tick += 1
    }

    fun end() {
        task?.cancel()
        p1Server.clear()
        p2Server.clear()
        p1.inventory.contents = p1Inv.contents
        p2.inventory.contents = p2Inv.contents
        parent.players.forEach { (t, u) ->
            if (t.isOnline) {
                t.teleport(u)
            }
        }
        parent.players.clear()
        parent.start = false
        parent.blockUpdate()
        players.forEach {
            it.key.sendMessage("${ChatColor.GOLD.bold()}인디언 포커${ChatColor.WHITE.bold()} 게임이 종료되었습니다.")
        }
    }

    fun go(player: Player) {
        if (tick >= 100) {
            players[player] = true
        }
    }

    fun forceQuit(player: Player) {
        if (p1 == player) {
            p2.sendMessage("${ChatColor.RED.bold()}${p1.name}${ChatColor.WHITE.bold()}님이 게임을 나가셨습니다...")
            econ.withdrawPlayer(p2, totalMoney.toDouble())
            end()
        } else if (p2 == player) {
            p1.sendMessage("${ChatColor.RED.bold()}${p2.name}${ChatColor.WHITE.bold()}님이 게임을 나가셨습니다...")
            econ.withdrawPlayer(p1, totalMoney.toDouble())
            end()
        }
    }

}