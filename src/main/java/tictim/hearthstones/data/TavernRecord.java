package tictim.hearthstones.data;

import com.google.common.base.MoreObjects;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;
import tictim.hearthstones.logic.Tavern;
import tictim.hearthstones.utils.TavernType;

import javax.annotation.Nullable;
import java.util.Objects;

public final class TavernRecord implements INBTSerializable<CompoundNBT>, Comparable<TavernRecord>{
	private TavernPos pos;
	@Nullable private ITextComponent name;
	private Owner owner;
	private TavernType tavernType;

	private boolean missing;

	public TavernRecord(Tavern tavern){
		update(tavern);
	}
	public TavernRecord(CompoundNBT nbt){
		deserializeNBT(nbt);
	}
	public TavernRecord(ITextComponent name, TavernPos pos, @Nullable Owner owner, TavernType tavernType){
		this.name = Objects.requireNonNull(name);
		this.pos = Objects.requireNonNull(pos);
		this.owner = owner;
		this.tavernType = Objects.requireNonNull(tavernType);
	}

	public ResourceLocation getDimensionType(){
		return this.pos.dim;
	}
	public TavernPos getTavernPos(){
		return this.pos;
	}
	public BlockPos getPos(){
		return this.pos.pos;
	}
	@Nullable public ITextComponent getName(){
		return this.name;
	}
	public Owner getOwner(){
		return this.owner;
	}
	public boolean isMissing(){
		return this.missing;
	}
	public void setMissing(boolean newValue){
		this.missing = newValue;
	}
	public TavernType getTavernType(){
		return this.tavernType;
	}
	public void setTavernType(TavernType type){
		this.tavernType = type;
	}

	public void update(Tavern tavern){
		this.pos = tavern.tavernPos();
		this.name = tavern.hasCustomName() ? tavern.getName() : null;
		this.owner = tavern.owner();
		this.tavernType = tavern.tavernType();
	}

	public boolean isInSameDimension(Entity entity){
		return entity.world.getDimensionKey().getLocation().equals(pos.dim);
	}

	@Override
	public CompoundNBT serializeNBT(){
		CompoundNBT nbt = new CompoundNBT();
		nbt.put("pos", pos.serialize());
		if(name!=null) nbt.putString("name", ITextComponent.Serializer.toJson(name));
		CompoundNBT subnbt = owner.serializeNBT();
		if(!subnbt.isEmpty()) nbt.put("owner", subnbt);
		if(missing) nbt.putBoolean("missing", true);
		if(tavernType!=TavernType.NORMAL) nbt.putByte("type", tavernType.id);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt){
		this.pos = new TavernPos(nbt.getCompound("pos"));
		if(nbt.contains("name", NBT.TAG_STRING)) this.name = ITextComponent.Serializer.getComponentFromJson(nbt.getString("name"));
		this.owner = new Owner(nbt.getCompound("owner"));
		this.missing = nbt.getBoolean("missing");
		this.tavernType = TavernType.of(nbt.getByte("type"));
	}

	@Override
	public boolean equals(Object o){
		if(this==o) return true;
		if(!(o instanceof TavernRecord)) return false;
		TavernRecord that = (TavernRecord)o;
		return missing==that.missing&&
				pos.equals(that.pos)&&
				Objects.equals(name, that.name)&&
				owner.equals(that.owner)&&
				tavernType==that.tavernType;
	}

	@Override
	public int hashCode(){
		return Objects.hash(pos, name, owner, tavernType, missing);
	}

	@Override
	public String toString(){
		return MoreObjects.toStringHelper(this)
				.add("pos", pos)
				.add("name", (name==null ? "Nameless Tavern" : name.getString()))
				.add("owner", owner)
				.add("tavernType", tavernType)
				.add("missing", missing)
				.toString();
	}

	@Override
	public int compareTo(TavernRecord o){
		int i;
		// missing
		i = Boolean.compare(isMissing(), o.isMissing());
		if(i!=0) return i;
		// dimension
		i = getDimensionType().compareTo(o.getDimensionType());
		if(i!=0) return i;
		// name
		if((getName()==null)!=(o.getName()==null)) return getName()==null ? -1 : 1;
		else if(getName()!=null){
			i = getName().getUnformattedComponentText().compareTo(o.getName().getUnformattedComponentText());
			if(i!=0) return i;
		}
		// owner
		i = getOwner().compareTo(o.getOwner());
		if(i!=0) return i;
		// static position
		return getPos().compareTo(o.getPos());
	}
}
