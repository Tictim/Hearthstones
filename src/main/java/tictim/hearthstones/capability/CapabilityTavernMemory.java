package tictim.hearthstones.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.tavern.TavernRecord;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityTavernMemory extends TavernMemory implements ICapabilitySerializable<CompoundTag>{
	@Nullable private LazyOptional<TavernMemory> self;

	@Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap==Caps.TAVERN_MEMORY){
			if(self==null) self = LazyOptional.of(() -> this);
			return self.cast();
		}else return LazyOptional.empty();
	}

	@Override public CompoundTag serializeNBT(){
		return write();
	}
	@Override public void deserializeNBT(CompoundTag nbt){
		read(nbt);
	}
}
