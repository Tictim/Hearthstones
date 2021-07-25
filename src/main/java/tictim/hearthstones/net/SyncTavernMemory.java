package tictim.hearthstones.net;

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import tictim.hearthstones.data.GlobalTavernMemory;
import tictim.hearthstones.data.PlayerTavernMemory;

public class SyncTavernMemory{
	public final CompoundTag player;
	public final CompoundTag global;

	public SyncTavernMemory(PlayerTavernMemory cap, GlobalTavernMemory global, Player player){
		this(cap.serializeNBT(), global.serializeAccessibleTaverns(player));
	}
	public SyncTavernMemory(CompoundTag nbt, CompoundTag globalNbt){
		this.player = nbt;
		this.global = globalNbt;
	}
}
