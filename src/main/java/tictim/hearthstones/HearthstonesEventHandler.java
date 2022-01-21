package tictim.hearthstones;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.SyncHomePosMsg;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernMemories;

@Mod.EventBusSubscriber(modid = Hearthstones.MODID)
public final class HearthstonesEventHandler{
	private HearthstonesEventHandler(){}

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event){
		ModCommands.init(event.getDispatcher());
	}

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


	private static final ResourceLocation CAP_KEY = new ResourceLocation(Hearthstones.MODID, "tavern_memory");

	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<Level> event){
		Level level = event.getObject();
		if(!level.isClientSide&&level.dimension().equals(Level.OVERWORLD)) event.addCapability(CAP_KEY, new TavernMemories());
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event){
		if(!event.player.level.isClientSide&&event.player.level.getGameTime()%20==0&&event.player.isAlive()){
			PlayerTavernMemory memory = TavernMemories.player(event.player);
			if(memory.getCooldown()>0) memory.setCooldown(memory.getCooldown()-1);
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
		if(event.getPlayer() instanceof ServerPlayer sp){
			ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new SyncHomePosMsg(TavernMemories.player(sp).getHomePos()));
		}
	}
}
