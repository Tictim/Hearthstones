package tictim.hearthstones.tavern;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.Constants.NBT;

import javax.annotation.Nullable;

public record TavernRecord(@Override TavernPos pos,
                           @Override @Nullable Component name,
                           @Override Owner owner,
                           @Override TavernType type,
                           @Override AccessModifier access,
                           @Override boolean isMissing) implements Tavern{
	public TavernRecord(Tavern tavern){
		this(tavern.pos(), tavern.name(), tavern.owner(), tavern.type(), tavern.access(), tavern.isMissing());
	}
	public TavernRecord(Tavern tavern, boolean missing){
		this(tavern.pos(), tavern.name(), tavern.owner(), tavern.type(), tavern.access(), missing);
	}
	public TavernRecord(CompoundTag nbt){
		this(new TavernPos(nbt.getCompound("pos")),
				nbt.contains("name", NBT.TAG_STRING) ? Component.Serializer.fromJson(nbt.getString("name")) : null,
				Owner.read(nbt.getCompound("owner")),
				TavernType.of(nbt.getByte("type")),
				AccessModifier.of(nbt.getByte("access")),
				nbt.getBoolean("missing"));
	}

	@Override public BlockPos blockPos(){
		return pos.pos;
	}

	@Override public TavernRecord toRecord(){
		return this;
	}

	public CompoundTag write(){
		CompoundTag nbt = new CompoundTag();
		nbt.put("pos", pos.write());
		if(type!=TavernType.NORMAL) nbt.putByte("type", type.id);
		if(owner.hasOwner()) nbt.put("owner", owner.write());
		if(access.ordinal()!=0) nbt.putByte("access", (byte)access.ordinal());
		if(name!=null) nbt.putString("name", Component.Serializer.toJson(name));
		if(isMissing) nbt.putBoolean("missing", true);
		return nbt;
	}
}
