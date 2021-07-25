package tictim.hearthstones.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import tictim.hearthstones.logic.Tavern;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.SyncTavernMemory;
import tictim.hearthstones.net.SyncTavernMemoryRequest;
import tictim.hearthstones.utils.TavernType;

import javax.annotation.Nullable;
import java.util.Objects;

public class PlayerTavernMemory extends TavernMemory implements ICapabilityProvider{
	private final Player player;

	private int cooldown;
	@Nullable
	private TavernPos selected;
	@Nullable
	private TavernPos homeTavern;

	public PlayerTavernMemory(Player player){
		this.player = player;
	}

	public void sync(){
		if(player instanceof ServerPlayer)
			ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new SyncTavernMemory(this, GlobalTavernMemory.get(), player));
	}

	public void requestSync(){
		ModNet.CHANNEL.sendToServer(new SyncTavernMemoryRequest());
	}

	public int getCooldown(){
		return cooldown;
	}
	public boolean hasCooldown(){
		return this.cooldown>0;
	}
	public void setCooldown(int secs){
		this.cooldown = secs;
	}

	public void setHomeTavern(@Nullable TavernPos homeTavern){
		TavernRecord cache = getHomeTavern();
		this.homeTavern = homeTavern;
		if(cache!=getHomeTavern()&&cache!=null&&cache.getTavernType()==TavernType.SHABBY) delete(cache.getTavernPos());
	}
	@Nullable
	public TavernPos getHomePos(){
		return this.homeTavern;
	}
	@Nullable
	public TavernRecord getHomeTavern(){
		return this.homeTavern==null ? null : get(this.homeTavern);
	}

	public void select(@Nullable TavernPos pos){
		if(!Objects.equals(this.selected, pos)) this.selected = pos;
	}
	@Nullable
	public TavernPos getSelectedPos(){
		return this.selected;
	}
	@Nullable
	public TavernRecord getSelectedTavern(){
		if(this.selected==null) return null;
		else{
			TavernRecord t = get(this.selected);
			return t!=null ? t : GlobalTavernMemory.get().get(this.selected);
		}
	}

	@Nullable
	@Override
	public TavernRecord delete(TavernPos key){
		TavernRecord deleted = super.delete(key);
		if(deleted!=null){
			if(selected!=null&&selected.equals(deleted.getTavernPos())) selected = null;
			if(homeTavern!=null&&homeTavern.equals(deleted.getTavernPos())) homeTavern = null;
		}
		return deleted;
	}

	@Override
	public void add(Tavern tavern){
		TavernRecord t = add0(tavern);
		if(getHomeTavern()==null) setHomeTavern(t.getTavernPos());
	}

	@Override
	public CompoundTag serializeNBT(){
		CompoundTag nbt = super.serializeNBT();
		if(selected!=null) nbt.put("selected", selected.serialize());
		if(homeTavern!=null) nbt.put("homeTavern", homeTavern.serialize());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt){
		super.deserializeNBT(nbt);
		selected = nbt.contains("selected", NBT.TAG_COMPOUND) ? new TavernPos(nbt.getCompound("selected")) : null;
		homeTavern = nbt.contains("homeTavern", NBT.TAG_COMPOUND) ? new TavernPos(nbt.getCompound("homeTavern")) : null;
	}

	@Nullable
	@Override
	protected Capability<?> getMatchingCapability(){
		return PLAYER;
	}

	public static PlayerTavernMemory get(Player player){
		return player.getCapability(PLAYER).orElseThrow(() -> new RuntimeException("Unexpected"));
	}

	@Nullable
	public static PlayerTavernMemory tryGet(Player player){
		return player.getCapability(PLAYER).orElse(null);
	}
}
