package tictim.hearthstones.proxy;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import tictim.hearthstones.data.GlobalTavernMemory;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.data.TavernMemory;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.SyncTavernMemory;

public class ServerProxy implements IProxy{
	@Override
	public boolean isOp(Player player){
		MinecraftServer server = player.getServer();
		if(server==null) return false;
		ServerOpListEntry e = server.getPlayerList().getOps().get(player.getGameProfile());
		return e!=null&&e.getLevel()>=0;
	}

	@Override
	public void openHearthstoneGui(Level world, Player player){
		if(player instanceof ServerPlayer) ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new SyncTavernMemory(PlayerTavernMemory.get(player), GlobalTavernMemory.get(), player));
	}

	@Override
	public GlobalTavernMemory getGlobalTavernMemory(){
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		return server.overworld().getCapability(TavernMemory.GLOBAL).orElseThrow(() -> new RuntimeException("Unable to access global tavern memory"));
	}
}
