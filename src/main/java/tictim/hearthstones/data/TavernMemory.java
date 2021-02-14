package tictim.hearthstones.data;

import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.logic.Tavern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class TavernMemory implements ICapabilitySerializable<CompoundNBT>{
	@CapabilityInject(PlayerTavernMemory.class)
	public static Capability<PlayerTavernMemory> PLAYER = null;
	@CapabilityInject(GlobalTavernMemory.class)
	public static Capability<GlobalTavernMemory> GLOBAL = null;

	private final Map<TavernPos, TavernRecord> memories = Maps.newHashMap();
	private final Map<TavernPos, TavernRecord> view = Collections.unmodifiableMap(memories);

	public Map<TavernPos, TavernRecord> view(){
		return view;
	}
	public Collection<TavernRecord> memories(){
		return view.values();
	}

	@Nullable
	public TavernRecord delete(World world, BlockPos pos){
		return delete(new TavernPos(world, pos));
	}
	@Nullable
	public TavernRecord delete(TavernPos key){
		return memories.remove(key);
	}

	@Nullable
	public TavernRecord get(TavernPos pos){
		return memories.get(pos);
	}
	public boolean has(TavernPos pos){
		return memories.containsKey(pos);
	}

	public void add(Tavern tavern){
		add0(tavern);
	}
	protected TavernRecord add0(Tavern tavern){
		TavernPos pos = tavern.tavernPos();
		if(memories.containsKey(pos)){
			TavernRecord t = memories.get(pos);
			t.update(tavern);
			if(ModCfg.traceTavernUpdate())
				Hearthstones.LOGGER.info("Updated tavern at {}", pos);
			return t;
		}else{
			TavernRecord t = new TavernRecord(tavern);
			memories.put(t.getTavernPos(), t);
			if(ModCfg.traceTavernUpdate())
				Hearthstones.LOGGER.info("Added tavern at {}", pos);
			return t;
		}
	}

	public boolean isEmpty(){
		return memories.isEmpty();
	}

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap==getMatchingCapability()) return LazyOptional.of(() -> (T)this);
		return LazyOptional.empty();
	}

	@Nullable
	protected Capability<?> getMatchingCapability(){
		return null;
	}

	@Override
	public CompoundNBT serializeNBT(){
		CompoundNBT nbt = new CompoundNBT();
		ListNBT list = new ListNBT();
		for(TavernRecord e : memories.values()) list.add(e.serializeNBT());
		nbt.put("memory", list);
		return nbt;
	}


	@Override
	public void deserializeNBT(CompoundNBT nbt){
		this.memories.clear();
		ListNBT list = nbt.getList("memory", NBT.TAG_COMPOUND);
		for(int i = 0; i<list.size(); i++){
			TavernRecord e = new TavernRecord(list.getCompound(i));
			if(memories.containsKey(e.getTavernPos())) Hearthstones.LOGGER.error("Error occurred during deserialization of TavernMemory, duplicated tavern data at {}", e.getTavernPos());
			else memories.put(e.getTavernPos(), e);
		}
	}
}
