package tictim.hearthstones.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.capability.CapabilityTavernMemory;
import tictim.hearthstones.capability.PlayerTavernMemory;
import tictim.hearthstones.capability.TavernMemory;

@Mod.EventBusSubscriber(modid = Hearthstones.MODID)
public final class TavernMemoryEventHandler{
	private TavernMemoryEventHandler(){}

	private static final ResourceLocation CAP_KEY = new ResourceLocation(Hearthstones.MODID, "tavern_memory");

	@SubscribeEvent
	public static void attachWorldCapabilities(AttachCapabilitiesEvent<Level> event){
		Level level = event.getObject();
		if(level.isClientSide||level.dimension().equals(Level.OVERWORLD)) event.addCapability(CAP_KEY, new CapabilityTavernMemory());
	}

	@SubscribeEvent
	public static void attachPlayerCapabilities(AttachCapabilitiesEvent<Entity> event){
		if(event.getObject() instanceof Player p)
			event.addCapability(CAP_KEY, new PlayerTavernMemory(p));
	}

	@SubscribeEvent
	public static void clonePlayer(PlayerEvent.Clone event){
		PlayerTavernMemory m1 = TavernMemory.expectFromPlayer(event.getPlayer());
		PlayerTavernMemory m2 = TavernMemory.expectFromPlayer(event.getOriginal());
		m1.read(m2.write());
	}

	@SubscribeEvent
	public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event){
		TavernMemory.expectFromPlayer(event.getPlayer()).sync();
	}

	@SubscribeEvent
	public static void playerTick(TickEvent.PlayerTickEvent event){
		if(!event.player.level.isClientSide&&event.player.level.getGameTime()%20==0&&event.player.isAlive()){
			PlayerTavernMemory memory = TavernMemory.expectFromPlayer(event.player);
			if(memory.getCooldown()>0) memory.setCooldown(memory.getCooldown()-1);
		}
	}

	@Mod.EventBusSubscriber(modid = Hearthstones.MODID, value = Dist.CLIENT)
	public static final class Client{
		private Client(){}

		@SubscribeEvent
		public static void clientPlayerRespawn(ClientPlayerNetworkEvent.RespawnEvent event){
			PlayerTavernMemory m1 = TavernMemory.fromPlayer(event.getNewPlayer());
			PlayerTavernMemory m2 = TavernMemory.fromPlayer(event.getOldPlayer());
			if(m1!=null){
				if(m2!=null) m1.read(m2.write());
				else m1.requestSync();
			}
		}
	}
}
