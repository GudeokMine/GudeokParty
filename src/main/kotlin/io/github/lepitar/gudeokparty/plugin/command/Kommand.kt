package io.github.lepitar.gudeokparty.plugin.command

import io.github.lepitar.gudeokparty.plugin.Coloseum.ColoseumPlayer
import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin
import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.coloseum
import io.github.lepitar.gudeokparty.plugin.GudeokpartyPlugin.Companion.gachaInstance
import io.github.lepitar.gudeokparty.plugin.Settings.SettingInventory
import io.github.lepitar.gudeokparty.plugin.gamble.Indian.IndianPoker
import io.github.lepitar.gudeokparty.plugin.gamble.gameManager.indianPokerGame
import io.github.lepitar.gudeokparty.plugin.gamble.slotMachine.slotMachine
import io.github.lepitar.gudeokparty.plugin.prefix.chingho.chinhoInventory
import io.github.lepitar.gudeokparty.plugin.trashcan.trashInv
import io.github.lepitar.gudeokparty.plugin.util.dataConfig
import io.github.lepitar.gudeokparty.plugin.util.dataConfig.customConfig
import io.github.lepitar.gudeokparty.plugin.util.textColor.bold
import io.github.monun.kommand.PluginKommand
import org.bukkit.*
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import java.util.UUID

object Kommand {

    val econ = GudeokpartyPlugin.econ

