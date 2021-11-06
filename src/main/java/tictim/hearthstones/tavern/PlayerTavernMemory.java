package tictim.hearthstones.tavern;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nullable;

public class PlayerTavernMemory extends TavernMemory{
	private int cooldown;
	@Nullable private TavernPos selected;
	@Nullable private TavernPos homeTavern;

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
		this.selected = pos;
	}
	@Nullable public TavernPos getSelectedPos(){
		return this.selected;
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
		if(cooldown>0) nbt.putInt("cooldown", cooldown);
		if(selected!=null) nbt.put("selected", selected.write());
		if(homeTavern!=null) nbt.put("homeTavern", homeTavern.write());
		return nbt;
	}

	@Override
	public void read(CompoundTag nbt){
		super.read(nbt);
		this.cooldown = nbt.getInt("cooldown");
		this.selected = nbt.contains("selected", NBT.TAG_COMPOUND) ? new TavernPos(nbt.getCompound("selected")) : null;
		this.homeTavern = nbt.contains("homeTavern", NBT.TAG_COMPOUND) ? new TavernPos(nbt.getCompound("homeTavern")) : null;
	}

	@Override public void write(FriendlyByteBuf buf){
		super.write(buf);
		buf.writeBoolean(selected!=null);
		if(selected!=null) selected.write(buf);
		buf.writeBoolean(homeTavern!=null);
		if(homeTavern!=null) homeTavern.write(buf);
	}

	@Override public void read(FriendlyByteBuf buf){
		super.read(buf);
		this.selected = buf.readBoolean() ? TavernPos.read(buf) : null;
		this.homeTavern = buf.readBoolean() ? TavernPos.read(buf) : null;
	}
}
