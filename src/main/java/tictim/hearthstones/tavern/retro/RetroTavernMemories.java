package tictim.hearthstones.tavern.retro;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernMemory;
import tictim.hearthstones.tavern.TavernPos;
import tictim.hearthstones.tavern.TavernRecord;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

import static tictim.hearthstones.Hearthstones.MODID;

public class RetroTavernMemories extends WorldSavedData{
	public static final String NAME = MODID+"tavern_memories";

	private boolean obsolete;
	private TavernMemories tavernMemories;

	@SuppressWarnings("unused")
	public RetroTavernMemories(){
		this(NAME);
	}
	public RetroTavernMemories(String name){
		super(name);
	}

	public boolean isObsolete(){
		return obsolete;
	}
	public TavernMemories getTavernMemories(){
		return tavernMemories;
	}

	public void copyTo(TavernMemories to){
		for(Map.Entry<UUID, PlayerTavernMemory> e : this.tavernMemories.players().entrySet()){
			PlayerTavernMemory retroMemory = e.getValue();
			PlayerTavernMemory newMemory = to.getPlayer(e.getKey());
			copyTaverns(retroMemory, newMemory);
			newMemory.setHomeTavern(retroMemory.getHomePos());
		}
		copyTaverns(this.tavernMemories.getGlobal(), to.getGlobal());
	}

	private static void copyTaverns(TavernMemory from, TavernMemory to){
		for(Map.Entry<TavernPos, TavernRecord> e2 : from.taverns().entrySet()){
			to.addOrUpdate(e2.getValue());
		}
	}

	public void setObsolete(){
		if(!obsolete){
			obsolete = true;
			markDirty();
		}
	}

	@Override public void readFromNBT(NBTTagCompound tag){
		obsolete = tag.getBoolean("obsolete");
		if(obsolete) return;
		TavernMemories tavernMemories = new TavernMemories();
		if(tag.hasKey("list", Constants.NBT.TAG_LIST)){
			NBTTagList list = tag.getTagList("list", Constants.NBT.TAG_COMPOUND);
			for(int i = 0; i<list.tagCount(); i++){
				NBTTagCompound t2 = list.getCompoundTagAt(i);
				if(t2.hasUniqueId("uid"))
					Retro.readPlayerMemory(tavernMemories.getPlayer(t2.getUniqueId("uid")), t2);
			}
		}
		if(tag.hasKey("global", Constants.NBT.TAG_COMPOUND))
			Retro.readMemory(tavernMemories.getGlobal(), tag.getCompoundTag("global"));

		this.tavernMemories = tavernMemories;
	}

	@Override public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		if(obsolete) nbt.setBoolean("obsolete", true);
		return nbt;
	}

	@Nullable
	public static RetroTavernMemories get(World world){
		MapStorage s = world.getMapStorage();
		if(s==null) return null;
		return (RetroTavernMemories)s.getOrLoadData(RetroTavernMemories.class, NAME);
	}
}
