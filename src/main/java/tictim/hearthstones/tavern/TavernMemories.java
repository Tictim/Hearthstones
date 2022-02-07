package tictim.hearthstones.tavern;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import tictim.hearthstones.Caps;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TavernMemories implements ICapabilitySerializable<NBTTagCompound>{
	private final TavernMemory global = new TavernMemory();
	private final Map<UUID, PlayerTavernMemory> players = new HashMap<>();

	public TavernMemory getGlobal(){
		return global;
	}
	public PlayerTavernMemory getPlayer(EntityPlayer player){
		return getPlayer(player.getUniqueID());
	}
	public PlayerTavernMemory getPlayer(UUID id){
		return players.computeIfAbsent(id, uuid -> new PlayerTavernMemory());
	}

	@Override public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing facing){
		return cap==Caps.TAVERN_MEMORIES;
	}
	@Override public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing side){
		return cap==Caps.TAVERN_MEMORIES ? Caps.TAVERN_MEMORIES.cast(this) : null;
	}

	@Override public NBTTagCompound serializeNBT(){
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("global", global.write());
		NBTTagList list = new NBTTagList();
		for(Map.Entry<UUID, PlayerTavernMemory> e : players.entrySet()){
			NBTTagCompound playerTag = e.getValue().write();
			playerTag.setUniqueId("id", e.getKey());
			list.appendTag(playerTag);
		}
		tag.setTag("players", list);
		return tag;
	}
	@Override public void deserializeNBT(NBTTagCompound nbt){
		global.read(nbt.getCompoundTag("global"));
		NBTTagList players = nbt.getTagList("players", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i<players.tagCount(); i++){
			NBTTagCompound playerTag = players.getCompoundTagAt(i);
			if(playerTag.hasUniqueId("id")){
				PlayerTavernMemory value = new PlayerTavernMemory();
				value.read(playerTag);
				this.players.put(playerTag.getUniqueId("id"), value);
			}
		}
	}

	public static TavernMemory global(){
		return expect().getGlobal();
	}

	public static PlayerTavernMemory player(EntityPlayer player){
		return expect().getPlayer(player.getUniqueID());
	}
	public static PlayerTavernMemory player(UUID id){
		return expect().getPlayer(id);
	}

	public static TavernMemories expect(){
		TavernMemories m = get();
		if(m==null) throw new IllegalStateException("No Tavern Memory present for server");
		return m;
	}

	@Nullable public static TavernMemories get(){
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		return server!=null ? server.getEntityWorld().getCapability(Caps.TAVERN_MEMORIES, null) : null;
	}
}
