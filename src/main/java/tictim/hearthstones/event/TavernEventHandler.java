package tictim.hearthstones.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
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
		TileEntity te = entity.world.getTileEntity(event.getPos());
		if(te instanceof Tavern){
			Tavern tavern = (Tavern)te;
			boolean canBreak = entity instanceof PlayerEntity&&canBreak(tavern, (PlayerEntity)entity);
			if(!canBreak) event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void breakSpeedEvent(PlayerEvent.BreakSpeed event){
		PlayerEntity player = event.getPlayer();
		TileEntity _te = player.world.getTileEntity(event.getPos());
		if(_te instanceof Tavern){
			Tavern tavern = (Tavern)_te;
			boolean canBreak = canBreak(tavern, player);
			if(!canBreak) event.setCanceled(true);
		}
	}

	private static boolean canBreak(Tavern te, PlayerEntity player){
		return player.isCreative()||te.owner().isOwnerOrOp(player);
	}
}
