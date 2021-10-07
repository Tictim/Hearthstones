package tictim.hearthstones.capability;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernPos;
import tictim.hearthstones.tavern.TavernRecord;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

public class TavernMemory{
	private final Map<TavernPos, TavernRecord> taverns = Maps.newHashMap();

	public Map<TavernPos, TavernRecord> taverns(){
		return Collections.unmodifiableMap(taverns);
	}

	@Nullable public Tavern delete(Level world, BlockPos pos){
		return delete(new TavernPos(world, pos));
	}
	@Nullable public Tavern delete(TavernPos key){
		return taverns.remove(key);
	}

	@Nullable public Tavern get(TavernPos pos){
		return taverns.get(pos);
	}
	public boolean has(TavernPos pos){
		return taverns.containsKey(pos);
	}

	public void addOrUpdate(Tavern tavern){
		TavernPos pos = tavern.pos();
		TavernRecord t = tavern.toRecord();
		taverns.put(pos, t);
		if(ModCfg.traceTavernUpdate())
			Hearthstones.LOGGER.info("Updated tavern at {}", pos);
	}

	public boolean isEmpty(){
		return taverns.isEmpty();
	}

	public CompoundTag write(){
		CompoundTag nbt = new CompoundTag();
		ListTag list = new ListTag();
		for(TavernRecord e : taverns.values()) list.add(e.write());
		nbt.put("memory", list);
		return nbt;
	}

	public void read(CompoundTag nbt){
		this.taverns.clear();
		ListTag list = nbt.getList("memory", NBT.TAG_COMPOUND);
		for(int i = 0; i<list.size(); i++){
			TavernRecord e = new TavernRecord(list.getCompound(i));
			if(taverns.containsKey(e.pos())) Hearthstones.LOGGER.error("Error occurred during deserialization of TavernMemory, duplicated tavern data at {}", e.pos());
			else taverns.put(e.pos(), e);
		}
	}

	public static PlayerTavernMemory expectFromPlayer(Player player){
		PlayerTavernMemory playerTavernMemory = fromPlayer(player);
		if(playerTavernMemory==null) throw new IllegalStateException("No Player Tavern Memory present for player "+player);
		return playerTavernMemory;
	}

	@SuppressWarnings("ConstantConditions")
	@Nullable
	public static PlayerTavernMemory fromPlayer(Player player){
		TavernMemory m = player.getCapability(Caps.TAVERN_MEMORY).orElse(null);
		return m instanceof PlayerTavernMemory p ? p : null;
	}

	public static TavernMemory expectGlobal(boolean clientSide){
		return clientSide ? expectClientGlobal() : expectServerGlobal();
	}

	@Nullable
	public static TavernMemory global(boolean clientSide){
		return clientSide ? clientGlobal() : serverGlobal();
	}

	public static TavernMemory expectServerGlobal(){
		TavernMemory tavernMemory = serverGlobal();
		if(tavernMemory==null) throw new IllegalStateException("No Global Tavern Memory present for server");
		return tavernMemory;
	}

	public static TavernMemory expectClientGlobal(){
		TavernMemory tavernMemory = clientGlobal();
		if(tavernMemory==null) throw new IllegalStateException("No Global Tavern Memory present for client");
		return tavernMemory;
	}

	@SuppressWarnings("ConstantConditions")
	@Nullable
	public static TavernMemory serverGlobal(){
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		return server.overworld().getCapability(Caps.TAVERN_MEMORY).orElse(null);
	}

	@Nullable
	public static TavernMemory clientGlobal(){
		return Client.clientGlobal();
	}

	private static final class Client{
		private Client(){}

		@SuppressWarnings("ConstantConditions")
		@Nullable
		private static TavernMemory clientGlobal(){
			if(ServerLifecycleHooks.getCurrentServer()!=null) return serverGlobal();
			ClientLevel level = Minecraft.getInstance().level;
			if(level==null) return null;
			return level.getCapability(Caps.TAVERN_MEMORY).orElse(null);
		}
	}
}
