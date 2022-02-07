package tictim.hearthstones.hearthstone;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.tavern.Tavern;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static tictim.hearthstones.hearthstone.HearthUtils.warp;

public class CompanionHearthstone extends SelectionHearthstone{
	public CompanionHearthstone(){
		super(ModCfg.companionHearthstone);
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
		for(EntityLivingBase e : warpingEntity.world.getEntitiesWithinAABB(EntityLivingBase.class,
				new AxisAlignedBB(warpingEntity.posX, warpingEntity.posY, warpingEntity.posZ, warpingEntity.posX, warpingEntity.posY, warpingEntity.posZ).grow(8)))
			if(isConsideredCompanion(warpingEntity, e))
				addRidingEntities(e, set);
		set.remove(warpingEntity);
		return set;
	}

	protected boolean isConsideredCompanion(Entity warpingEntity, EntityLivingBase entity){
		return entity.isEntityAlive()&&(entity instanceof EntityLiving&&((EntityLiving)entity).getLeashHolder()==warpingEntity||
				entity instanceof IEntityOwnable&&((IEntityOwnable)entity).getOwner()==warpingEntity||
				entity instanceof AbstractHorse&&((AbstractHorse)entity).isTame()&&warpingEntity.getUniqueID().equals(((AbstractHorse)entity).getOwnerUniqueId())||
				warpingEntity.isSneaking()&&entity instanceof EntityPlayer&&entity!=warpingEntity&&!entity.isPlayerSleeping()&&isHoldingCompanionStone(entity));
	}

	protected static boolean isHoldingCompanionStone(EntityLivingBase entity){
		return (entity.getHeldItemMainhand().getItem()==ModItems.COMPANION_STONE)||
				(entity.getHeldItemOffhand().getItem()==ModItems.COMPANION_STONE);
	}

	protected static void addRidingEntities(Entity e, Set<Entity> set){
		addPassengers(e.getLowestRidingEntity(), set);
	}
	protected static void addPassengers(Entity root, Set<Entity> set){
		if(set.add(root)){
			List<Entity> passengers = root.getPassengers();
			if(!passengers.isEmpty()) for(Entity e : passengers) addPassengers(e, set);
		}
	}
}
