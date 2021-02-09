package tictim.hearthstones.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import tictim.hearthstones.data.GlobalTavernMemory;

public interface IProxy{
	boolean isOp(PlayerEntity player);
	void openHearthstoneGui(World world, PlayerEntity player);
	GlobalTavernMemory getGlobalTavernMemory();
}
