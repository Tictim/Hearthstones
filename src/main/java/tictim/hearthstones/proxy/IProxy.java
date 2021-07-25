package tictim.hearthstones.proxy;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import tictim.hearthstones.data.GlobalTavernMemory;

public interface IProxy{
	boolean isOp(Player player);
	void openHearthstoneGui(Level world, Player player);
	GlobalTavernMemory getGlobalTavernMemory();
}
