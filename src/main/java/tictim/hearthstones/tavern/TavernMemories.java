package tictim.hearthstones.tavern;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import tictim.hearthstones.Caps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TavernMemories implements ICapabilitySerializable<CompoundTag>{
	private final TavernMemory global = new TavernMemory();
	private final Map<UUID, PlayerTavernMemory> players = new HashMap<>();

	public TavernMemory getGlobal(){
		return global;
	}
	public PlayerTavernMemory getPlayer(Player player){
		return getPlayer(player.getUUID());
	}
	public PlayerTavernMemory getPlayer(UUID id){
		return players.computeIfAbsent(id, uuid -> new PlayerTavernMemory());
	}

	@Nullable private LazyOptional<TavernMemories> self;

	@Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap==Caps.TAVERN_MEMORIES){
			if(self==null) self = LazyOptional.of(() -> this);
			return self.cast();
		}else return LazyOptional.empty();
	}

	@Override public CompoundTag serializeNBT(){
		CompoundTag tag = new CompoundTag();
		tag.put("global", global.write());
		ListTag list = new ListTag();
		for(Map.Entry<UUID, PlayerTavernMemory> e : players.entrySet()){
			CompoundTag playerTag = e.getValue().write();
			playerTag.putUUID("id", e.getKey());
			list.add(playerTag);
		}
		tag.put("players", list);
		return tag;
	}
	@Override public void deserializeNBT(CompoundTag nbt){
		global.read(nbt.getCompound("global"));
		ListTag players = nbt.getList("players", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i<players.size(); i++){
			CompoundTag playerTag = players.getCompound(i);
			if(playerTag.hasUUID("id")){
				PlayerTavernMemory value = new PlayerTavernMemory();
				value.read(playerTag);
				this.players.put(playerTag.getUUID("id"), value);
			}
		}
	}

	public static TavernMemory global(){
		return expect().getGlobal();
	}

	public static PlayerTavernMemory player(Player player){
		return expect().getPlayer(player.getUUID());
	}
	public static PlayerTavernMemory player(UUID id){
		return expect().getPlayer(id);
	}

	public static TavernMemories expect(){
		TavernMemories m = get();
		if(m==null) throw new IllegalStateException("No Tavern Memory present for server");
		return m;
	}

	@SuppressWarnings("ConstantConditions") @Nullable public static TavernMemories get(){
		MinecraftServer s = ServerLifecycleHooks.getCurrentServer();
		return s!=null ? s.overworld().getCapability(Caps.TAVERN_MEMORIES).orElse(null) : null;
	}
}
