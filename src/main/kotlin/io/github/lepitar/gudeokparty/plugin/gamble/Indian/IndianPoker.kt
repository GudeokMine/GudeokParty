package io.github.lepitar.gudeokparty.plugin.gamble.Indian

import io.github.lepitar.gudeokparty.plugin.util.dataConfig
import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class IndianPoker(val uuid: String, val loc: Location) {
    val players = HashMap<Player, Location>()
    var progress: IndianPokerScheduler? = null
    var start = false
    var box: Location? = null

    fun join(player: Player) {
        if (players.contains(player)) return
        if (players.size == 2) {
            player.sendMessage("방이 다찼습니다")
            return
        }
        players[player] = player.location
        player.teleport(box!!)
        player.sendMessage("${ChatColor.GREEN.bold()}방에 입장하였습니다 /leave 명령어로 나갈 수 있습니다")
        blockUpdate()
        if (players.size == 2) {
            start = true
            players.forEach { (t, _) ->
                dataConfig.customConfig!!.set("player.${t.uniqueId}.gamble", dataConfig.customConfig!!.getInt("player.${t.uniqueId}.gamble", 0) + 1)
            }
            dataConfig.save()
            progress = IndianPokerScheduler(this, players.toList()[0].first, players.toList()[1].first).apply {
                start()
            }
        }
    }

    fun blockUpdate() {
        val block = loc.block
        if (block.state is Sign) {
            val sign = block.state as Sign
            sign.setLine(0, "${ChatColor.WHITE.bold()}[ ${ChatColor.AQUA.bold()}인디언 포커 ${ChatColor.WHITE.bold()}]")
            sign.setLine(2, "${players.size}/2")
            sign.setLine(3, "클릭시입장")
            sign.update()
        }
    }
}
