package tictim.hearthstones.contents.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tictim.hearthstones.contents.ModBlockEntities;
import tictim.hearthstones.contents.item.TavernBinderItem;
import tictim.hearthstones.tavern.TavernBinderData;

import javax.annotation.Nullable;
import java.util.UUID;

public class BinderLecternBlockEntity extends BlockEntity{
	@Nullable private UUID player;
	@Nullable private ItemStack item;
	@Nullable private TavernBinderData data;

	private int waypointsSync;
	private int emptyWaypointsSync;
	private boolean infiniteWaypointsSync;

	public BinderLecternBlockEntity(BlockPos pos, BlockState state){
		super(ModBlockEntities.BINDER_LECTERN.get(), pos, state);
	}

	@Nullable public UUID getPlayer(){
		return player;
	}
	@Nullable public ItemStack getItem(){
		return item;
	}
	@Nullable public TavernBinderData getData(){
		return data;
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

	public void setBinder(@Nullable Player player, @Nullable ItemStack binder){
		this.player = player!=null ? player.getUUID() : null;
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

	@Nullable @Override public Packet<ClientGamePacketListener> getUpdatePacket(){
		return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
	}
	@Override public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt){
		if(pkt.getTag()!=null) handleUpdateTag(pkt.getTag());
	}

	@Override public CompoundTag getUpdateTag(){
		CompoundTag tag = new CompoundTag();
		tag.putInt("Waypoints", waypointsSync);
		tag.putInt("EmptyWaypoints", emptyWaypointsSync);
		tag.putBoolean("InfiniteWaypoints", infiniteWaypointsSync);
		return tag;
	}
	@Override public void handleUpdateTag(CompoundTag tag){
		this.waypointsSync = tag.getInt("Waypoints");
		this.emptyWaypointsSync = tag.getInt("EmptyWaypoints");
		this.infiniteWaypointsSync = tag.getBoolean("InfiniteWaypoints");
	}

	@Override public void setChanged(){
		super.setChanged();
		if(updateSyncData()&&this.level!=null)
			this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
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

	@Override public void load(CompoundTag tag){
		super.load(tag);
		this.player = tag.hasUUID("player") ? tag.getUUID("player") : null;
		this.item = tag.contains("item", Tag.TAG_COMPOUND) ? ItemStack.of(tag.getCompound("item")) : null;
		this.data = readData(tag);
		updateSyncData();
	}

	@Nullable private static TavernBinderData readData(CompoundTag tag){
		if(!tag.contains("data", Tag.TAG_COMPOUND)) return null;
		CompoundTag dataTag = tag.getCompound("data");
		TavernBinderData data = new TavernBinderData(tag.getBoolean("infinite"));
		data.deserializeNBT(dataTag);
		return data;
	}

	@Override protected void saveAdditional(CompoundTag tag){
		if(player!=null) tag.putUUID("player", player);
		if(item!=null) tag.put("item", item.save(new CompoundTag()));
		if(data!=null){
			tag.put("data", data.serializeNBT());
			if(data.isInfiniteWaypoints()) tag.putBoolean("infinite", true);
		}
	}
}
