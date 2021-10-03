package tictim.hearthstones.block;

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
import net.minecraft.world.level.block.entity.BlockEntity;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.data.Owner;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.data.TavernPos;
import tictim.hearthstones.logic.Tavern;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.OpenTavernScreen;
import tictim.hearthstones.blockentity.TavernBlockEntity;
import tictim.hearthstones.utils.AccessModifier;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class BaseTavernBlock extends Block implements EntityBlock{
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

	public BaseTavernBlock(){
		super(Properties.of(Material.WOOD).strength(2.5f, 6000000).sound(SoundType.WOOD));
		this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_){
		return SHAPE;
	}

	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray){
		if(!world.isClientSide){
			world.sendBlockUpdated(pos, state, state, 2);
			Tavern te;
			BlockEntity _te = world.getBlockEntity(pos);
			if(_te instanceof Tavern){
				te = (Tavern)_te;
				if(te.owner().hasAccessPermission(player)){
					PlayerTavernMemory memory = PlayerTavernMemory.get(player);
					if(player.isShiftKeyDown()){
						memory.add(te);
						Owner o = te.owner();
						if(o.hasAccessPermission(player)&&player instanceof ServerPlayer){
							TavernPos tavernPos = te.tavernPos();
							OpenTavernScreen packet = new OpenTavernScreen(
									tavernPos,
									te.tavernType(),
									te.hasCustomName() ? te.getName() : null,
									te.owner().getAccessibility(player),
									te.owner(),
									tavernPos.equals(memory.getHomePos()));
							ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), packet);
						}
					}else{
						world.playSound(null, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.5f, 1);
						memory.add(te);
						memory.sync();
					}
				}else player.displayClientMessage(new TranslatableComponent("info.hearthstone.noPermission"), true);
			}else{
				Hearthstones.LOGGER.error("Tavern at {} is broken!", pos);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack){
		if(!world.isClientSide&&placer instanceof Player player){
			if(world.getBlockEntity(pos) instanceof Tavern tavern){
				if(stack.hasCustomHoverName()) tavern.setName(stack.getHoverName());
				if(!tavern.owner().hasOwner()){
					tavern.owner().setOwner(Player.createPlayerUUID(player.getGameProfile()), player.getGameProfile().getName());
					tavern.owner().setAccessModifier(AccessModifier.PROTECTED);
				}
				PlayerTavernMemory.get(player).add(tavern);
			}
		}
	}

	@Override @SuppressWarnings("deprecation") public PushReaction getPistonPushReaction(BlockState p_149656_1_){
		return PushReaction.BLOCK;
	}

	@Override
	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player){
		super.playerWillDestroy(world, pos, state, player);
		if(!world.isClientSide){
			BlockEntity te = world.getBlockEntity(pos);
			if(te instanceof TavernBlockEntity) PlayerTavernMemory.get(player).delete(new TavernPos(te));
		}
	}

	@Override
	public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player){
		ItemStack stack = super.getPickBlock(state, target, world, pos, player);
		if(world.getBlockEntity(pos) instanceof TavernBlockEntity tavern){
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

	@Override public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		addTipInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslatableComponent("info.hearthstones.tavern.help"));

		CompoundTag tag = stack.getTag();
		if(tag!=null&&tag.contains("BlockEntityTag", NBT.TAG_COMPOUND)){
			CompoundTag nbt = tag.getCompound("BlockEntityTag");
			if(nbt.contains("name", NBT.TAG_STRING)){
				tooltip.add(new TextComponent(" ").append(new TranslatableComponent("info.hearthstones.tavern.name", Component.Serializer.fromJson(nbt.getString("name")))));
			}
			if(nbt.contains("owner", NBT.TAG_COMPOUND)){
				CompoundTag nbtOwner = nbt.getCompound("owner");
				UUID uid = nbtOwner.hasUUID("owner") ? nbtOwner.getUUID("owner") : null;
				String ownerName = nbtOwner.getString("ownerName");

				tooltip.add(new TextComponent(" ").append(uid!=null ?
						new TranslatableComponent("info.hearthstones.tavern.owner", ownerName, uid) :
						new TranslatableComponent("info.hearthstones.tavern.owner.no_id", ownerName)));
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	protected void addTipInformation(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("info.hearthstones.tavern.tooltip"));
	}

	@Override @SuppressWarnings("deprecation") public BlockState rotate(BlockState state, Rotation rot){
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rot.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}
	@Override @SuppressWarnings("deprecation") public BlockState mirror(BlockState state, Mirror mirrorIn){
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, mirrorIn.mirror(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}

	@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}
}
