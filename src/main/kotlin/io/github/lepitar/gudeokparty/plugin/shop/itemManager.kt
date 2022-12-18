package io.github.lepitar.gudeokparty.plugin.shop

import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin
import io.github.lepitar.gudeokparty.plugin.util.dataConfig
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object itemManager {

    val itemList = ArrayList<ItemCase>()
    val registerPlayer = HashMap<UUID, ItemCase>()
    var scheduler: BukkitTask? = null
    private val config = dataConfig.customConfig!!
    private val intstance = GudeokpartyPlugin.instance

    fun addItem(item: ItemCase, player: Player): Boolean {
        for (itemCase in itemList) {
            if (itemCase.loc == item.loc) {
                return false
            }
        }
        itemList.add(item)
        registerPlayer[player.uniqueId] = item

        return true
    }

    fun detachPlayer(player: Player) {
        registerPlayer.remove(player.uniqueId)
    }

    fun detachItem(item: ItemCase) {
        itemList.remove(item)
    }

    fun isItem(uuid: UUID): Boolean {
        itemList.forEach {
            if (it.dropItem?.uniqueId == uuid)
                return true
        }
        return false
    }

    fun loadItem() {
        val itemCaseList = config.getConfigurationSection("item")?.getKeys(false)
        if (itemCaseList != null) {
            for (itemCase in itemCaseList) {
                println(itemCase)
                val encoded_item = config.getString("item.$itemCase.item")
                val price = config.getInt("item.$itemCase.price")
                val world = Bukkit.getWorld(config.getString("item.$itemCase.world")!!)
                val x = config.getDouble("item.$itemCase.x")
                val y = config.getDouble("item.$itemCase.y")
                val z = config.getDouble("item.$itemCase.z")
                val loc = Location(world, x, y, z, 0.0F, 0.0F)
                val signWorld = Bukkit.getWorld(config.getString("item.$itemCase.signWorld")!!)
                val signX = config.getDouble("item.$itemCase.signX")
                val signY = config.getDouble("item.$itemCase.signY")
                val signZ = config.getDouble("item.$itemCase.signZ")
                val signLoc = Location(signWorld, signX, signY, signZ, 0.0F, 0.0F)
                val buy = config.getBoolean("item.$itemCase.buy")
                val serializedObject = Base64.getDecoder().decode(encoded_item)
                val io = ByteArrayInputStream(serializedObject)
                val os = BukkitObjectInputStream(io)
                val item = os.readObject() as ItemStack
                val loadedItem = ItemCase(item, loc, signLoc, price, buy)
                itemList.add(loadedItem)
            }
        }
    }

    fun saveItem(itemCase: ItemCase) {
        val item = config.createSection("item.${itemCase.dropItem?.uniqueId}")
        //itemstack encoding base64
        val io = ByteArrayOutputStream();
        val os = BukkitObjectOutputStream(io);
        os.writeObject(itemCase.item);
        os.flush();
        val data = io.toByteArray();
        val base64 = Base64.getEncoder().encodeToString(data);
        item.set("item", base64)
        item.set("world", itemCase.loc.world!!.name)
        item.set("x", itemCase.loc.x)
        item.set("y", itemCase.loc.y)
        item.set("z", itemCase.loc.z)
        item.set("signWorld", itemCase.sign!!.world!!.name)
        item.set("signX", itemCase.sign!!.x)
        item.set("signY", itemCase.sign!!.y)
        item.set("signZ", itemCase.sign!!.z)
        item.set("price", itemCase.price)
        item.set("buy", itemCase.buy)
        dataConfig.save()
    }

    fun init(plugin: Plugin) {

        //sign reload
        itemList.forEach { itemCase ->
            val loc = itemCase.sign!!
            val block = Bukkit.getWorld(loc.world.name)!!.getBlockAt(loc).state as Sign
            block.setLine(0, "${ChatColor.BOLD}[ ${itemCase.item.itemMeta.displayName} ]")
            if (itemCase.buy) {
                block.setLine(2, "${ChatColor.BOLD}구매: ${itemCase.price}원")
            } else {
                block.setLine(2, "${ChatColor.BOLD}구매: 불가")
            }
            block.setLine(3, "${ChatColor.BOLD}판매: ${itemCase.price*80/100}원")
            block.isGlowingText = true
            block.update()
        }

        //죽었으면 다시 스폰
        scheduler = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            itemList.forEach {
                if (it.dropItem == null || it.dropItem!!.isDead || !it.dropItem!!.isValid) {
                    it.spawnItem()
                }
            }
        }, 0, 20)
    }

}