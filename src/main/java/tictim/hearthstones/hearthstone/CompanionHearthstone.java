package tictim.hearthstones.hearthstone;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
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
			for(Entity e : getWarpTargets(context.getPlayer())) warp(e, selectedTavern.pos().dim, warpPos, false);
			warp(context.getPlayer(), selectedTavern.pos().dim, warpPos, true);
			context.getStack().hurtAndBreak(1,
					context.getPlayer(),
					player -> {
						if(context.getHand()!=null) player.broadcastBreakEvent(context.getHand());
					});
			context.getMemory().setCooldown(config.cooldown());
		};
	}

	public static Set<Entity> getWarpTargets(Entity fuck){
		Set<Entity> set = new HashSet<>();
		addRidingEntities(fuck, set);
		for(LivingEntity e : fuck.level.getEntitiesOfClass(LivingEntity.class,
				new AABB(fuck.getX(), fuck.getY(), fuck.getZ(), fuck.getX(), fuck.getY(), fuck.getZ()).inflate(8)))
			if(e.isAlive()&&(e instanceof Mob m&&m.getLeashHolder()==fuck||
					e instanceof TamableAnimal ta&&ta.getOwner()==fuck||
					e instanceof AbstractHorse ah&&ah.isTamed()&&fuck.getUUID().equals(ah.getOwnerUUID())||
					!fuck.isShiftKeyDown()&&e instanceof Player&&e!=fuck&&!e.isSleeping()&&(
							e.getMainHandItem().is(ModItems.COMPANION_STONE.get())||
									e.getOffhandItem().is(ModItems.COMPANION_STONE.get()))))
				addRidingEntities(e, set);
		set.remove(fuck);
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
