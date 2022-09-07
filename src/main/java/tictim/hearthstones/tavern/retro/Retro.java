package tictim.hearthstones.tavern.retro;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import tictim.hearthstones.tavern.AccessModifier;
import tictim.hearthstones.tavern.Owner;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernMemory;
import tictim.hearthstones.tavern.TavernPos;
import tictim.hearthstones.tavern.TavernRecord;
import tictim.hearthstones.tavern.TavernType;

import javax.annotation.Nullable;

/**
 * Various deserialization method for reading 1.0.0.x save data.
 */
public class Retro{
	public static void readPlayerMemory(PlayerTavernMemory memory, NBTTagCompound tag){
		readMemory(memory, tag);
		if(tag.hasKey("homeTavernX", Constants.NBT.TAG_INT))
			memory.setHomeTavern(new TavernPos(tag.getInteger("homeTavernDim"),
					new BlockPos(tag.getInteger("homeTavernX"), tag.getInteger("homeTavernY"), tag.getInteger("homeTavernZ"))));
	}

	public static void readMemory(TavernMemory memory, NBTTagCompound tag){
		NBTTagList list = tag.getTagList("memory", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i<list.tagCount(); i++){
			TavernRecord e = readMemorizedTavern(list.getCompoundTagAt(i));
			if(!memory.has(e.pos()))
				memory.addOrUpdate(e);
		}
	}

	public static TavernRecord readMemorizedTavern(NBTTagCompound tag){
		TavernPos pos = new TavernPos(tag.getInteger("dim"),
				new BlockPos(tag.getInteger("posx"), tag.getInteger("posy"), tag.getInteger("posz")));
		String name = tag.getString("name");
		@Nullable IBlockState skin = Tavern.readSkin(tag);
		RetroOwner owner = readOwner(tag.getCompoundTag("owner"));
		boolean missing = tag.getBoolean("missing");
		TavernType tavernType = TavernType.of(Byte.toUnsignedInt(tag.getByte("type")));
		return new TavernRecord(pos, name.isEmpty() ? null : name, owner.owner, tavernType, owner.access, missing, skin);
	}

	public static RetroOwner readOwner(NBTTagCompound tag){
		Owner owner = tag.hasUniqueId("owner") ?
				Owner.of(tag.getUniqueId("owner"), tag.getString("ownerName")) :
				Owner.NO_OWNER;
		int accessIndex = Byte.toUnsignedInt(tag.getByte("access"))%3;
		AccessModifier access;
		switch(accessIndex){
			case 1:
				access = AccessModifier.PROTECTED;
				break;
			case 2:
				access = AccessModifier.PRIVATE;
				break;
			default: // case 0:
				access = AccessModifier.PUBLIC;
		}
		return new RetroOwner(owner, access);
	}

	public static final class RetroOwner{
		public final Owner owner;
		public final AccessModifier access;

		public RetroOwner(Owner owner, AccessModifier access){
			this.owner = owner;
			this.access = access;
		}
	}
}
