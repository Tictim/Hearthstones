package tictim.hearthstones.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.SyncTavernMemoryMsg;
import tictim.hearthstones.net.SyncTavernMemoryRequestMsg;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernPos;
import tictim.hearthstones.tavern.TavernType;

import javax.annotation.Nullable;
import java.util.Objects;

public class PlayerTavernMemory extends CapabilityTavernMemory implements ICapabilitySerializable<CompoundTag>{
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
		if(player instanceof ServerPlayer serverPlayer)
			ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncTavernMemoryMsg(player));
	}

	public void requestSync(){
		ModNet.CHANNEL.sendToServer(new SyncTavernMemoryRequestMsg());
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
		Tavern cache = getHomeTavern();
		this.homeTavern = homeTavern;
		if(cache!=getHomeTavern()&&cache!=null&&cache.type()==TavernType.SHABBY) delete(cache.pos());
	}
	@Nullable public TavernPos getHomePos(){
		return this.homeTavern;
	}
	@Nullable public Tavern getHomeTavern(){
		return this.homeTavern==null ? null : get(this.homeTavern);
	}

	public void select(@Nullable TavernPos pos){
		if(!Objects.equals(this.selected, pos)) this.selected = pos;
	}
	@Nullable public TavernPos getSelectedPos(){
		return this.selected;
	}
	@Nullable public Tavern getSelectedTavern(){
		if(this.selected==null) return null;
		Tavern t = get(this.selected);
		return t!=null ? t : expectGlobal(player.level.isClientSide).get(this.selected);
	}

	@Nullable
	@Override
	public Tavern delete(TavernPos key){
		Tavern deleted = super.delete(key);
		if(deleted!=null){
			if(selected!=null&&selected.equals(deleted.pos())) selected = null;
			if(homeTavern!=null&&homeTavern.equals(deleted.pos())) homeTavern = null;
		}
		return deleted;
	}

	@Override
	public void addOrUpdate(Tavern tavern){
		super.addOrUpdate(tavern);
		if(getHomeTavern()==null) setHomeTavern(tavern.pos());
	}

	@Override
	public CompoundTag write(){
		CompoundTag nbt = super.write();
		if(selected!=null) nbt.put("selected", selected.write());
		if(homeTavern!=null) nbt.put("homeTavern", homeTavern.write());
		return nbt;
	}

	@Override
	public void read(CompoundTag nbt){
		super.read(nbt);
		selected = nbt.contains("selected", NBT.TAG_COMPOUND) ? new TavernPos(nbt.getCompound("selected")) : null;
		homeTavern = nbt.contains("homeTavern", NBT.TAG_COMPOUND) ? new TavernPos(nbt.getCompound("homeTavern")) : null;
	}
}
