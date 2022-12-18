package io.github.lepitar.gudeokparty.plugin.util

import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

object dataConfig {

    private var customConfigFile: File? = null
    var customConfig: FileConfiguration? = null

    fun createCustomConfig(plugin: GudeokpartyPlugin) {
        customConfigFile = File(plugin.dataFolder, "data.yml")
        if (!customConfigFile!!.exists()) {
            customConfigFile!!.parentFile.mkdirs()
            plugin.saveResource("data.yml", false)
        }
        customConfig = YamlConfiguration()
        try {
            customConfig!!.load(customConfigFile!!)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
    }

    fun save() {
        customConfig!!.save(customConfigFile!!)
    }
}