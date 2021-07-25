package tictim.hearthstones.logic;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
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
		Set<Player> players = new HashSet<>();
		for(Entity e : entities){
			if(e instanceof Player) players.add((Player)e);
			else ctx.warpEntity(e);
		}
		for(Player e : players) ctx.warpEntity(e);
		ctx.warpEntity(ctx.getPlayer());
	}

	public static Set<Entity> getWarpTargets(HearthingContext ctx){
		Set<Entity> set = new HashSet<>();
		addRidingEntities(ctx.getPlayer(), set);
		for(LivingEntity e : ctx.getPlayer().level.getEntitiesOfClass(LivingEntity.class, new AABB(ctx.getOriginPos(), ctx.getOriginPos()).inflate(8))){
			if(!set.contains(e)&&e.isAlive()&&(
					(e instanceof Mob&&((Mob)e).getLeashHolder()==ctx.getPlayer())||
							(e instanceof TamableAnimal&&((TamableAnimal)e).getOwner()==ctx.getPlayer())||
							(e instanceof AbstractHorse&&((AbstractHorse)e).isTamed()&&ctx.getPlayer().getUUID().equals(((AbstractHorse)e).getOwnerUUID()))||
							(!ctx.getPlayer().isShiftKeyDown()&&e instanceof Player&&!e.isSleeping()&&(e.getMainHandItem().getItem()==ModItems.COMPANION_STONE.get()
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