    fun register(kommand: PluginKommand) {
        kommand.register("bet") {
            val argument = dynamic {_, input ->
                coloseum!!.currPlayers.find { it.player.name == input }
            }.apply {
                suggests {
                    suggest(coloseum!!.currPlayers.map { it.player.name })
                }
            }

            then("player" to argument) {
                then("amount" to int()) {
                    executes {
                        val betPlayer = it.get<ColoseumPlayer>("player")
                        val player = sender as Player

                        if (it.get<Int>("amount") <= 0) {
                            player.sendMessage("${ChatColor.RED.bold()}0보다 큰 금액을 입력해주세요.")
                            return@executes
                        }

                        if (econ.getBalance(player).toInt() < it.get<Int>("amount")) {
                            player.sendMessage("${ChatColor.RED.bold()}돈이 부족합니다.")
                            return@executes
                        }

                        betPlayer.bet(it.get<Int>("amount"), player)
                    }
                }
            }
        }
        kommand.register("pvp") {
            then("bool" to bool()) {
                executes {
                    val player = sender as Player
                    val bool = it.get<Boolean>("bool")
                    if (bool) {
                        player.sendMessage("${ChatColor.RED.bold()}PVP가 활성화 되었습니다.")
                        customConfig!!.set("player.${player.uniqueId}.pvp", true)
                        dataConfig.save()
                    } else {
                        player.sendMessage("${ChatColor.RED.bold()}PVP가 비활성화 되었습니다.")
                        customConfig!!.set("player.${player.uniqueId}.pvp", false)
                        dataConfig.save()
                    }
                }
            }
        }
        kommand.register("chingho", "칭호") {
            executes {
                val player = sender as Player
                chinhoInventory.openInventory(player)
            }
        }
        kommand.register("gacha", "가챠") {
            executes {
                val player = sender as Player
                gachaInstance.openInventory(player)
            }
        }
        kommand.register("setting", "설정") {
            executes {
                val player = sender as Player
                SettingInventory.openSetting(player)
            }
        }
        kommand.register("trashcan", "쓰레기통") {
            executes {
                val player = sender as Player
                trashInv.openInventory(player)
            }
        }
        kommand.register("spawn", "넴주") {
            executes {
                (sender as Player).teleport(Bukkit.getWorld("world")!!.spawnLocation)
            }
        }
        kommand.register("leave") {
            executes {
                val player = sender as Player
                indianPokerGame.find { it.players.contains(player) }?.let {
                    it.players.remove(player)
                    it.blockUpdate()
                    player.teleport(player.world.spawnLocation)
                    player.sendMessage("${ChatColor.RED.bold()}게임을 나갔습니다.")
                    return@executes
                }
                player.sendMessage("${ChatColor.RED.bold()}게임에 참여중이지 않습니다.")
            }
        }
        kommand.register("warp") {
            then("name" to string()) {
                executes {
                    val player = sender as Player
//                    val name = it.get<String>("name")
//                    val warp = customConfig!!.getConfigurationSection("warp")!!.getKeys(false)
//                    if (warp.contains(name)) {
//                        val location = customConfig!!.getLocation("warp.$name")
//                        player.teleport(location)
//                        player.sendMessage("${ChatColor.RED.bold()}워프되었습니다.")
//                    } else {
//                        player.sendMessage("${ChatColor.RED.bold()}워프할 수 없는 지역입니다.")
//                    }
                }
            }
        }
        kommand.register("gamble") {
            permission("gudeokparty.gamble")
            then("machine") {
                then("register") {
                    executes {
                        val machineConfig = customConfig!!.getConfigurationSection("machine") ?: customConfig!!.createSection("machine")
                        val player = sender as Player
                        val targetLoc = player.getTargetBlock(10)?.location
                        if (targetLoc == null) {
                            player.sendMessage("${ChatColor.RED.bold()}블럭을 향해주세요.")
                            return@executes
                        }
                        if (targetLoc.block.state !is Sign) {
                            player.sendMessage("${ChatColor.RED.bold()}블럭을 향해주세요.")
                            return@executes
                        }

                        val sign = targetLoc.block.state as Sign
                        sign.setLine(1, "${ChatColor.WHITE.bold()}[ ${ChatColor.AQUA.bold()}슬롯머신 ${ChatColor.WHITE.bold()}]")
                        sign.update()
                        slotMachine.slotMachine.add(targetLoc)

                        machineConfig.createSection("loc.${UUID.randomUUID()}").apply {
                            set("x", targetLoc.x)
                            set("y", targetLoc.y)
                            set("z", targetLoc.z)
                            set("world", targetLoc.world.name)
                        }
                        dataConfig.save()
                    }
                }
            }
            then("indian") {
                val argument = dynamic {_, input ->
                    indianPokerGame.find { it.uuid == input }
                }.apply {
                    suggests {
                        suggest(indianPokerGame.map { it.uuid })
                    }
                }
                then("register") {
                    executes {
                        val player = sender as Player
                        val block = player.getTargetBlock(null, 10);

                        if (block.type == Material.AIR) return@executes

                        if (block.state is Sign) {
                            val sign = block.state as Sign
                            sign.setLine(0, "${ChatColor.WHITE.bold()}[ ${ChatColor.AQUA.bold()}인디언 포커 ${ChatColor.WHITE.bold()}]")
                            sign.setLine(2, "0/2")
                            sign.setLine(3, "클릭시입장")
                            sign.update()
                        }

                        val uuid = UUID.randomUUID()
                        val loc = block.location
                        indianPokerGame.add(IndianPoker(uuid.toString(), loc))
                        val section = customConfig!!.getConfigurationSection("IndianPoker.$uuid")
                            ?: customConfig!!.createSection("IndianPoker.$uuid")

                        section.apply {
                            set("x",  loc.x)
                            set("y", loc.y)
                            set("z", loc.z)
                            set("world", loc.world.name)
                        }
                        dataConfig.save()
                    }
                }
                then("uuid" to argument) {
                    then("loc") {
                        executes {context ->
                            val player = sender as Player
                            val loc = player.location
                            context.get<IndianPoker>("uuid").box = loc
                            val uuid = context.get<IndianPoker>("uuid").uuid
                            val section = customConfig!!.getConfigurationSection("IndianPoker.$uuid.loc")
                                ?: customConfig!!.createSection("IndianPoker.$uuid.loc")

                            section.apply {
                                set("x",  loc.x)
                                set("y", loc.y)
                                set("z", loc.z)
                                set("world", loc.world.name)
                            }
                            dataConfig.save()
                        }
                    }
                }
            }
            then("holjjak") {
                then ("register") {
                    executes {
                        val machineConfig = customConfig!!.getConfigurationSection("holjjak") ?: customConfig!!.createSection("holjjak")
                        val player = sender as Player
                        val targetLoc = player.getTargetBlock(10)?.location
                        if (targetLoc == null) {
                            player.sendMessage("${ChatColor.RED.bold()}블럭을 향해주세요.")
                            return@executes
                        }
                        if (targetLoc.block.state !is Sign) {
                            player.sendMessage("${ChatColor.RED.bold()}블럭을 향해주세요.")
                            return@executes
                        }

                        val sign = targetLoc.block.state as Sign
                        sign.setLine(1, "${ChatColor.WHITE.bold()}[ ${ChatColor.AQUA.bold()}홀/짝 ${ChatColor.WHITE.bold()}]")
                        sign.update()
                        slotMachine.slotMachine.add(targetLoc)

                        machineConfig.createSection("loc.${UUID.randomUUID()}").apply {
                            set("x", targetLoc.x)
                            set("y", targetLoc.y)
                            set("z", targetLoc.z)
                            set("world", targetLoc.world.name)
                        }
                        dataConfig.save()
                    }
                }
            }
        }
        kommand.register("colo") {
            then("start") {
                permission("gudeokparty.colo.start")
                executes {
                    val player = sender as Player
                    player.sendMessage("강제로 시작하였습니다")
                    coloseum!!.start()
                }
            }
            then("join") {
                executes {
                    coloseum!!.join(sender as Player)
                }
            }
            then("spectate") {
                executes {
                    val player = sender as Player
                    if (coloseum!!.isInColoseum(player) && coloseum!!.bettable) return@executes
                    (sender as Player).teleport(Location(Bukkit.getWorld("world"), -297.0, 17.0, 260.0))
                }
            }
            then("set") {
                permission("gudeokparty.color.set")
                then("one") {
                    executes {
                        val player = sender as Player
                        val section = customConfig!!.createSection("coloseum.one")
                        // save location
                        section.set("x", player.location.x)
                        section.set("y", player.location.y)
                        section.set("z", player.location.z)
                        section.set("world", player.location.world.name)
                        section.set("pitch", player.location.pitch)
                        section.set("yaw", player.location.yaw)
                        dataConfig.save()
                    }
                }
                then("two") {
                    executes {
                        val player = sender as Player
                        val section = customConfig!!.createSection("coloseum.two")
                        // save location
                        section.set("x", player.location.x)
                        section.set("y", player.location.y)
                        section.set("z", player.location.z)
                        section.set("world", player.location.world.name)
                        section.set("pitch", player.location.pitch)
                        section.set("yaw", player.location.yaw)
                        dataConfig.save()
                    }
                }
            }
        }
//        kommand.register("shop") {
//            then("buy") {
//                then("amount" to int(1, 64)) {
//                    then("item" to string(StringType.GREEDY_PHRASE)) {
//                        executes {
//                            val player = sender as Player
//                            val qun = it.get<Int>("amount")
//                            itemManager.itemList.forEach { item ->
//                                if (item.dropItem?.uniqueId.toString() == it["item"]) {
//                                    item.item.amount = qun
//                                    val meta = item.item.itemMeta
//                                    meta.setDisplayName(null)
//                                    item.item.itemMeta = meta
//                                    if (!econ.has(player, (item.price * qun).toDouble())) {
//                                        player.sendActionBar("${ChatColor.RED.bold()}돈이 부족합니다")
//                                        return@forEach
//                                    }
//                                    player.inventory.addItem(item.item)
//                                    player.sendActionBar("${ChatColor.GREEN.bold()}구매 완료")
//                                    econ.withdrawPlayer(player as OfflinePlayer, (item.price * qun).toDouble())
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            then("sell") {
//                then("amount" to int(1, 64)) {
//                    then("item" to string(StringType.GREEDY_PHRASE)) {
//                        executes {
//                            val player = sender as Player
//                            val amount = it.get<Int>("amount")
//                            itemManager.itemList.forEach { item ->
//                                if (item.dropItem?.uniqueId.toString() == it["item"]) {
//                                    for (inv in player.inventory.contents) {
//                                        if (inv?.type == item.item.type && inv.amount >= amount) {
//                                            inv.amount -= amount
//                                            econ.depositPlayer(player as OfflinePlayer, ((item.price*80/100) * amount).toDouble())
//                                            player.sendActionBar("${ChatColor.GREEN.bold()}판매 완료")
//                                            MoneyHistory().recordMoney(player, item.price * amount)
//                                            return@forEach
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            then("register") {
//                permission("shop.commands.register")
//                then("price" to int()) {
//                    then("buy" to bool()) {
//                        then("name" to string(StringType.GREEDY_PHRASE)) {
//                            executes {
//                                val player = sender as Player
//                                val item = player.inventory.itemInMainHand.clone()
//                                val block = player.getTargetBlock(5)
//                                val buy = it.get<Boolean>("buy")
//
//                                if (item.type == Material.AIR) {
//                                    player.sendMessage("등록할 아이템을 들어주세요.")
//                                    return@executes
//                                }
//                                if (block == null || block.type == Material.AIR) {
//                                    player.sendMessage("블럭을 선택해주세요.")
//                                    return@executes
//                                }
//                                item.itemMeta = item.itemMeta.apply { setDisplayName(it["name"]) }
//                                val case = ItemCase(item, block.location, null, it["price"], buy)
//                                if (itemManager.addItem(case, player)) {
//                                    player.sendMessage("표지판을 설치해주세요.")
//                                } else {
//                                    player.sendMessage("이미 등록된 상점입니다.")
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
}