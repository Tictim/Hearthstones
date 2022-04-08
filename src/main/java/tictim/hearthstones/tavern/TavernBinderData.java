package tictim.hearthstones.tavern;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import tictim.hearthstones.Caps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TavernBinderData implements ICapabilitySerializable<CompoundTag>{
	private final boolean infiniteWaypoints;
	private int emptyWaypoints;
	public final TavernMemory memory = new TavernMemory();

	public TavernBinderData(boolean infiniteWaypoints){
		this.infiniteWaypoints = infiniteWaypoints;
	}

	public boolean isInfiniteWaypoints(){
		return infiniteWaypoints;
	}

	public int getWaypoints(){
		return memory.taverns().size();
	}

	public int getEmptyWaypoints(){
		return emptyWaypoints;
	}
	public void setEmptyWaypoints(int emptyWaypoints){
		this.emptyWaypoints = emptyWaypoints;
	}

	public boolean syncTo(TavernMemory m){
		return sync(this.memory, m, Integer.MAX_VALUE)>0;
	}

	public boolean syncFrom(TavernMemory m){
		if(infiniteWaypoints)
			return sync(m, this.memory, Integer.MAX_VALUE)>0;
		int synced = sync(m, this.memory, emptyWaypoints);
		this.emptyWaypoints -= synced;
		return synced>0;
	}

	private static int sync(TavernMemory from, TavernMemory to, int maxEntry){
		if(maxEntry<=0) return 0;
		int entry = 0;
		for(TavernRecord t : from.taverns().values()){
			if(to.has(t.pos())) continue;
			to.addOrUpdate(t);
			if(++entry>=maxEntry) break;
		}
		return entry;
	}

	public void overwrite(TavernBinderData data){
		this.emptyWaypoints = data.emptyWaypoints;
		this.memory.clear();
		for(TavernRecord r : data.memory.taverns().values())
			this.memory.addOrUpdate(r);
	}

	/**
	 * Tries to add or update the waypoint.
	 *
	 * @return {@code true} if the action succeed, {@code false} if there's no empty waypoints left.
	 */
	public boolean addOrUpdateWaypoint(Tavern tavern){
		if(infiniteWaypoints){
			memory.addOrUpdate(tavern);
			return true;
		}
		boolean overwrite = memory.has(tavern.pos());
		if(overwrite||emptyWaypoints>0){
			memory.addOrUpdate(tavern);
			if(!overwrite) emptyWaypoints--;
			return true;
		}else return false;
	}

	/**
	 * Increase amount of empty waypoints by {@code waypoint}.
	 *
	 * @return Whether the action succeed. Number of waypoints will not go above {@code Integer.MAX_VALUE}. This action always fails if the binder have infinite waypoints (i.e. {@code isInfiniteWaypoints() == true}).
	 */
	public boolean addEmptyWaypoint(int waypoint){
		if(infiniteWaypoints||(Integer.MAX_VALUE-waypoint<emptyWaypoints)) return false;
		this.emptyWaypoints += waypoint;
		return true;
	}

	@Nullable private LazyOptional<TavernBinderData> self;

	@Nonnull @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
		if(cap!=Caps.BINDER_DATA) return LazyOptional.empty();
		if(self==null) self = LazyOptional.of(() -> this);
		return self.cast();
	}

	@Override public CompoundTag serializeNBT(){
		CompoundTag tag = memory.write();
		if(!infiniteWaypoints&&emptyWaypoints>0)
			tag.putInt("Waypoints", emptyWaypoints);
		return tag;
	}
	@Override public void deserializeNBT(CompoundTag nbt){
		memory.read(nbt);
		if(!infiniteWaypoints)
			this.emptyWaypoints = nbt.getInt("Waypoints");
	}
}
