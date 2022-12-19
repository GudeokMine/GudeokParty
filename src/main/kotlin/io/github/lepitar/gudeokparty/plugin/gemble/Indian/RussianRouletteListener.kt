package io.github.lepitar.gudeokparty.plugin.gemble.Indian

class RussianRouletteListener: Listener {

    @EventHandler
    fun onFire(e: EntityDamageByEntityEvent) {
        if (EntityDamageEvent.DamageCause.PROJECTILE != e.getCause())
            return
        val damager = e.getShooter();
        val entity = e.getEntity()

        gameManager.russianList.forEach {russia ->
            if (!russia.list.contains(player) || !russia.list.contains(damager)) {
                return
            }

            russia.calculate(damager, entity)
        }

    }
}