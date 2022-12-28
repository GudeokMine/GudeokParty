package io.github.lepitar.gudeokparty.plugin.prefix.chingho

import com.artemis.the.gr8.playerstats.api.PlayerStats
import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.econ
import io.github.lepitar.gudeokparty.plugin.prefix.prefixManager
import io.github.lepitar.gudeokparty.plugin.util.dataConfig
import io.github.lepitar.gudeokparty.plugin.util.dataConfig.customConfig
import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object chinhoInventory {

    val invList = HashMap<Player, Inventory>()
    val chinhoList = mutableListOf(
        ChinHo("${ChatColor.DARK_RED}☠ ${ChatColor.BOLD}학살자 ${ChatColor.DARK_RED}☠", "서버내 플레이어 킬수 1위에게 주어지는 칭호",ItemStack(Material.DIAMOND_SWORD), "chingho.most_kill"),
        ChinHo("${ChatColor.AQUA}❃ ${ChatColor.BOLD}모험가 ${ChatColor.AQUA}❃", "서버내 걸음 수 1위에게 주어지는 칭호", ItemStack(Material.GRASS_BLOCK), "chingho.walk"),
        ChinHo("${ChatColor.GOLD}✮ ${ChatColor.BOLD}이재용 ${ChatColor.GOLD}✮", "서버 1위 부자, 기부좀 하세요", ItemStack(Material.GOLD_INGOT), "chingho.rich"),
        ChinHo("${ChatColor.GREEN}✿ ${ChatColor.BOLD}쑥쑥새싹 ${ChatColor.GREEN}✿", "귀여운 신참이다", ItemStack(Material.PLAYER_HEAD), "chingho.newbie"),
        ChinHo("${ChatColor.GOLD}${ChatColor.MAGIC}d ${ChatColor.RESET}${ChatColor.GOLD.bold()}도박왕 ${ChatColor.GOLD}${ChatColor.MAGIC}d", "${ChatColor.RESET}${ChatColor.BOLD}도박중독은 1336", ItemStack(Material.CREEPER_HEAD), "chingho.gamble"),
        ChinHo("${ChatColor.DARK_PURPLE}☯ ${ChatColor.BOLD}개천에서 용났다 ${ChatColor.DARK_PURPLE}☯", "최초 드래곤을 죽이면 얻을 수 있다", ItemStack(Material.DRAGON_HEAD), "chingho.dragon"),
        ChinHo("${ChatColor.BLACK.bold()}삼삼드래 킬러", "머리가 세개?", ItemStack(Material.WITHER_SKELETON_SKULL), "chingho.wither"),
        ChinHo("${ChatColor.AQUA}✣ ${ChatColor.BOLD}프로미스나인 ${ChatColor.AQUA}✣", "프로미스나인이 뭔데 씹덕아", ItemStack(Material.SUNFLOWER), "chingho.specialthanksMGURI"),
        ChinHo("${ChatColor.RED}☢ ${ChatColor.BOLD}MVP 레드 ${ChatColor.RED}☢", "적우님 안녕하세요", ItemStack(Material.REDSTONE), "chingho.specialthanksMG"),
        ChinHo("${ChatColor.GREEN}↑ ${ChatColor.BOLD}LEVEL UP ${ChatColor.GREEN}↑", "나혼자만 레벨업", ItemStack(Material.EXPERIENCE_BOTTLE), "chingho.exp"),
        ChinHo("${ChatColor.WHITE.bold()}토끼", "연골 닳는다", ItemStack(Material.RABBIT_FOOT), "chingho.rabbit"),
        ChinHo("${ChatColor.BLUE.bold()}아무것도 안했는데", "이만큼이나 ??", ItemStack(Material.CLOCK), "chingho.playtime"),
        ChinHo("${ChatColor.BLUE.bold()}날아라 슈퍼보드", "서버내 체공시간 1위에게 주어지는 칭호", ItemStack(Material.ELYTRA), "chingho.elytra"),
        ChinHo("${ChatColor.GOLD.bold()}사막의 지배자", "콜로세움에서 1위에게 주어지는 칭호", ItemStack(Material.DIAMOND_CHESTPLATE), "chingho.coloseum"),
        ChinHo("${ChatColor.AQUA.bold()}베타 테스터", "12월 28일 베타테스트에 참여하였다", ItemStack(Material.COMMAND_BLOCK), "chingho.tester"),
    )

    fun openInventory(player: Player) {
        invList[player] = Bukkit.createInventory(null, InventoryType.PLAYER, "칭호")
        chinhoList.forEachIndexed { _, chinHo ->
            val path = "permission.${chinHo.permission}" //permision.chingho.most_kill
            if (path == "permission.chingho.tester") {
                customConfig!!.getStringList(path).forEach {
                    if (it == player.name || player.isOp) {
                        invList[player]!!.addItem(chinHo.item.apply {
                            itemMeta = itemMeta.clone().apply {
                                displayName(Component.text(chinHo.name))
                                lore(listOf(Component.text("${ChatColor.RESET}${chinHo.description}").color(TextColor.color(0xFFFFFF))))
                            }
                        })
                    }
                }
            } else {
                val name = customConfig!!.getString(path)
                if (name == null) {
                    invList[player]!!.addItem(chinHo.item.apply {
                        itemMeta = itemMeta.clone().apply {
                            displayName(Component.text(chinHo.name))
                            lore(listOf(Component.text("${ChatColor.RESET}${chinHo.description}").color(TextColor.color(0xFFFFFF))))
                        }
                    })
                }
                if (name == player.name || player.isOp) {
                    invList[player]!!.addItem(chinHo.item.apply {
                        itemMeta = itemMeta.clone().apply {
                            displayName(Component.text(chinHo.name))
                            lore(listOf(Component.text("${ChatColor.RESET}${chinHo.description}").color(TextColor.color(0xFFFFFF))))
                        }
                    })
                }
            }
        }
        player.openInventory(invList[player]!!)
    }

    fun equipChingHo(player: Player, itemSlot: ItemStack) {
        val chingHo = chinhoList.find { it.item == itemSlot }!!
        player.sendMessage("${ChatColor.GREEN.bold()}칭호가 ${chingHo.name}${ChatColor.GREEN.bold()}으로 변경되었습니다.")
        val prefix = prefixManager.prefixList.find { it.player.uniqueId == player.uniqueId }
        prefix?.armorStand?.updateMetadata {
            customName(Component.text(chingHo.name))
            isCustomNameVisible = true
        }
        if (chingHo.name == "${ChatColor.BLUE.bold()}아무것도 안했는데") {
            val stats = PlayerStats.getAPI().statManager
            val time = stats.playerStatRequest(player.name).untyped(Statistic.PLAY_ONE_MINUTE).execute().numericalValue
            prefix?.armorStand?.updateMetadata {
                customName(Component.text("${ChatColor.BLUE.bold()}아무것도 안했는데 ${ChatColor.WHITE.bold()}${(time/20)/3600}${ChatColor.BLUE.bold()}시간"))
                isCustomNameVisible = true
            }
        }
        customConfig!!.set("player.${player.uniqueId}.chingho", chingHo.name)
        dataConfig.save()
    }

    fun loadChingHo(player: Player) {
        val chinghoName = customConfig!!.getString("player.${player.uniqueId}.chingho")
        val chingHo = chinhoList.find { it.name == chinghoName } ?: return
        val prefix = prefixManager.prefixList.find { it.player.uniqueId == player.uniqueId }
        prefix?.armorStand?.updateMetadata {
            customName(Component.text(chingHo.name))
            isCustomNameVisible = true
        }
        if (chingHo.name == "${ChatColor.BLUE.bold()}아무것도 안했는데") {
            val stats = PlayerStats.getAPI().statManager
            val time = stats.playerStatRequest(player.name).untyped(Statistic.PLAY_ONE_MINUTE).execute().numericalValue
            prefix?.armorStand?.updateMetadata {
                customName(Component.text("${ChatColor.BLUE.bold()}아무것도 안했는데 ${ChatColor.WHITE.bold()}${(time/20)/3600}${ChatColor.BLUE.bold()}시간"))
                isCustomNameVisible = true
            }
        }
    }

    fun removeChingHo(player: Player) {
        val prefix = prefixManager.prefixList.find { it.player.uniqueId == player.uniqueId }
        prefix?.armorStand?.updateMetadata {
            customName(Component.text(""))
            isCustomNameVisible = false
        }
    }

    fun updateChingHoPermission() {
        //get prev Player
//        val prev_kill_player = customConfig!!.getString("permission.chingho.most_kill", "")!!
//        val prev_walked_player = customConfig!!.getString("permission.chingho.walk", "")!!
//        val prev_flyed_player = customConfig!!.getString("permission.chingho.elytra", "")!!
//        val prev_jumped_player = customConfig!!.getString("permission.chingho.jump", "")!!
//        val prev_richest_player = customConfig!!.getString("permission.chingho.rich", "")!!
//        val prev_exp_player = customConfig!!.getString("permission.chingho.exp", "")!!
//        val prev_gamble_plyer = customConfig!!.getString("permission.chingho.gamble", "")!!

//        LuckPermsProvider.get().userManager.getUser(prev_kill_player)?.data()?.remove(Node.builder("chingho.most_kill").build())
//        LuckPermsProvider.get().userManager.getUser(prev_walked_player)?.data()?.remove(Node.builder("chingho.walk").build())
//        LuckPermsProvider.get().userManager.getUser(prev_flyed_player)?.data()?.remove(Node.builder("chingho.elytra").build())
//        LuckPermsProvider.get().userManager.getUser(prev_jumped_player)?.data()?.remove(Node.builder("chingho.jump").build())
//        LuckPermsProvider.get().userManager.getUser(prev_richest_player)?.data()?.remove(Node.builder("chingho.rich").build())
//        LuckPermsProvider.get().userManager.getUser(prev_exp_player)?.data()?.remove(Node.builder("chingho.highest_exp_player").build())
//        LuckPermsProvider.get().userManager.getUser(prev_gamble_plyer)?.data()?.remove(Node.builder("chingho.gamble").build())

        val stats = PlayerStats.getAPI().statManager
        val most_killed_player = stats.totalTopStatRequest().untyped(Statistic.PLAYER_KILLS).execute().numericalValue.toList()[0].first
        val most_walked_player = stats.totalTopStatRequest().untyped(Statistic.WALK_ONE_CM).execute().numericalValue.toList()[0].first
        val most_flyed_player = stats.totalTopStatRequest().untyped(Statistic.FLY_ONE_CM).execute().numericalValue.toList()[0].first
        val most_jumped_player = stats.totalTopStatRequest().untyped(Statistic.JUMP).execute().numericalValue.toList()[0].first
        var most_player = ""
        var money = 0.0
        for (player in Bukkit.getOfflinePlayers()) {
            val amount = econ.getBalance(player)
            if (amount > money) {
                money = amount
                most_player = player.name!!
            }
        }
        val highest_exp_player =Bukkit.getOfflinePlayers().sortedByDescending { it.player?.exp }[0].player
        var gamble_player = ""
        var count = 0
        customConfig!!.getConfigurationSection("player")?.getKeys(false)?.forEach {
            val gambleCount = customConfig!!.getInt("player.$it.gamble")
            if (gambleCount > count) {
                count = gambleCount
                gamble_player = customConfig!!.getString("player.$it.name")!!
            }
        }

        customConfig!!.set("permission.chingho.most_kill", most_killed_player)
        customConfig!!.set("permission.chingho.walk", most_walked_player)
        customConfig!!.set("permission.chingho.elytra", most_flyed_player)
        customConfig!!.set("permission.chingho.rabbit", most_jumped_player)
        customConfig!!.set("permission.chingho.rich", most_player)
        customConfig!!.set("permission.chingho.exp", highest_exp_player)
        customConfig!!.set("permission.chingho.gamble", gamble_player)
        dataConfig.save()
   }

}