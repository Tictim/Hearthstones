package tictim.hearthstones.contents.item.hearthstone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import tictim.hearthstones.Caps;
import tictim.hearthstones.contents.ModBlocks;
import tictim.hearthstones.contents.block.TavernBlock;
import tictim.hearthstones.contents.blockentity.BinderLecternBlockEntity;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.OpenBinderScreenMsg;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernMemory;
import tictim.hearthstones.tavern.TavernRecord;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TavernWaypointBinderItem extends Item{
	public TavernWaypointBinderItem(Properties properties){
		super(properties);
	}

	@Override public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context){
		Level level = context.getLevel();
		if(level.isClientSide) return InteractionResult.PASS;
		Data data = data(stack);
		if(data==null) return InteractionResult.PASS;
		BlockPos pos = context.getClickedPos();
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(blockEntity instanceof Tavern tavern){
			boolean overwrite = data.memory.has(tavern.pos());
			Player player = context.getPlayer();
			if(overwrite||data.waypoints>0){
				data.memory.addOrUpdate(tavern);
				if(!overwrite) data.waypoints--;

				if(player!=null) player.displayClientMessage(new TranslatableComponent("info.hearthstones.binder.saved"), true);
			}else if(player!=null) player.displayClientMessage(new TranslatableComponent("info.hearthstones.binder.no_waypoint"), true);
		}else if(blockEntity instanceof BinderLecternBlockEntity binderLectern){
			TavernWaypointBinderItem.Data d2 = binderLectern.getData();
			if(d2!=null){
				boolean r1 = data.syncFrom(d2.memory), r2 = d2.syncFrom(data.memory);
				if(r1||r2){
					if(r2) binderLectern.setChanged();
					TavernBlock.playSyncSound(level, pos);
					if(context.getPlayer()!=null)
						context.getPlayer().displayClientMessage(new TranslatableComponent("info.hearthstones.binder.combined"), true);
				}
			}
			return InteractionResult.SUCCESS;
		}else{
			BlockState state = level.getBlockState(pos);
			if(state.getBlock()==Blocks.LECTERN){
				if(state.getValue(LecternBlock.HAS_BOOK)) return InteractionResult.PASS;
				level.setBlock(pos, ModBlocks.BINDER_LECTERN.get()
						.defaultBlockState()
						.setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(LecternBlock.FACING)), 0);
				if(level.getBlockEntity(pos) instanceof BinderLecternBlockEntity binderLectern)
					binderLectern.setBinder(context.getPlayer(), context.getItemInHand());
				level.playSound(null, pos, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1, 1);
				level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, pos);

				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	@Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand){
		ItemStack stack = player.getItemInHand(hand);
		if(!level.isClientSide){
			Data data = data(stack);
			if(data!=null){
				if(player.isSecondaryUseActive()){
					if(player instanceof ServerPlayer sp)
						ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
								new OpenBinderScreenMsg(hand==InteractionHand.OFF_HAND ?
										Inventory.SLOT_OFFHAND : player.getInventory().selected,
										data.memory, data.getWaypoints()));
				}else if(data.syncTo(TavernMemories.player(player))){
					TavernBlock.playSyncSound(level, player);
				}
			}
		}
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> text, TooltipFlag flag){
		Data data = data(stack);
		if(data!=null&&data.memory.taverns().size()>0)
			text.add(new TranslatableComponent("info.hearthstones.binder.tooltip.entries",
					data.memory.taverns().size()));
		text.add(new TranslatableComponent("info.hearthstones.binder.tooltip.0"));
		text.add(new TranslatableComponent("info.hearthstones.binder.tooltip.1"));
		text.add(new TranslatableComponent("info.hearthstones.binder.tooltip.2"));
	}

	@Nullable @Override public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt){
		return new Data();
	}

	@Nullable @Override public CompoundTag getShareTag(ItemStack stack){
		CompoundTag tag = stack.getTag();
		Data data = data(stack);
		if(data!=null&&data.getWaypoints()>0){
			if(tag==null) tag = data.serializeNBT();
			else tag.merge(data.serializeNBT());
		}
		return tag;
	}

	@Override public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt){
		super.readShareTag(stack, nbt);
		Data data = data(stack);
		if(data!=null&&nbt!=null)
			data.deserializeNBT(nbt);
	}

	@SuppressWarnings("ConstantConditions") @Nullable public static Data data(ItemStack stack){
		return stack.getCapability(Caps.BINDER_DATA).orElse(null);
	}

	public static final class Data implements ICapabilitySerializable<CompoundTag>{
		private int waypoints;
		public final TavernMemory memory = new TavernMemory();

		public Data(){}
		public Data(CompoundTag tag){
			deserializeNBT(tag);
		}

		public int getWaypoints(){
			return waypoints;
		}
		public void setWaypoints(int waypoints){
			this.waypoints = waypoints;
		}

		public boolean syncTo(TavernMemory m){
			return sync(this.memory, m, Integer.MAX_VALUE)>0;
		}

		public boolean syncFrom(TavernMemory m){
			int synced = sync(m, this.memory, waypoints);
			waypoints -= synced;
			return synced>0;
		}

		private static int sync(TavernMemory from, TavernMemory to, int maxEntry){
			if(maxEntry<=0) return 0;
			int entry = 0;
			for(TavernRecord t : from.taverns().values()){
				if(to.has(t.pos())) continue;
				to.addOrUpdate(t);
				if(++entry>=maxEntry) break;
			}
			return entry;
		}

		public void overwrite(Data data){
			this.waypoints = data.waypoints;
			this.memory.clear();
			for(TavernRecord r : data.memory.taverns().values())
				this.memory.addOrUpdate(r);
		}

		@Nullable private LazyOptional<Data> self;

		@Nonnull @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side){
			if(cap!=Caps.BINDER_DATA) return LazyOptional.empty();
			if(self==null) self = LazyOptional.of(() -> this);
			return self.cast();
		}
		@Override public CompoundTag serializeNBT(){
			CompoundTag tag = memory.write();
			if(waypoints>0)
				tag.putInt("Waypoints", waypoints);
			return tag;
		}
		@Override public void deserializeNBT(CompoundTag nbt){
			memory.read(nbt);
			this.waypoints = nbt.getInt("Waypoints");
		}
	}
}
