package tictim.hearthstones;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.SyncHomePosMsg;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernMemories;

import static tictim.hearthstones.Hearthstones.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class HearthstonesEventHandler{
	@SubscribeEvent
	public static void blockBreakEvent(LivingDestroyBlockEvent event){
		EntityLivingBase entity = event.getEntityLiving();
		TileEntity te = entity.world.getTileEntity(event.getPos());
		if(te instanceof Tavern){
			boolean canBreak = entity instanceof EntityPlayer&&canBreak((Tavern)te, (EntityPlayer)entity);
			if(!canBreak) event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void breakSpeedEvent(BreakSpeed event){
		EntityPlayer player = event.getEntityPlayer();
		TileEntity te = player.world.getTileEntity(event.getPos());
		if(te instanceof Tavern){
			boolean canBreak = canBreak((Tavern)te, player);
			if(!canBreak) event.setCanceled(true);
		}
	}

	private static boolean canBreak(Tavern te, EntityPlayer player){
		return player.isCreative()||te.owner().isOwnerOrOp(player);
	}

	private static final ResourceLocation CAP_KEY = new ResourceLocation(Hearthstones.MODID, "tavern_memory");

	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<World> event){
		World world = event.getObject();
		if(!world.isRemote&&world.provider.getDimension()==0) event.addCapability(CAP_KEY, new TavernMemories());
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event){
		if(!event.player.world.isRemote&&event.player.world.getTotalWorldTime()%20==0&&event.player.isEntityAlive()){
			PlayerTavernMemory memory = TavernMemories.player(event.player);
			if(memory.getCooldown()>0) memory.setCooldown(memory.getCooldown()-1);
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event){
		if(event.player instanceof EntityPlayerMP)
			ModNet.CHANNEL.sendTo(new SyncHomePosMsg(TavernMemories.player(event.player).getHomePos()), (EntityPlayerMP)event.player);
	}
}
