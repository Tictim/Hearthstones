package tictim.hearthstones.tavern;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;

public record TavernRecord(@Override TavernPos pos,
                           @Override @Nullable String name,
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
				nbt.contains("name", Tag.TAG_STRING) ? nbt.getString("name") : null,
				Owner.read(nbt.getCompound("owner")),
				TavernType.of(nbt.getByte("type")),
				AccessModifier.of(nbt.getByte("access")),
				nbt.getBoolean("missing"));
	}

	@Override public BlockPos blockPos(){
		return pos.pos();
	}

	@Override public TavernRecord toRecord(){
		return this;
	}

	public CompoundTag write(){
		return write(this);
	}

	public void write(FriendlyByteBuf buf){
		write(this, buf);
	}

	public static TavernRecord read(FriendlyByteBuf buf){
		return new TavernRecord(
				TavernPos.read(buf),
				buf.readBoolean() ? buf.readUtf() : null,
				Owner.read(buf),
				TavernType.of(buf.readByte()),
				AccessModifier.of(buf.readUnsignedByte()),
				buf.readBoolean());
	}

	public static CompoundTag write(Tavern tavern){
		CompoundTag nbt = new CompoundTag();

		String name = tavern.name();
		Owner owner = tavern.owner();
		TavernType type = tavern.type();
		AccessModifier access = tavern.access();

		nbt.put("pos", tavern.pos().write());
		if(name!=null) nbt.putString("name", name);
		if(owner.hasOwner()) nbt.put("owner", owner.write());
		if(type!=TavernType.NORMAL) nbt.putByte("type", type.id);
		if(access.ordinal()!=0) nbt.putByte("access", (byte)access.ordinal());
		if(tavern.isMissing()) nbt.putBoolean("missing", true);
		return nbt;
	}

	public static void write(Tavern tavern, FriendlyByteBuf buf){
		tavern.pos().write(buf);
		String name = tavern.name();
		buf.writeBoolean(name!=null);
		if(name!=null) buf.writeUtf(name);
		tavern.owner().write(buf);
		buf.writeVarInt(tavern.type().id);
		buf.writeByte(tavern.access().ordinal());
		buf.writeBoolean(tavern.isMissing());
	}
}
