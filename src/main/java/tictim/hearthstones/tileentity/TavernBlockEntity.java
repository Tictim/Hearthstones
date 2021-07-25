package tictim.hearthstones.tileentity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Constants.NBT;
import tictim.hearthstones.data.Owner;
import tictim.hearthstones.logic.Tavern;
import tictim.hearthstones.utils.TavernType;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class TavernBlockEntity extends BlockEntity implements Tavern{
	private final Owner owner = new Owner();
	private Component name;

	protected TavernBlockEntity(BlockEntityType<?> type, BlockPos pos ,BlockState state){
		super(type, pos, state);
	}

	@Override public Level world(){
		return Objects.requireNonNull(getLevel());
	}
	@Override public BlockPos pos(){
		return getBlockPos();
	}

	@Override public Component getName(){
		return hasCustomName() ? name : new TextComponent("");
	}
	@Nullable @Override public Component getCustomName(){
		return name;
	}
	public void setName(@Nullable Component name){
		this.name = name;
	}

	public Owner owner(){
		return this.owner;
	}
	public TavernType tavernType(){
		return TavernType.NORMAL;
	}

	@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){
		return new ClientboundBlockEntityDataPacket(this.worldPosition, 0, this.getUpdateTag());
	}
	@Override public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt){
		read(pkt.getTag());
	}
	@Override public CompoundTag getUpdateTag(){
		return save(new CompoundTag());
	}

	@Override public void load(CompoundTag tag){
		super.load(tag);
		read(tag);
	}

	private void read(CompoundTag nbt){
		this.name = nbt.contains("name", NBT.TAG_STRING) ? Component.Serializer.fromJson(nbt.getString("name")) : null;
		if(nbt.contains("owner", NBT.TAG_COMPOUND)) this.owner.deserializeNBT(nbt.getCompound("owner"));
		else this.owner.reset();
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		nbt = super.save(nbt);
		if(this.name!=null) nbt.putString("name", Component.Serializer.toJson(this.name));
		if(owner.hasOwner()) nbt.put("owner", this.owner.serializeNBT());
		return nbt;
	}

	public CompoundTag writeNBTForStack(){
		CompoundTag nbt = new CompoundTag();
		if(owner.hasOwner()) nbt.put("owner", this.owner.serializeNBT());
		return nbt;
	}

	@Override public String toString(){
		return "BaseTavernTileEntity{"+
				"owner="+owner+
				", name="+(name!=null ? name : null)+
				'}';
	}
}
