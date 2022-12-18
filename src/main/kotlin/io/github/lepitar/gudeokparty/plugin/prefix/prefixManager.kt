package io.github.lepitar.gudeokparty.plugin.prefix

import com.google.gson.JsonParser
import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object prefixManager {

    val prefixList = ArrayList<Nametag>()

    fun parsePrefix(player: Player): String {
        val failPrefix = "${ChatColor.GRAY.bold()}[미인증] ${ChatColor.WHITE}"
        try {
            val website = URL("https://gdmine.kro.kr:1211/userPrefix?username=${player.name}")
            val connection = website.openConnection() as HttpURLConnection
            //send connection
            connection.connect()
            //get input stream
            val inputStream = connection.inputStream
            //read input stream
            val result = inputStream.bufferedReader().use { it.readText() }
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                //parse json
                val json = JsonParser.parseString(result).asJsonObject
                //get prefix
                val prefix = json.get("msg").asString
                return "$prefix "
            } else {
                return failPrefix
            }

        } catch (err: IOException) {
            return failPrefix
        }
    }

    fun getPrefix(player: Player): String {
    return prefixList.find { it.player.uniqueId == player.uniqueId }!!.prefix
    }
}