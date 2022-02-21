package tictim.hearthstones.hearthstone;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.tavern.Tavern;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static tictim.hearthstones.hearthstone.HearthUtils.warp;

public class CompanionHearthstone extends SelectionHearthstone{
	public CompanionHearthstone(){
		super(ModCfg.companionHearthstone());
	}

	@Override protected WarpSetup createWarpSetup(WarpContext context, Tavern selectedTavern, BlockPos warpPos){
		return () -> {
			for(Entity e : getWarpTargets(context.getPlayer())) warp(e, selectedTavern.pos().dim(), warpPos, false);
			warp(context.getPlayer(), selectedTavern.pos().dim(), warpPos, true);
			context.hurtItem(1);
			context.getMemory().addOrUpdate(selectedTavern);
			context.getMemory().setCooldown(config.cooldown());
		};
	}

	public Set<Entity> getWarpTargets(Entity warpingEntity){
		Set<Entity> set = new HashSet<>();
		addRidingEntities(warpingEntity, set);
		for(LivingEntity e : warpingEntity.level.getEntitiesOfClass(LivingEntity.class,
				new AABB(warpingEntity.getX(), warpingEntity.getY(), warpingEntity.getZ(), warpingEntity.getX(), warpingEntity.getY(), warpingEntity.getZ()).inflate(8)))
			if(isConsideredCompanion(warpingEntity, e))
				addRidingEntities(e, set);
		set.remove(warpingEntity);
		return set;
	}

	protected boolean isConsideredCompanion(Entity warpingEntity, LivingEntity entity){
		return entity.isAlive()&&(entity instanceof Mob m&&m.getLeashHolder()==warpingEntity||
				entity instanceof OwnableEntity ownable&&ownable.getOwner()==warpingEntity||
				entity instanceof AbstractHorse ah&&ah.isTamed()&&warpingEntity.getUUID().equals(ah.getOwnerUUID())||
				!warpingEntity.isShiftKeyDown()&&entity instanceof Player&&entity!=warpingEntity&&!entity.isSleeping()&&isHoldingCompanionStone(entity));
	}

	protected static boolean isHoldingCompanionStone(LivingEntity entity){
		return entity.getMainHandItem().is(ModItems.COMPANION_STONE.get())||
				entity.getOffhandItem().is(ModItems.COMPANION_STONE.get());
	}

	private static void addRidingEntities(Entity e, Set<Entity> set){
		addPassengers(e.getRootVehicle(), set);
	}
	private static void addPassengers(Entity root, Set<Entity> set){
		if(set.add(root)){
			List<Entity> passengers = root.getPassengers();
			if(!passengers.isEmpty()) for(Entity e : passengers) addPassengers(e, set);
		}
	}
}
