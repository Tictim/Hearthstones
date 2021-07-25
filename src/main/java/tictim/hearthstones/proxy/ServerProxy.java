package tictim.hearthstones.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.OpEntry;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import tictim.hearthstones.data.GlobalTavernMemory;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.data.TavernMemory;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.SyncTavernMemory;

public class ServerProxy implements IProxy{
	@Override
	public boolean isOp(PlayerEntity player){
		MinecraftServer server = player.getServer();
		if(server==null) return false;
		OpEntry e = server.getPlayerList().getOps().get(player.getGameProfile());
		return e!=null&&e.getLevel()>=0;
	}

	@Override
	public void openHearthstoneGui(World world, PlayerEntity player){
		if(player instanceof ServerPlayerEntity) ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SyncTavernMemory(PlayerTavernMemory.get(player), GlobalTavernMemory.get(), player));
	}

	@Override
	public GlobalTavernMemory getGlobalTavernMemory(){
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		return server.overworld().getCapability(TavernMemory.GLOBAL).orElseThrow(() -> new RuntimeException("Unable to access global tavern memory"));
	}
}
