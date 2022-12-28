package io.github.lepitar.gudeokparty.plugin.prefix.chingho

import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.time.LocalDateTime

class ChinghoRunnable: Runnable {
    private var prev_date: LocalDateTime = LocalDateTime.now()

    override fun run() {
        val date = LocalDateTime.now()
        if (date.month > prev_date.month || date.dayOfMonth > prev_date.dayOfMonth) {
            chinhoInventory.updateChingHoPermission()
            Bukkit.broadcastMessage("${ChatColor.GREEN.bold()}서버 랭킹 정산이 완료되었습니다")
            prev_date = date
        }
    }
}