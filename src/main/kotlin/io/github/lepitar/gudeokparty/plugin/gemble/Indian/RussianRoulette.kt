package io.github.lepitar.gudeokparty.plugin.gemble.Indian

class RussianRoulette(val list: List<Player>) {
    
    val playerInv = ArrayList<Inventory>()

    val percent = 166 //16.6%
    var totalMoney = 0
    var minMoney = 2000
    var perMoney = 1000
    var tick = 0
    var round = 0
    var turnPlayer: Player? = null 

    fun start() {
        list.forEach { player ->
            playerInv.add(Bukkit.createInventory(null, InventoryType.PLAYER).apply { contents = player.inventory.contents }))
            player.inventory.clear()
        }
        gameManager.russianList.add(this)
    }

    fun calculate(damager: Player, entity: Player) {
        if (tick < 100) return
        val r = Random()
        if (r.nextInt(10000) <= percent) {
            failPlayer(entity)
        }
        nextTurn()
    }

    Runnable {
        list.forEach { player ->
            player.sendActionBar("현재 턴: ${turnPlayer.name}")
        }
        if (tick == 0) {
            list.forEach { player-> 
                player.sendTitle("러시안 룰렛", "", 10, 40, 10)
            }
        }

        if (tick == 100) {
            list.forEach { player ->
                player.sendTitle("배팅 시간", "돈을 레이즈 해주세요 단위: $perMoney원", 10, 40, 10)
            }
        }

        if (tick == 200) {
            list.forEach { player ->
                player.sendTitle("${ChatColor.GOLD.bold()}$totalMoney", "총 배팅금액", 10, 40, 10)
            }
        }

        if (tick >= 300) {
            
        }

        tick++
    }

    fun raiseMoney() {
        if (tick < 100) return

    }

    fun nextTurn() {
        val innerTick = tick.clone().apply { this += 100 }
        if (tick >= innerTick) {
            val index = list.indexOf(turnPlayer!!)
            if (index >= list.size()) {
                round ++
                tick = 0
            }
        }    
    }

    fun failPlayer(player: Player) {
        //탈락
        
    }

    fun end() {
        
    }

}
