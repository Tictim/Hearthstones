package tictim.hearthstones.contents.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.contents.block.TavernBlock;
import tictim.hearthstones.hearthstone.WarpContext;
import tictim.hearthstones.tavern.AccessModifier;
import tictim.hearthstones.tavern.Owner;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernPos;

import javax.annotation.Nullable;
import java.util.Objects;

import static net.minecraft.block.BlockHorizontal.FACING;

public abstract class TavernTile extends TileEntity implements Tavern, IWorldNameable{
	private Owner owner = Owner.NO_OWNER;
	@Nullable private String name;
	private AccessModifier access = AccessModifier.PUBLIC;

	private IBlockState skin;

	public boolean upgrade(IBlockState newState, boolean dropUpgradeItem){
		IBlockState state = world.getBlockState(this.pos);
		if(state.getPropertyKeys().contains(FACING)&&
				newState.getPropertyKeys().contains(FACING)){
			newState = newState.withProperty(FACING, state.getValue(FACING));
		}
		world.setBlockState(this.pos, newState);
		TileEntity te = world.getTileEntity(this.pos);
		if(te instanceof TavernTile){
			TavernTile tavernTile = (TavernTile)te;
			copyAttributes(tavernTile);
			world.notifyBlockUpdate(this.pos, state, state, 0);

			if(dropUpgradeItem){
				ItemStack s = getUpgradeItem();
				if(!s.isEmpty()) InventoryHelper.spawnItemStack(world, this.pos.getX()+0.5, this.pos.getY()+0.5, this.pos.getZ()+0.5, s);
			}
		}else Hearthstones.LOGGER.error("Updated tavern at {} from {} to {}, but failed to retrieve Tavern.", this.pos, state, newState);
		return true;
	}

	protected void copyAttributes(TavernTile another){
		another.setName(this.name());
		another.setOwner(this.owner());
		another.setAccess(this.access());
	}

	protected abstract ItemStack getUpgradeItem();

	@Override public TavernPos pos(){
		return new TavernPos(world, pos);
	}
	@Override public BlockPos blockPos(){
		return pos;
	}

	@Override public Owner owner(){
		return owner;
	}
	public void setOwner(Owner owner){
		if(this.owner==owner) return;
		this.owner = owner;
		onUpdate();
	}
	@Override public AccessModifier access(){
		return access;
	}
	public void setAccess(AccessModifier access){
		if(this.access==access) return;
		this.access = access;
		onUpdate();
	}

	@Nullable @Override public String name(){
		return name;
	}
	@Override public boolean isMissing(){
		return false;
	}

	@Override public String getName(){
		return name!=null ? name : "";
	}
	@Override public boolean hasCustomName(){
		return name!=null&&!name.isEmpty();
	}
	@Override public ITextComponent getDisplayName(){
		return new TextComponentString(getName());
	}

	public void setName(@Nullable String name){
		if(Objects.equals(this.name, name)) return;
		this.name = name;
		onUpdate();
	}

	public boolean canTeleportTo(WarpContext context){
		return hasAccessPermission(context.getPlayer());
	}

	private boolean dropDisabled;

	public boolean isDropDisabled(){
		return this.dropDisabled;
	}
	public void setDropDisabled(boolean dropDisabled){
		this.dropDisabled = dropDisabled;
	}

	@Nullable public IBlockState skin(){
		return skin;
	}
	public boolean hasSkin(){
		return skin!=null;
	}
	public void setSkin(@Nullable IBlockState skin){
		if(this.skin!=skin){
			this.skin = skin;
			this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
			onUpdate();
		}
	}

	protected void onUpdate(){}

	@Override public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock()!=newState.getBlock()||oldState.getValue(TavernBlock.TAVERN_TYPE)!=newState.getValue(TavernBlock.TAVERN_TYPE);
	}

	@Override public SPacketUpdateTileEntity getUpdatePacket(){
		return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
	}
	@Override public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt){
		IBlockState skin = this.skin;
		readFromNBT(pkt.getNbtCompound());
		if(skin!=this.skin&&world!=null) world.checkLight(pos);
	}
	@Override public NBTTagCompound getUpdateTag(){
		return writeToNBT(new NBTTagCompound());
	}
	@Override public void handleUpdateTag(NBTTagCompound tag){
		IBlockState skin = this.skin;
		readFromNBT(tag);
		if(skin!=this.skin&&world!=null) world.checkLight(pos);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		if(isRetroSave(tag)) readRetro(tag);
		else readNonRetro(tag);
	}

	private boolean isRetroSave(NBTTagCompound tag){
		if(tag.hasKey("owner", Constants.NBT.TAG_COMPOUND)){
			NBTTagCompound owner = tag.getCompoundTag("owner");
			return owner.hasKey("access");
		}else return false; // Doesn't matter
	}

	private void readRetro(NBTTagCompound tag){ // Read 1.0.0.x data
		this.name = tag.hasKey("name", Constants.NBT.TAG_STRING) ? tag.getString("name") : null;
		if(tag.hasKey("owner", Constants.NBT.TAG_COMPOUND)){
			NBTTagCompound owner = tag.getCompoundTag("owner");
			this.owner = owner.hasUniqueId("owner") ?
					Owner.of(owner.getUniqueId("owner"), owner.getString("ownerName")) :
					Owner.NO_OWNER;
			AccessModifier[] values = AccessModifier.values();
			this.access = values[Byte.toUnsignedInt(owner.getByte("access"))%values.length];
		}else{
			this.owner = Owner.NO_OWNER;
			this.access = AccessModifier.PUBLIC;
		}
		this.skin = Tavern.readSkin(tag);
	}

	private void readNonRetro(NBTTagCompound tag){
		this.name = tag.hasKey("name", Constants.NBT.TAG_STRING) ? tag.getString("name") : null;
		this.owner = Owner.read(tag.getCompoundTag("owner"));
		this.access = AccessModifier.of(tag.getByte("access"));
		this.skin = Tavern.readSkin(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		if(name!=null) tag.setString("name", this.name);
		if(owner.hasOwner()) tag.setTag("owner", this.owner.write());
		if(access.ordinal()!=0) tag.setByte("access", (byte)access.ordinal());
		if(skin!=null) Tavern.writeSkin(tag, this.skin);
		return tag;
	}

	public static IBlockState readItemSkin(ItemStack stack){
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt!=null&&nbt.hasKey("BlockEntityTag", Constants.NBT.TAG_COMPOUND)){
			NBTTagCompound blockEntityTag = nbt.getCompoundTag("BlockEntityTag");
			IBlockState skin = Tavern.readSkin(blockEntityTag);
			if(skin!=null) return skin;
		}
		return Blocks.LOG.getDefaultState();
	}

	@Override public boolean canRenderBreaking(){
		return true;
	}
}
