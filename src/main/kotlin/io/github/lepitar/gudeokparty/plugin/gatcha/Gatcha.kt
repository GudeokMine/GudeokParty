package io.github.lepitar.gudeokparty.plugin.gatcha

import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin
import io.github.lepitar.gudeokparty.plugin.gatcha.item.GachaItem
import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import net.minecraft.world.item.enchantment.Enchantments
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable


class Gatcha {
    companion object {
        var gachaList = mutableListOf<GachaClass>(
            GachaClass("${ChatColor.RESET}${ChatColor.LIGHT_PURPLE.bold()}하지마세요", "${ChatColor.RESET}${ChatColor.WHITE.bold()}좋은게 나올까요?", GudeokpartyPlugin.instance.config.getInt("gacha.1")).apply {
                setListItem(listOf(
                    GachaItem(ItemStack(Material.TOTEM_OF_UNDYING), 1.0),
                    GachaItem(ItemStack(Material.ELYTRA), 0.1),
                    GachaItem(ItemStack(Material.SADDLE), 1.0),
                    GachaItem(ItemStack(Material.NAME_TAG), 1.0),
                    GachaItem(ItemStack(Material.WITHER_SKELETON_SKULL), 1.0),
                    GachaItem(ItemStack(Material.MUSIC_DISC_11), 1.0),
                    GachaItem(ItemStack(Material.MUSIC_DISC_13), 1.0),
                    GachaItem(ItemStack(Material.MUSIC_DISC_BLOCKS), 1.0),
                    GachaItem(ItemStack(Material.MUSIC_DISC_CAT), 1.0),
                    GachaItem(ItemStack(Material.MUSIC_DISC_CHIRP), 1.0),
                    GachaItem(ItemStack(Material.MUSIC_DISC_FAR), 1.0),
                    GachaItem(ItemStack(Material.MUSIC_DISC_MALL), 1.0),
                    GachaItem(ItemStack(Material.MUSIC_DISC_MELLOHI), 1.0),
                    GachaItem(ItemStack(Material.MUSIC_DISC_STAL), 1.0),
                    GachaItem(ItemStack(Material.MUSIC_DISC_STRAD), 1.0),
                    GachaItem(ItemStack(Material.MUSIC_DISC_WAIT), 1.0),
                    GachaItem(ItemStack(Material.MUSIC_DISC_WARD), 1.0),
                    GachaItem(ItemStack(Material.MUSIC_DISC_PIGSTEP), 1.0),
                    GachaItem(ItemStack(Material.LEAD), 1.0),
                    GachaItem(ItemStack(Material.GHAST_TEAR), 1.0),
                    GachaItem(ItemStack(Material.WITHER_ROSE), 1.0),
                    //잡템
                    GachaItem(ItemStack(Material.BONE), 6.0),
                    GachaItem(ItemStack(Material.ROTTEN_FLESH), 6.0),
                    GachaItem(ItemStack(Material.SPIDER_EYE), 6.0),
                    GachaItem(ItemStack(Material.SPIDER_EYE), 6.0),
                    GachaItem(ItemStack(Material.RABBIT_HIDE), 6.0),
                    GachaItem(ItemStack(Material.FISHING_ROD), 6.0),
                    GachaItem(ItemStack(Material.DRIED_KELP), 6.0),
                    GachaItem(ItemStack(Material.KELP), 6.0),
                    GachaItem(ItemStack(Material.EGG), 6.0),
                    GachaItem(ItemStack(Material.ALLIUM), 6.0),
                    GachaItem(ItemStack(Material.FLOWER_POT), 6.0),
                    GachaItem(ItemStack(Material.POPPY), 6.0),
                    GachaItem(ItemStack(Material.DEAD_BUSH), 6.0),
                    GachaItem(ItemStack(Material.DAMAGED_ANVIL), 6.0),
                    GachaItem(ItemStack(Material.BRAIN_CORAL), 6.0),
                    GachaItem(ItemStack(Material.STRING), 6.0),
                    GachaItem(ItemStack(Material.WOODEN_AXE), 6.0),
                    GachaItem(ItemStack(Material.FEATHER), 6.0),
                    GachaItem(ItemStack(Material.SNOWBALL), 6.0),
                    GachaItem(ItemStack(Material.LADDER), 6.0),
                ))
            },
            GachaClass("${ChatColor.RESET}${ChatColor.GREEN.bold()}블럭 가챠", "${ChatColor.RESET}${ChatColor.WHITE}종종 얻기 힘든게 나올수도?",GudeokpartyPlugin.instance.config.getInt("gacha.2")).apply {
                setListItem(listOf(
                    GachaItem(ItemStack(Material.BEDROCK), 0.0001),
                    GachaItem(ItemStack(Material.NETHERITE_BLOCK), 0.01),
                    GachaItem(ItemStack(Material.DIAMOND_BLOCK), 0.05),
                    GachaItem(ItemStack(Material.REINFORCED_DEEPSLATE), 0.0001),
                    GachaItem(ItemStack(Material.LIGHT, 3), 1.0),
                    GachaItem(ItemStack(Material.OCHRE_FROGLIGHT), 1.0),
                    GachaItem(ItemStack(Material.PEARLESCENT_FROGLIGHT), 1.0),
                    GachaItem(ItemStack(Material.VERDANT_FROGLIGHT), 1.0),
                    GachaItem(ItemStack(Material.SHULKER_BOX), 0.5),
                    GachaItem(ItemStack(Material.LODESTONE), 0.5),
                    GachaItem(ItemStack(Material.RESPAWN_ANCHOR), 1.0),
                    GachaItem(ItemStack(Material.SEA_LANTERN), 1.0),
                    GachaItem(ItemStack(Material.SLIME_BLOCK), 1.0),
                    GachaItem(ItemStack(Material.NOTE_BLOCK), 1.0),
                    GachaItem(ItemStack(Material.HONEY_BLOCK), 1.0),
                    //glazed teracota
                    GachaItem(ItemStack(Material.BLACK_GLAZED_TERRACOTTA,32), 3.0),
                    GachaItem(ItemStack(Material.BLUE_GLAZED_TERRACOTTA,32), 3.0),
                    GachaItem(ItemStack(Material.BROWN_GLAZED_TERRACOTTA,32), 3.0),
                    GachaItem(ItemStack(Material.CYAN_GLAZED_TERRACOTTA,32), 3.0),
                    GachaItem(ItemStack(Material.GRAY_GLAZED_TERRACOTTA,32), 3.0),
                    GachaItem(ItemStack(Material.GREEN_GLAZED_TERRACOTTA,32), 3.0),
                    GachaItem(ItemStack(Material.LIGHT_BLUE_GLAZED_TERRACOTTA,32), 3.0),
                    GachaItem(ItemStack(Material.LIGHT_GRAY_GLAZED_TERRACOTTA,32), 3.0),
                    GachaItem(ItemStack(Material.LIME_GLAZED_TERRACOTTA,32), 3.0),
                    GachaItem(ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA,32), 3.0),
                    GachaItem(ItemStack(Material.ORANGE_GLAZED_TERRACOTTA,32), 3.0),
                    GachaItem(ItemStack(Material.PINK_GLAZED_TERRACOTTA,32), 3.0),
                    GachaItem(ItemStack(Material.PURPLE_GLAZED_TERRACOTTA,32), 3.0),
                    GachaItem(ItemStack(Material.RED_GLAZED_TERRACOTTA,32), 3.0),
                    GachaItem(ItemStack(Material.WHITE_GLAZED_TERRACOTTA,32), 3.0),
                    GachaItem(ItemStack(Material.YELLOW_GLAZED_TERRACOTTA,32), 3.0),
                    //concrete
                    GachaItem(ItemStack(Material.WHITE_CONCRETE, 64), 3.0),
                    GachaItem(ItemStack(Material.ORANGE_CONCRETE, 64), 3.0),
                    GachaItem(ItemStack(Material.MAGENTA_CONCRETE, 64), 3.0),
                    GachaItem(ItemStack(Material.LIGHT_BLUE_CONCRETE, 64), 3.0),
                    GachaItem(ItemStack(Material.YELLOW_CONCRETE, 64), 3.0),
                    GachaItem(ItemStack(Material.LIME_CONCRETE, 64), 3.0),
                    GachaItem(ItemStack(Material.PINK_CONCRETE, 64), 3.0),
                    GachaItem(ItemStack(Material.GRAY_CONCRETE, 64), 3.0),
                    GachaItem(ItemStack(Material.LIGHT_GRAY_CONCRETE, 64), 3.0),
                    GachaItem(ItemStack(Material.CYAN_CONCRETE, 64), 3.0),
                    GachaItem(ItemStack(Material.PURPLE_CONCRETE, 64), 3.0),
                    GachaItem(ItemStack(Material.BLUE_CONCRETE, 64), 3.0),
                    GachaItem(ItemStack(Material.BROWN_CONCRETE, 64), 3.0),
                    GachaItem(ItemStack(Material.GREEN_CONCRETE, 64), 3.0),
                    GachaItem(ItemStack(Material.RED_CONCRETE, 64), 3.0),
                    GachaItem(ItemStack(Material.BLACK_CONCRETE, 64), 3.0),
                    GachaItem(ItemStack(Material.TINTED_GLASS, 2), 3.0),
                    //잡템
                    GachaItem(ItemStack(Material.GRASS_BLOCK), 5.5),
                    GachaItem(ItemStack(Material.WARPED_WART_BLOCK), 5.5),
                    GachaItem(ItemStack(Material.COBBLESTONE), 5.5),
                    GachaItem(ItemStack(Material.DEEPSLATE), 5.5),
                    GachaItem(ItemStack(Material.BRICKS), 5.5),
                    GachaItem(ItemStack(Material.POLISHED_ANDESITE), 5.5),
                    GachaItem(ItemStack(Material.ANDESITE), 5.5),
                    GachaItem(ItemStack(Material.OAK_PLANKS), 5.5),
                    GachaItem(ItemStack(Material.ACACIA_LEAVES), 5.5),
                    GachaItem(ItemStack(Material.JUNGLE_LEAVES), 5.5),
                    GachaItem(ItemStack(Material.SMOOTH_BASALT), 5.5),
                    GachaItem(ItemStack(Material.BLUE_ICE), 5.5),
                    GachaItem(ItemStack(Material.FARMLAND), 5.5),
                    GachaItem(ItemStack(Material.CACTUS), 5.5),
                    GachaItem(ItemStack(Material.SNOW_BLOCK), 5.5),
                    GachaItem(ItemStack(Material.BOOKSHELF), 5.5),
                    GachaItem(ItemStack(Material.STONE), 5.5),
                    GachaItem(ItemStack(Material.GLASS), 5.5),
                    GachaItem(ItemStack(Material.MYCELIUM), 5.5),
                    GachaItem(ItemStack(Material.TUFF), 5.5),
                ))
            },
            GachaClass("${ChatColor.RESET}${ChatColor.GOLD.bold()}광물&도구 가챠", "${ChatColor.RESET}${ChatColor.WHITE}광물은 늘 좋습니다", GudeokpartyPlugin.instance.config.getInt("gacha.3")).apply {
                setListItem(listOf(
                    GachaItem(ItemStack(Material.NETHERITE_HOE), 0.2),
                    GachaItem(ItemStack(Material.GOLDEN_AXE).apply {
                       this.itemMeta = this.itemMeta.clone().apply {
                           (this as Damageable).damage = 24
                       }
                        addEnchantment(Enchantment.DIG_SPEED, 5)
                    }, 8.0),
                    GachaItem(ItemStack(Material.DIAMOND, 2), 0.8),
                    GachaItem(ItemStack(Material.IRON_INGOT, 5), 1.8),
                    GachaItem(ItemStack(Material.GOLD_INGOT, 5), 4.2),
                    GachaItem(ItemStack(Material.COPPER_INGOT, 5), 5.4),
                    GachaItem(ItemStack(Material.NETHERITE_INGOT), 0.005),
                    GachaItem(ItemStack(Material.QUARTZ, 5), 1.0),
                    GachaItem(ItemStack(Material.LAPIS_LAZULI, 16), 4.0),
                    GachaItem(ItemStack(Material.REDSTONE, 16), 4.0),
                    GachaItem(ItemStack(Material.CHAINMAIL_BOOTS), 3.0),
                    GachaItem(ItemStack(Material.CHAINMAIL_CHESTPLATE), 3.0),
                    GachaItem(ItemStack(Material.CHAINMAIL_HELMET), 3.0),
                    GachaItem(ItemStack(Material.CHAINMAIL_LEGGINGS), 3.0),
                    GachaItem(ItemStack(Material.FLINT_AND_STEEL, 1), 9.0),
                    GachaItem(ItemStack(Material.TRIDENT), 0.05),
                    GachaItem(ItemStack(Material.TURTLE_HELMET), 1.5),
                    GachaItem(ItemStack(Material.SHIELD), 1.4),
                    GachaItem(ItemStack(Material.SPYGLASS), 5.8),
                    GachaItem(ItemStack(Material.CROSSBOW), 5.4),
                    //잡템
                    GachaItem(ItemStack(Material.WOODEN_AXE), 17.3),
                    GachaItem(ItemStack(Material.WOODEN_SWORD), 17.3),
                    GachaItem(ItemStack(Material.WOODEN_HOE), 17.3),
                    GachaItem(ItemStack(Material.WOODEN_PICKAXE), 17.3),
                    GachaItem(ItemStack(Material.WOODEN_SHOVEL), 17.3),
                    GachaItem(ItemStack(Material.WOODEN_AXE), 17.3),
                ))
            },
        )
    }

    fun openInventory(player: Player) {
        val inventory: Inventory = Bukkit.createInventory(null, 27, "${ChatColor.GOLD.bold()}가챠")
        for (i in 0..26) {
            inventory.setItem(i, ItemStack(Material.RED_STAINED_GLASS_PANE))
        }
        inventory.setItem(11, gachaList[0].clickedItem)
        inventory.setItem(13, gachaList[1].clickedItem)
        inventory.setItem(15, gachaList[2].clickedItem)
        player.openInventory(inventory)
    }
}