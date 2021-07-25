package tictim.hearthstones.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import tictim.hearthstones.client.screen.HearthstoneScreen;
import tictim.hearthstones.data.GlobalTavernMemory;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.data.TavernMemory;

import java.util.Objects;

public class ClientProxy extends ServerProxy{
	@Override
	public boolean isOp(Player player){
		return player.getServer()==null ? player.hasPermissions(1) : super.isOp(player);
	}

	@Override
	public void openHearthstoneGui(Level world, Player player){
		if(player.getServer()!=null){
			if(player instanceof ServerPlayer) super.openHearthstoneGui(world, player);
		}else Minecraft.getInstance().setScreen(new HearthstoneScreen(PlayerTavernMemory.get(player)));
	}

	@Override
	public GlobalTavernMemory getGlobalTavernMemory(){
		if(ServerLifecycleHooks.getCurrentServer()!=null) return super.getGlobalTavernMemory();
		return Objects.requireNonNull(Minecraft.getInstance().level).getCapability(TavernMemory.GLOBAL).orElseThrow(() -> new RuntimeException("Unable to access global tavern memory"));
	}
}
