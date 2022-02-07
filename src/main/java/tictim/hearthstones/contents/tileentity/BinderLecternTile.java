package tictim.hearthstones.contents.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import tictim.hearthstones.contents.item.TavernBinderItem;
import tictim.hearthstones.tavern.TavernBinderData;

import javax.annotation.Nullable;
import java.util.UUID;

public class BinderLecternTile extends TileEntity{
	@Nullable private UUID player;
	@Nullable private ItemStack item;
	@Nullable private TavernBinderData data;

	private int waypointsSync;
	private int emptyWaypointsSync;
	private boolean infiniteWaypointsSync;

	@Nullable public UUID getPlayer(){
		return player;
	}
	@Nullable public ItemStack getItem(){
		return item;
	}
	@Nullable public TavernBinderData getData(){
		return data;
	}

	public boolean hasBinder(){
		return item!=null;
	}

	/**
	 * For client side only.
	 */
	public int getWaypointsSync(){
		return waypointsSync;
	}
	/**
	 * For client side only.
	 */
	public int getEmptyWaypointsSync(){
		return emptyWaypointsSync;
	}
	/**
	 * For client side only.
	 */
	public boolean isInfiniteWaypointsSync(){
		return infiniteWaypointsSync;
	}

	public void setBinder(@Nullable EntityPlayer player, @Nullable ItemStack binder){
		this.player = player!=null ? player.getUniqueID() : null;
		if(binder==null){
			this.item = null;
			this.data = null;
		}else{
			this.item = binder.copy();
			TavernBinderData d2 = TavernBinderItem.data(binder);
			if(d2!=null){
				this.data = new TavernBinderData(d2.isInfiniteWaypoints());
				this.data.overwrite(d2);
			}
		}
		this.setChanged();
	}

	public void setChanged(){
		if(updateSyncData()&&this.world!=null){
			IBlockState state = this.world.getBlockState(this.pos);
			this.world.notifyBlockUpdate(this.pos, state, state, 0);
		}
	}

	private boolean updateSyncData(){
		int waypoints = data!=null ? data.getWaypoints() : 0;
		int emptyWaypoints = data!=null ? data.getEmptyWaypoints() : 0;
		boolean infiniteWaypoints = data!=null&&data.isInfiniteWaypoints();

		if(waypointsSync==waypoints&&emptyWaypointsSync==emptyWaypoints&&infiniteWaypointsSync==infiniteWaypoints)
			return false;
		this.waypointsSync = waypoints;
		this.emptyWaypointsSync = emptyWaypoints;
		this.infiniteWaypointsSync = infiniteWaypoints;
		return true;
	}

	@Override public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
		this.player = tag.hasUniqueId("player") ? tag.getUniqueId("player") : null;
		this.item = tag.hasKey("item", Constants.NBT.TAG_COMPOUND) ? new ItemStack(tag.getCompoundTag("item")) : null;
		this.data = readData(tag);
	}

	@Nullable private static TavernBinderData readData(NBTTagCompound tag){
		if(!tag.hasKey("data", Constants.NBT.TAG_COMPOUND)) return null;
		NBTTagCompound dataTag = tag.getCompoundTag("data");
		TavernBinderData data = new TavernBinderData(tag.getBoolean("infinite"));
		data.deserializeNBT(dataTag);
		return data;
	}

	@Override public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		if(player!=null) tag.setUniqueId("player", player);
		if(item!=null) tag.setTag("item", item.serializeNBT());
		if(data!=null){
			tag.setTag("data", data.serializeNBT());
			if(data.isInfiniteWaypoints()) tag.setBoolean("infinite", true);
		}
		return tag;
	}

	@Override public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate){
		return oldState.getBlock()!=newSate.getBlock();
	}
}