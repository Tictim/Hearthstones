package tictim.hearthstones.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.data.GlobalTavernMemory;
import tictim.hearthstones.data.PlayerTavernMemory;

@Mod.EventBusSubscriber(modid = Hearthstones.MODID)
public final class TavernMemoryEventHandler{
	private TavernMemoryEventHandler(){}

	private static final ResourceLocation KEY_GLOBAL = new ResourceLocation(Hearthstones.MODID, "global_tavern_memory");
	private static final ResourceLocation KEY_PLAYER = new ResourceLocation(Hearthstones.MODID, "player_tavern_memory");

	@SubscribeEvent
	public static void attachWorldCapabilities(AttachCapabilitiesEvent<Level> event){
		Level w = event.getObject();
		if(w.isClientSide||w.dimension().equals(Level.OVERWORLD)) event.addCapability(KEY_GLOBAL, new GlobalTavernMemory());
	}

	@SubscribeEvent
	public static void attachPlayerCapabilities(AttachCapabilitiesEvent<Entity> event){
		if(event.getObject() instanceof Player)
			event.addCapability(KEY_PLAYER, new PlayerTavernMemory((Player)event.getObject()));
	}

	@SubscribeEvent
	public static void clonePlayer(PlayerEvent.Clone event){
		PlayerTavernMemory m1 = PlayerTavernMemory.get(event.getPlayer());
		PlayerTavernMemory m2 = PlayerTavernMemory.get(event.getOriginal());
		m1.deserializeNBT(m2.serializeNBT());
	}

	@SubscribeEvent
	public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event){
		PlayerTavernMemory.get(event.getPlayer()).sync();
	}

	@SubscribeEvent
	public static void playerTick(TickEvent.PlayerTickEvent event){
		if(!event.player.level.isClientSide&&event.player.level.getGameTime()%20==0&&event.player.isAlive()){
			PlayerTavernMemory memory = PlayerTavernMemory.get(event.player);
			if(memory.getCooldown()>0) memory.setCooldown(memory.getCooldown()-1);
		}
	}

	@Mod.EventBusSubscriber(modid = Hearthstones.MODID, value = Dist.CLIENT)
	public static final class Client{
		private Client(){}

		@SubscribeEvent
		public static void clientPlayerRespawn(ClientPlayerNetworkEvent.RespawnEvent event){
			PlayerTavernMemory m1 = PlayerTavernMemory.tryGet(event.getNewPlayer());
			PlayerTavernMemory m2 = PlayerTavernMemory.tryGet(event.getOldPlayer());
			if(m1!=null){
				if(m2!=null) m1.deserializeNBT(m2.serializeNBT());
				else m1.requestSync();
			}
		}
	}
}
