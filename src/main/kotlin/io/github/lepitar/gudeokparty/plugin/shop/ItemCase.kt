package io.github.lepitar.gudeokparty.plugin.shop

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*

class ItemCase(val item: ItemStack, val loc: Location, var sign: Location?, val price: Int, val buy: Boolean) {

    val quntity: Int = 64
    var dropItem : Entity? = null

    fun spawnItem() {
        if (!loc.isChunkLoaded)
            return

        item.amount = 1

        dropItem = loc.world.dropItem(loc.clone().apply
        {
            x += 0.5
            y += 1.6
            z += 0.5
        }, item)
        dropItem!!.velocity = Vector(0,0,0)
        dropItem!!.isInvulnerable = true
    }
}