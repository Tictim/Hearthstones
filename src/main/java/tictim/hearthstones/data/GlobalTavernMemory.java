package tictim.hearthstones.data;

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.capabilities.Capability;
import tictim.hearthstones.Hearthstones;

import javax.annotation.Nullable;

public class GlobalTavernMemory extends TavernMemory{
	public CompoundTag serializeAccessibleTaverns(Player player){
		CompoundTag nbt = new CompoundTag();
		ListTag list = new ListTag();
		for(TavernRecord e : memories()) if(e.getOwner().hasAccessPermission(player)) list.add(e.serializeNBT());
		nbt.put("memory", list);
		return nbt;
	}

	@Nullable
	@Override
	protected Capability<?> getMatchingCapability(){
		return GLOBAL;
	}

	public static GlobalTavernMemory get(){
		return Hearthstones.PROXY.getGlobalTavernMemory();
	}
}
