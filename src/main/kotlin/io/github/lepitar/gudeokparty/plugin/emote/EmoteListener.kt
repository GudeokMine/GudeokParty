package io.github.lepitar.gudeokparty.plugin.emote

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.TextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class EmoteListener: Listener {
    @EventHandler
    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val message = event.message.removePrefix("/")
        val emote = Emote.emoteBy(message)

        if (emote != null) {
            event.isCancelled = true
            emote.invoke(event.player.location)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onAsyncPlayerChat(event: AsyncPlayerChatEvent) {
        val message = event.message
        val emote = Emote.emoteBy(message)

        if (emote != null) {
            event.isCancelled = true
            emote.invoke(event.player.location)

            val component = Component.text("[$message]")
                .color(TextColor.color(0xFF5555))
                .clickEvent(ClickEvent.runCommand("/$message"))
            event.player.sendMessage(component)
        }
    }
}