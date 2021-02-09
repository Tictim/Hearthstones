package tictim.hearthstones.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import tictim.hearthstones.client.screen.HearthstoneScreen;
import tictim.hearthstones.data.GlobalTavernMemory;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.data.TavernMemory;

public class ClientProxy extends ServerProxy{
	@Override
	public boolean isOp(PlayerEntity player){
		return player.getServer()==null ? player.hasPermissionLevel(1) : super.isOp(player);
	}

	@Override
	public void openHearthstoneGui(World world, PlayerEntity player){
		if(player.getServer()!=null){
			if(player instanceof ServerPlayerEntity) super.openHearthstoneGui(world, player);
		}else Minecraft.getInstance().displayGuiScreen(new HearthstoneScreen(PlayerTavernMemory.get(player)));
	}

	@Override
	public GlobalTavernMemory getGlobalTavernMemory(){
		if(ServerLifecycleHooks.getCurrentServer()!=null) return super.getGlobalTavernMemory();
		return Minecraft.getInstance().world.getCapability(TavernMemory.GLOBAL).orElseThrow(() -> new RuntimeException("Unable to access global tavern memory"));
	}
}
