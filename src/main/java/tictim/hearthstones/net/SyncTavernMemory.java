package tictim.hearthstones.net;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import tictim.hearthstones.data.GlobalTavernMemory;
import tictim.hearthstones.data.PlayerTavernMemory;

public class SyncTavernMemory{
	public final CompoundNBT player;
	public final CompoundNBT global;

	public SyncTavernMemory(PlayerTavernMemory cap, GlobalTavernMemory global, PlayerEntity player){
		this(cap.serializeNBT(), global.serializeAccessibleTaverns(player));
	}
	public SyncTavernMemory(CompoundNBT nbt, CompoundNBT globalNbt){
		this.player = nbt;
		this.global = globalNbt;
	}
}
