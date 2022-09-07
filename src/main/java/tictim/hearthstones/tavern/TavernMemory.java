package tictim.hearthstones.tavern;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
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

	@Nullable public Tavern delete(World world, BlockPos pos){
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

	public NBTTagCompound write(){
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for(TavernRecord e : taverns.values()) list.appendTag(e.write());
		nbt.setTag("memory", list);
		return nbt;
	}

	public void read(NBTTagCompound nbt){
		this.taverns.clear();
		NBTTagList list = nbt.getTagList("memory", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i<list.tagCount(); i++){
			TavernRecord r = new TavernRecord(list.getCompoundTagAt(i));
			if(taverns.containsKey(r.pos()))
				Hearthstones.LOGGER.error("Error occurred during deserialization of TavernMemory, duplicated tavern data at {}", r.pos());
			else taverns.put(r.pos(), r);
		}
	}

	public void write(ByteBuf buf){
		buf.writeInt(taverns.size());
		for(TavernRecord e : taverns.values()) e.write(buf);
	}

	public void read(ByteBuf buf){
		this.taverns.clear();
		for(int i = buf.readInt(); i>0; i--){
			TavernRecord r = TavernRecord.read(buf);
			if(taverns.containsKey(r.pos()))
				Hearthstones.LOGGER.error("Error occurred during deserialization of TavernMemory, duplicated tavern data at {}", r.pos());
			else this.taverns.put(r.pos(), r);
		}
	}
}