package tictim.hearthstones.contents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.contents.blockentity.TavernBlockEntity;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.OpenTavernScreenMsg;
import tictim.hearthstones.tavern.AccessModifier;
import tictim.hearthstones.tavern.Owner;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernPos;

import javax.annotation.Nullable;
import java.util.List;

public abstract class TavernBlock extends Block implements EntityBlock{
	private static final VoxelShape SHAPE = Shapes.or(box(5, 5, 3, 11, 11, 3),
			box(0, 13, 0, 16, 16, 16),
			box(2, 1, 2, 14, 13, 14),
			box(13, 0, 0, 16, 13, 3),
			box(0, 0, 0, 3, 13, 3),
			box(13, 0, 13, 16, 13, 16),
			box(0, 0, 13, 3, 13, 16),
			box(0, 12, 11, 2, 13, 13),
			box(11, 12, 14, 13, 13, 16),
			box(14, 12, 3, 16, 13, 5),
			box(3, 12, 0, 5, 13, 2),
			box(0, 12, 3, 2, 13, 5),
			box(3, 12, 14, 5, 13, 16),
			box(14, 12, 11, 16, 13, 13),
			box(11, 12, 0, 13, 13, 2),
			box(0, 11, 12, 2, 12, 13),
			box(0, 11, 3, 2, 12, 4),
			box(3, 11, 0, 4, 12, 2),
			box(14, 11, 3, 16, 12, 4),
			box(12, 11, 0, 13, 12, 2),
			box(14, 11, 12, 16, 12, 13),
			box(12, 11, 14, 13, 12, 16),
			box(3, 11, 14, 4, 12, 16)).optimize();

	public TavernBlock(){
		super(Properties.of(Material.WOOD).strength(2.5f, 6000000).sound(SoundType.WOOD));
		this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
	}

	@SuppressWarnings("deprecation") @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext collisionContext){
		return SHAPE;
	}

	@SuppressWarnings("deprecation") @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray){
		if(level.isClientSide) return InteractionResult.SUCCESS;
		level.sendBlockUpdated(pos, state, state, 0);
		if(!(level.getBlockEntity(pos) instanceof Tavern tavern)){
			Hearthstones.LOGGER.error("Tavern at {} is broken!", pos);
			return InteractionResult.SUCCESS;
		}
		if(tavern.hasAccessPermission(player)){
			PlayerTavernMemory memory = TavernMemories.player(player);
			memory.addOrUpdate(tavern);
			if(!player.isShiftKeyDown())
				level.playSound(null, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.5f, 1);
			else if(tavern.hasAccessPermission(player)&&player instanceof ServerPlayer sp)
				ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new OpenTavernScreenMsg(tavern, player, tavern.pos().equals(memory.getHomePos())));
		}else player.displayClientMessage(new TranslatableComponent("info.hearthstone.noPermission"), true);
		return InteractionResult.SUCCESS;
	}

	@Override public BlockState getStateForPlacement(BlockPlaceContext context){
		return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack){
		if(level.isClientSide||
				!(placer instanceof Player player)||
				!(level.getBlockEntity(pos) instanceof TavernBlockEntity tavern)) return;
		if(stack.hasCustomHoverName()) tavern.setName(stack.getHoverName());
		if(!tavern.owner().hasOwner()){
			tavern.setOwner(Owner.of(player));
			tavern.setAccess(AccessModifier.PROTECTED);
		}
		TavernMemories.player(player).addOrUpdate(tavern);
	}

	@SuppressWarnings("deprecation") @Override public PushReaction getPistonPushReaction(BlockState state){
		return PushReaction.BLOCK;
	}

	@Override public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player){
		super.playerWillDestroy(level, pos, state, player);
		if(!level.isClientSide&&level.getBlockEntity(pos) instanceof TavernBlockEntity tavern)
			TavernMemories.player(player).delete(new TavernPos(tavern));
	}

	@Override public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player){
		ItemStack stack = super.getPickBlock(state, target, level, pos, player);
		if(level.getBlockEntity(pos) instanceof TavernBlockEntity tavern){
			CompoundTag subnbt = tavern.writeNBTForStack();
			if(!subnbt.isEmpty()){
				CompoundTag nbt = new CompoundTag();
				nbt.put("BlockEntityTag", subnbt);
				stack.setTag(nbt);
			}
			if(tavern.hasCustomName()){
				stack.setHoverName(tavern.getName());
				tavern.getDisplayName();
			}
		}
		return stack;
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flagIn){
		addTipInformation(stack, level, tooltip, flagIn);
		tooltip.add(new TranslatableComponent("info.hearthstones.tavern.help"));

		CompoundTag tag = stack.getTag();
		if(tag==null||!tag.contains("BlockEntityTag", NBT.TAG_COMPOUND)) return;
		CompoundTag nbt = tag.getCompound("BlockEntityTag");
		if(nbt.contains("name", NBT.TAG_STRING))
			tooltip.add(new TextComponent(" ").append(new TranslatableComponent("info.hearthstones.tavern.name", Component.Serializer.fromJson(nbt.getString("name")))));
		if(nbt.contains("owner", NBT.TAG_COMPOUND)){
			Owner owner = Owner.read(nbt.getCompound("owner"));
			tooltip.add(new TextComponent(" ").append(new TranslatableComponent("info.hearthstones.tavern.owner", owner.getName(), owner.getId())));
		}
	}

	protected void addTipInformation(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(new TranslatableComponent("info.hearthstones.tavern.tooltip"));
	}

	@SuppressWarnings("deprecation") @Override public BlockState rotate(BlockState state, Rotation rotation){
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}
	@SuppressWarnings("deprecation") @Override public BlockState mirror(BlockState state, Mirror mirror){
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, mirror.mirror(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}

	@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}
}
