package io.github.lepitar.gudeokparty.plugin.gamble.holjjak

import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.econ
import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.instance
import io.github.lepitar.gudeokparty.plugin.TaxListener.MoneyHistory
import io.github.lepitar.gudeokparty.plugin.util.dataConfig
import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.*
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitTask
import java.lang.Math.ceil

class holjjak : Listener {
    val betPlayer = ArrayList<Player>()
    val setPlayer = HashMap<Player, Int>()
    val strings = arrayOf("홀","짝")

    companion object {
        val holjjakMachine = ArrayList<Location>()
    }

    @EventHandler
    fun onInteract(e: PlayerInteractEvent) {
        val block = e.clickedBlock
        // block check
        if (!holjjakMachine.contains(block?.location)) return

        if (betPlayer.contains(e.player)) return
        if (setPlayer.contains(e.player)) return

        e.isCancelled = true
        betPlayer.add(e.player)
        e.player.sendMessage("${ChatColor.GOLD.bold()}베팅할 금액을 입력해주세요!")

    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (holjjakMachine.contains(e.block.location)) e.isCancelled = true
    }

    @EventHandler
    fun onChat(e: AsyncChatEvent) {
        if (betPlayer.contains(e.player)) {
            e.isCancelled = true
            try {
                val amount = PlainTextComponentSerializer.plainText().serialize(e.message()).toInt()
                val balance = econ.getBalance(e.player)
                if (balance < amount) {
                    e.player.sendMessage("${ChatColor.RED.bold()}돈이 부족합니다.")
                    return
                }
                setPlayer[e.player] = amount
                e.player.sendMessage("${ChatColor.GOLD.bold()}홀/짝을 선택해주세요.")
            } catch (err: Exception) {
                e.player.sendMessage("${ChatColor.RED.bold()}숫자를 입력해주세요")
                e.player.sendMessage("${ChatColor.RED.bold()}홀/짝이 취소되었습니다")
            }
            betPlayer.remove(e.player)
        } else if (setPlayer.contains(e.player)) {
            e.isCancelled = true
            val holjjak = PlainTextComponentSerializer.plainText().serialize(e.message())
            if (!strings.contains(holjjak)) {
                e.player.sendMessage("${ChatColor.RED.bold()}홀/짝을 입력해주세요")
                return
            }
            dataConfig.customConfig!!.set("player.${e.player.uniqueId}.gamble", dataConfig.customConfig!!.getInt("player.${e.player.uniqueId}.gamble", 0) + 1)
            dataConfig.save()
            bettingSchedule(e.player, setPlayer[e.player]!!, holjjak)
        }
    }
    
    private fun bettingSchedule(player: Player, money: Int, holjjak: String) {
        var slot = "${ChatColor.WHITE}${strings[0]} ${ChatColor.BOLD}| ${ChatColor.LIGHT_PURPLE.bold()}${strings[1]}"
        var tick = 0
        var task: BukkitTask? = null
        task = instance.server.scheduler.runTaskTimer(instance, Runnable {
            player.sendTitle(slot, "", 0, 20, 0)
            if (tick >= 7) {
                slot = updateText(slot)
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
            }
            if (tick >= 50) {
                task?.cancel()
                slot = arrayOf("${ChatColor.LIGHT_PURPLE.bold()}${strings[0]} ${ChatColor.WHITE.bold()}| ${ChatColor.RESET}${ChatColor.WHITE}${strings[1]}", "${ChatColor.WHITE}${strings[0]} ${ChatColor.BOLD}| ${ChatColor.LIGHT_PURPLE.bold()}${strings[1]}").random()
                player.sendTitle(slot, "", 0, 30, 0)
                var success = false
                if (slot == "${ChatColor.WHITE}${strings[0]} ${ChatColor.BOLD}| ${ChatColor.LIGHT_PURPLE.bold()}${strings[1]}") {
                    if (holjjak == strings[1]) {
                        success = true
                    }
                } else {
                    if (holjjak == strings[0]) {
                        success = true
                    }
                }
                if (success) {
                    val winMoney = ceil(money.toDouble() * 1.3)
                    player.sendMessage("${ChatColor.GREEN.bold()}홀/짝에서 ${winMoney.toInt()}원을 획득하였습니다.")
                    MoneyHistory().recordMoney(player, winMoney.toInt())
                    econ.depositPlayer(player, money.toDouble())
                    player.playSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
                    player.world.spawn(player.location, Firework::class.java).apply {
                        fireworkMeta = fireworkMeta.clone().apply {
                            addEffect(FireworkEffect.builder().withColor(Color.YELLOW).withColor(Color.BLUE).withColor(Color.PURPLE).with(FireworkEffect.Type.BALL).build())
                        }
                    }
                } else {
                    player.sendMessage("${ChatColor.RED.bold()}홀/짝에서 ${money}원을 잃었습니다.")
                }
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1.5f)
                setPlayer.remove(player)
            }
            tick++
        }, 3, 3)
    }

    private fun updateText(slot: String): String {
        var text = ""
        text = if (slot == "${ChatColor.WHITE}${strings[0]} ${ChatColor.BOLD}| ${ChatColor.LIGHT_PURPLE.bold()}${strings[1]}") {
            "${ChatColor.LIGHT_PURPLE.bold()}${strings[0]} ${ChatColor.WHITE.bold()}| ${ChatColor.RESET}${ChatColor.WHITE}${strings[1]}"
        } else {
            "${ChatColor.WHITE}${strings[0]} ${ChatColor.BOLD}| ${ChatColor.LIGHT_PURPLE.bold()}${strings[1]}"
        }
        return text
    }

}