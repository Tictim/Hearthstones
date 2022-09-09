package tictim.hearthstones.contents.item;

import net.minecraft.core.BlockPos;
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
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.network.PacketDistributor;
import tictim.hearthstones.Caps;
import tictim.hearthstones.contents.ModBlocks;
import tictim.hearthstones.contents.block.TavernBlock;
import tictim.hearthstones.contents.blockentity.BinderLecternBlockEntity;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.OpenBinderScreenMsg;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernBinderData;
import tictim.hearthstones.tavern.TavernMemories;

import javax.annotation.Nullable;
import java.util.List;

public class TavernBinderItem extends Item{
	private final boolean infiniteWaypoints;

	public TavernBinderItem(boolean infiniteWaypoints, Properties properties){
		super(properties);
		this.infiniteWaypoints = infiniteWaypoints;
	}

	@Override public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context){
		Level level = context.getLevel();
		if(level.isClientSide) return InteractionResult.PASS;
		TavernBinderData data = data(stack);
		if(data==null) return InteractionResult.PASS;
		BlockPos pos = context.getClickedPos();
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(blockEntity instanceof Tavern tavern){
			boolean result = data.addOrUpdateWaypoint(tavern);
			Player player = context.getPlayer();
			if(player!=null)
				player.displayClientMessage(new TranslatableComponent(result ?
						"info.hearthstones.binder.saved" :
						"info.hearthstones.binder.no_waypoint"), true);
		}else if(blockEntity instanceof BinderLecternBlockEntity binderLectern){
			TavernBinderData d2 = binderLectern.getData();
			if(d2!=null){
				boolean r1 = data.syncFrom(d2.memory), r2 = d2.syncFrom(data.memory);
				if(r1||r2){
					if(r2) binderLectern.setChanged();
					if(context.getPlayer()!=null)
						context.getPlayer().displayClientMessage(new TranslatableComponent("info.hearthstones.binder.combined"), true);
				}
				TavernBlock.playSyncSound(level, pos);
			}
			return InteractionResult.SUCCESS;
		}else{
			BlockState state = level.getBlockState(pos);
			if(state.getBlock()==Blocks.LECTERN){
				if(state.getValue(LecternBlock.HAS_BOOK)) return InteractionResult.PASS;
				level.setBlock(pos, ModBlocks.BINDER_LECTERN.get()
						.defaultBlockState()
						.setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(LecternBlock.FACING)), 3);
				if(level.getBlockEntity(pos) instanceof BinderLecternBlockEntity binderLectern){
					ItemStack itemInHand = context.getItemInHand();
					binderLectern.setBinder(context.getPlayer(), itemInHand);
					itemInHand.shrink(itemInHand.getCount());
				}
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
			TavernBinderData data = data(stack);
			if(data!=null){
				if(player.isSecondaryUseActive()){
					if(player instanceof ServerPlayer sp)
						ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
								new OpenBinderScreenMsg(hand==InteractionHand.OFF_HAND ?
										Inventory.SLOT_OFFHAND : player.getInventory().selected,
										data.memory, data.getEmptyWaypoints(), data.isInfiniteWaypoints()));
				}else{
					data.syncTo(TavernMemories.player(player));
					TavernBlock.playSyncSound(level, player);
				}
			}
		}
		return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> text, TooltipFlag flag){
		TavernBinderData data = data(stack);
		if(data!=null&&(data.getWaypoints()>0||data.getEmptyWaypoints()>0)){
			text.add(infiniteWaypoints ?
					new TranslatableComponent("info.hearthstones.binder.tooltip.waypoints.infinite",
							data.getWaypoints()) :
					new TranslatableComponent("info.hearthstones.binder.tooltip.waypoints",
							data.getWaypoints(), data.getEmptyWaypoints()));
		}
		text.add(new TranslatableComponent("info.hearthstones.binder.tooltip"));
		if(infiniteWaypoints)
			text.add(new TranslatableComponent("info.hearthstones.binder.tooltip.infinite"));
	}

	@Override public boolean isFoil(ItemStack stack){
		return infiniteWaypoints||super.isFoil(stack);
	}

	@Nullable @Override public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt){
		return new TavernBinderData(infiniteWaypoints);
	}

	@Nullable @Override public CompoundTag getShareTag(ItemStack stack){
		CompoundTag tag = stack.getTag();
		TavernBinderData data = data(stack);
		if(data==null) return tag;
		if(tag==null) return data.serializeNBT();
		return tag.copy().merge(data.serializeNBT());
	}

	@Override public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt){
		super.readShareTag(stack, nbt);
		TavernBinderData data = data(stack);
		if(data!=null&&nbt!=null)
			data.deserializeNBT(nbt);
	}

	@SuppressWarnings("ConstantConditions") @Nullable public static TavernBinderData data(ItemStack stack){
		return stack.getCapability(Caps.BINDER_DATA).orElse(null);
	}
}
