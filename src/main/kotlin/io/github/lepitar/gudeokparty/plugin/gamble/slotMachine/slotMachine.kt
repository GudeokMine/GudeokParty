package io.github.lepitar.gudeokparty.plugin.gamble.slotMachine

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

class slotMachine : Listener {
    val betPlayer = ArrayList<Player>()
    val strings = arrayOf("Ⓠ","◆","✖","✔","☮","★","۞","☠","☭")

    companion object {
        val slotMachine = ArrayList<Location>()
    }

    @EventHandler
    fun onInteract(e: PlayerInteractEvent) {
        val block = e.clickedBlock
        // block check
        if (!slotMachine.contains(block?.location)) return

        e.isCancelled = true
        if (!betPlayer.contains(e.player)) {
            betPlayer.add(e.player)
            e.player.sendMessage("${ChatColor.GOLD.bold()}베팅할 금액을 입력해주세요.")
        }

    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (slotMachine.contains(e.block.location)) e.isCancelled = true
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
                dataConfig.customConfig!!.set("player.${e.player.uniqueId}.gamble", dataConfig.customConfig!!.getInt("player.${e.player.uniqueId}.gamble", 0) + 1)
                dataConfig.save()
                betPlayer.remove(e.player)
                bettingSchedule(e.player, amount)
            } catch (err: Exception) {
                betPlayer.remove(e.player)
                e.player.sendMessage("${ChatColor.RED.bold()}숫자를 입력해주세요")
                e.player.sendMessage("${ChatColor.RED.bold()}슬롯머신이 취소되었습니다")
            }
        }
    }
    
    private fun bettingSchedule(player: Player, money: Int) {
        var slot = arrayOf(
            strings.random(),
            strings.random(),
            strings.random(),
            strings.random()
        )
        var tick = 0
        var task: BukkitTask? = null
        task = instance.server.scheduler.runTaskTimer(instance, Runnable {
            player.sendTitle(slot.joinToString(" | "), "", 0, 20, 0)
            if (tick >= 7) {
                slot = arrayOf(
                    strings.random(),
                    strings.random(),
                    strings.random(),
                    strings.random()
                )
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f)
            }
            if (tick >= 50) {
                task?.cancel()
                player.sendTitle(slot.joinToString(" | "), "", 0, 30, 0)
                val result = HashMap<String, Int>()
                slot.forEach { it ->
                    if (result.containsKey(it)) {
                        result[it] = result[it]!! + 1
                    } else {
                        result[it] = 1
                    }
                }
                val count = result.toList().sortedByDescending { it.second }[0].second
//                if (count == 2) {
//                    //두개 맞출때
//                    val winMoney = ceil(money.toDouble() * 1.2)
//                    player.sendMessage("${ChatColor.GREEN.bold()}슬롯머신에서 ${winMoney.toInt()}원을 획득하였습니다.")
//                    MoneyHistory().recordMoney(player, winMoney.toInt())
//                    econ.depositPlayer(player, money.toDouble())
//                    player.playSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
//                    player.world.spawn(player.location, Firework::class.java).apply {
//                        fireworkMeta = fireworkMeta.clone().apply {
//                            addEffect(FireworkEffect.builder().withColor(Color.YELLOW).withColor(Color.BLUE).withColor(Color.PURPLE).with(FireworkEffect.Type.BALL).build())
//                        }
//                    }
//                }
                if(count == 3) {
                    //3개 맞출때
                    val winMoney = (money.toDouble() * 2.8).toInt()
                    player.sendMessage("${ChatColor.GREEN.bold()}슬롯머신에서 ${winMoney}원을 획득하였습니다.")
                    MoneyHistory().recordMoney(player, winMoney)
                    econ.depositPlayer(player, winMoney.toDouble())
                    player.playSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
                    player.world.spawn(player.location, Firework::class.java).apply {
                        fireworkMeta = fireworkMeta.clone().apply {
                            addEffect(FireworkEffect.builder().withColor(Color.RED).withColor(Color.YELLOW).withColor(Color.GREEN).withColor(Color.BLUE).withColor(Color.AQUA).with(FireworkEffect.Type.CREEPER).build())
                        }
                    }
                } else if(count == 4) {
                    val winMoney = (money.toDouble() * 7).toInt()
                    player.sendMessage("${ChatColor.GREEN.bold()}슬롯머신에서 ${winMoney}원을 획득하였습니다.")
                    MoneyHistory().recordMoney(player, winMoney)
                    econ.depositPlayer(player, winMoney.toDouble())
                    player.playSound(player.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
                    player.world.spawn(player.location, Firework::class.java).apply {
                        fireworkMeta = fireworkMeta.clone().apply {
                            addEffect(FireworkEffect.builder().withColor(Color.RED).withColor(Color.LIME).withColor(Color.YELLOW).withColor(Color.GREEN).withColor(Color.BLUE).withColor(Color.PURPLE).with(FireworkEffect.Type.STAR).build())
                        }
                    }
                } else {
                    player.sendMessage("${ChatColor.RED.bold()}슬롯머신에서 ${money}원을 잃었습니다.")
                    econ.withdrawPlayer(player, money.toDouble())
                }
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1.5f)
            }
            tick++
        }, 3, 3)
    }

}