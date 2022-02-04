package tictim.hearthstones.tavern;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.config.ModCfg;

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

	public void updateIfPresent(Tavern tavern){
		if(taverns.containsKey(tavern.pos())) addOrUpdate(tavern);
	}

	public void clear(){
		taverns.clear();
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
		ListTag list = nbt.getList("memory", Tag.TAG_COMPOUND);
		for(int i = 0; i<list.size(); i++){
			TavernRecord r = new TavernRecord(list.getCompound(i));
			if(taverns.containsKey(r.pos()))
				Hearthstones.LOGGER.error("Error occurred during deserialization of TavernMemory, duplicated tavern data at {}", r.pos());
			else taverns.put(r.pos(), r);
		}
	}

	public void write(FriendlyByteBuf buf){
		buf.writeVarInt(taverns.size());
		for(TavernRecord e : taverns.values()) e.write(buf);
	}

	public void read(FriendlyByteBuf buf){
		this.taverns.clear();
		for(int i = buf.readVarInt(); i>0; i--){
			TavernRecord r = TavernRecord.read(buf);
			if(taverns.containsKey(r.pos()))
				Hearthstones.LOGGER.error("Error occurred during deserialization of TavernMemory, duplicated tavern data at {}", r.pos());
			else this.taverns.put(r.pos(), r);
		}
	}
}
