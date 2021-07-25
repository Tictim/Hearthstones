package tictim.hearthstones.logic;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.utils.HearthingContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompanionHearthstone extends GuiHearthstone{
	public CompanionHearthstone(){
		super(ModCfg.companionHearthstone);
	}

	@Override public void onWarp(HearthingContext ctx){
		Set<Entity> entities = getWarpTargets(ctx);
		Set<PlayerEntity> players = new HashSet<>();
		for(Entity e : entities){
			if(e instanceof PlayerEntity) players.add((PlayerEntity)e);
			else ctx.warpEntity(e);
		}
		for(PlayerEntity e : players) ctx.warpEntity(e);
		ctx.warpEntity(ctx.getPlayer());
	}

	public static Set<Entity> getWarpTargets(HearthingContext ctx){
		Set<Entity> set = new HashSet<>();
		addRidingEntities(ctx.getPlayer(), set);
		for(LivingEntity e : ctx.getPlayer().level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(ctx.getOriginPos(), ctx.getOriginPos()).inflate(8))){
			if(!set.contains(e)&&e.isAlive()&&(
					(e instanceof MobEntity&&((MobEntity)e).getLeashHolder()==ctx.getPlayer())||
							(e instanceof TameableEntity&&((TameableEntity)e).getOwner()==ctx.getPlayer())||
							(e instanceof AbstractHorseEntity&&((AbstractHorseEntity)e).isTamed()&&ctx.getPlayer().getUUID().equals(((AbstractHorseEntity)e).getOwnerUUID()))||
							(!ctx.getPlayer().isShiftKeyDown()&&e instanceof PlayerEntity&&!e.isSleeping()&&(e.getMainHandItem().getItem()==ModItems.COMPANION_STONE.get()
									||e.getOffhandItem().getItem()==ModItems.COMPANION_STONE.get()))))
				addRidingEntities(e, set);
		}
		set.remove(ctx.getPlayer());
		return set;
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
