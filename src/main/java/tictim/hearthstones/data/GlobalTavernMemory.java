package tictim.hearthstones.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.capabilities.Capability;
import tictim.hearthstones.Hearthstones;

import javax.annotation.Nullable;

public class GlobalTavernMemory extends TavernMemory{
	public CompoundNBT serializeAccessibleTaverns(PlayerEntity player){
		CompoundNBT nbt = new CompoundNBT();
		ListNBT list = new ListNBT();
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
