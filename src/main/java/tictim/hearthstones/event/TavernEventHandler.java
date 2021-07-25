package tictim.hearthstones.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.logic.Tavern;

@Mod.EventBusSubscriber(modid = Hearthstones.MODID)
public final class TavernEventHandler{
	private TavernEventHandler(){}

	@SubscribeEvent
	public static void blockBreakEvent(LivingDestroyBlockEvent event){
		LivingEntity entity = event.getEntityLiving();
		if(entity.level.getBlockEntity(event.getPos()) instanceof Tavern tavern){
			boolean canBreak = entity instanceof Player&&canBreak(tavern, (Player)entity);
			if(!canBreak) event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void breakSpeedEvent(PlayerEvent.BreakSpeed event){
		Player player = event.getPlayer();
		if(player.level.getBlockEntity(event.getPos()) instanceof Tavern tavern){
			boolean canBreak = canBreak(tavern, player);
			if(!canBreak) event.setCanceled(true);
		}
	}

	private static boolean canBreak(Tavern te, Player player){
		return player.isCreative()||te.owner().isOwnerOrOp(player);
	}
}
