package tictim.hearthstones.contents.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.hearthstone.WarpContext;
import tictim.hearthstones.tavern.AccessModifier;
import tictim.hearthstones.tavern.Owner;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernPos;

import javax.annotation.Nullable;
import java.util.Objects;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public abstract class TavernBlockEntity extends BlockEntity implements Tavern, Nameable{
	private Owner owner = Owner.NO_OWNER;
	@Nullable private String name;
	private AccessModifier access = AccessModifier.PUBLIC;

	public TavernBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state){
		super(type, pos, state);
	}

	public boolean upgrade(BlockState newState, boolean dropUpgradeItem){
		if(level==null) return false;
		BlockState state = getBlockState();
		BlockPos pos = getBlockPos();
		if(state.hasProperty(HORIZONTAL_FACING)&&newState.hasProperty(HORIZONTAL_FACING)){
			newState = newState.setValue(HORIZONTAL_FACING, state.getValue(HORIZONTAL_FACING));
		}
		level.setBlockAndUpdate(pos, newState);
		if(level.getBlockEntity(pos) instanceof TavernBlockEntity another){
			copyAttributes(another);
			level.sendBlockUpdated(pos, state, state, 0);

			if(dropUpgradeItem){
				ItemStack s = getUpgradeItem();
				if(!s.isEmpty()) Containers.dropItemStack(level, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, s);
			}
		}else Hearthstones.LOGGER.error("Updated tavern at {} from {} to {}, but failed to retrieve Tavern.", pos, state, newState);
		return true;
	}

	protected void copyAttributes(TavernBlockEntity another){
		another.setName(this.name());
		another.setOwner(this.owner());
		another.setAccess(this.access());
	}

	protected abstract ItemStack getUpgradeItem();

	@Override public TavernPos pos(){
		return new TavernPos(Objects.requireNonNull(getLevel()), getBlockPos());
	}
	@Override public BlockPos blockPos(){
		return getBlockPos();
	}

	@Override public Owner owner(){
		return this.owner;
	}
	public void setOwner(Owner owner){
		if(this.owner==owner) return;
		this.owner = owner;
		setChanged();
	}
	@Override public AccessModifier access(){
		return access;
	}
	public void setAccess(AccessModifier access){
		if(this.access==access) return;
		this.access = access;
		setChanged();
	}

	@Nullable @Override public String name(){
		return name;
	}
	@Override public boolean isMissing(){
		return false;
	}

	@Override public Component getName(){
		return Component.literal(name!=null ? name : "");
	}
	@Nullable @Override public Component getCustomName(){
		return name!=null ? Component.literal(name) : null;
	}
	public void setName(@Nullable String name){
		if(Objects.equals(this.name, name)) return;
		this.name = name;
		setChanged();
	}

	public boolean canTeleportTo(WarpContext context){
		return hasAccessPermission(context.getPlayer());
	}

	@Override public ClientboundBlockEntityDataPacket getUpdatePacket(){
		return ClientboundBlockEntityDataPacket.create(this);
	}
	@Override public CompoundTag getUpdateTag(){
		return saveWithoutMetadata();
	}

	@Override public void load(CompoundTag tag){
		super.load(tag);
		this.name = tag.contains("name", Tag.TAG_STRING) ? tag.getString("name") : null;
		this.owner = Owner.read(tag.getCompound("owner"));
		this.access = AccessModifier.of(tag.getByte("access"));
	}

	@Override protected void saveAdditional(CompoundTag tag){
		if(name!=null) tag.putString("name", this.name);
		if(owner.hasOwner()) tag.put("owner", this.owner.write());
		if(access.ordinal()!=0) tag.putByte("access", (byte)access.ordinal());
	}

	@Override public String toString(){
		return "TavernBlockEntity{"+
				"owner="+owner+
				", name="+name+
				", access="+access+
				'}';
	}
}
