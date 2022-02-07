package tictim.hearthstones.contents.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.PacketDistributor;
import tictim.hearthstones.contents.blockentity.BinderLecternBlockEntity;
import tictim.hearthstones.contents.item.TavernWaypointBinderItem;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.OpenLecternBinderScreenMsg;
import tictim.hearthstones.tavern.TavernMemories;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class BinderLecternBlock extends Block implements EntityBlock{
	public BinderLecternBlock(Properties properties){
		super(properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(HORIZONTAL_FACING, Direction.NORTH));
	}

	@SuppressWarnings("deprecation")
	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos){
		return LecternBlock.SHAPE_COMMON;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean useShapeForLightOcclusion(BlockState state){
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx){
		return LecternBlock.SHAPE_COLLISION;
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx){
		return switch(state.getValue(HORIZONTAL_FACING)){
			case NORTH -> LecternBlock.SHAPE_NORTH;
			case SOUTH -> LecternBlock.SHAPE_SOUTH;
			case EAST -> LecternBlock.SHAPE_EAST;
			case WEST -> LecternBlock.SHAPE_WEST;
			default -> LecternBlock.SHAPE_COMMON;
		};
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState rotate(BlockState s, Rotation r){
		return s.setValue(HORIZONTAL_FACING, r.rotate(s.getValue(HORIZONTAL_FACING)));
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState s, Mirror m){
		return rotate(s, m.getRotation(s.getValue(HORIZONTAL_FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b){
		b.add(HORIZONTAL_FACING);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new BinderLecternBlockEntity(pos, state);
	}

	@SuppressWarnings("deprecation") @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
		if(!level.isClientSide&&level.getBlockEntity(pos) instanceof BinderLecternBlockEntity binderLectern){
			TavernWaypointBinderItem.Data data = binderLectern.getData();
			if(data!=null){
				if(player.isSecondaryUseActive()){
					if(player instanceof ServerPlayer sp)
						ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
								new OpenLecternBinderScreenMsg(pos, data.memory, data.getWaypoints()));
				}else if(data.syncTo(TavernMemories.player(player)))
					TavernBlock.playSyncSound(level, player);
			}
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving){
		if(!state.is(newState.getBlock())){
			if(level.getBlockEntity(pos) instanceof BinderLecternBlockEntity binderLectern){
				if(binderLectern.getItem()!=null){
					ItemStack copy = binderLectern.getItem().copy();
					if(binderLectern.getData()!=null){
						TavernWaypointBinderItem.Data data = TavernWaypointBinderItem.data(copy);
						if(data!=null) data.overwrite(binderLectern.getData());
					}
					Direction direction = state.getValue(HORIZONTAL_FACING);
					float f = .25f*(float)direction.getStepX();
					float f1 = .25f*(float)direction.getStepZ();
					ItemEntity itemEntity = new ItemEntity(level, pos.getX()+.5+f, pos.getY()+1, pos.getZ()+.5+f1, copy);
					itemEntity.setDefaultPickUpDelay();
					level.addFreshEntity(itemEntity);
					binderLectern.setBinder(null, null);
				}
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override public Item asItem(){
		return Items.LECTERN;
	}
}
