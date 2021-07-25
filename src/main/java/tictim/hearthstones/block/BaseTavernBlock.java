package tictim.hearthstones.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.network.PacketDistributor;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.data.Owner;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.data.TavernPos;
import tictim.hearthstones.logic.Tavern;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.OpenTavernScreen;
import tictim.hearthstones.tileentity.BaseTavernTileEntity;
import tictim.hearthstones.utils.AccessModifier;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class BaseTavernBlock extends Block{
	private static final VoxelShape SHAPE = VoxelShapes.or(box(5, 5, 3, 11, 11, 3),
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
	public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_){
		return SHAPE;
	}

	@Override
	@SuppressWarnings("deprecation")
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray){
		if(!world.isClientSide){
			world.sendBlockUpdated(pos, state, state, 2);
			Tavern te;
			TileEntity _te = world.getBlockEntity(pos);
			if(_te instanceof Tavern){
				te = (Tavern)_te;
				if(te.owner().hasAccessPermission(player)){
					PlayerTavernMemory memory = PlayerTavernMemory.get(player);
					if(player.isShiftKeyDown()){
						memory.add(te);
						Owner o = te.owner();
						if(o.hasAccessPermission(player)&&player instanceof ServerPlayerEntity){
							TavernPos tavernPos = te.tavernPos();
							OpenTavernScreen packet = new OpenTavernScreen(
									tavernPos,
									te.tavernType(),
									te.hasCustomName() ? te.getName() : null,
									te.owner().getAccessibility(player),
									te.owner(),
									tavernPos.equals(memory.getHomePos()));
							ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), packet);
						}
					}else{
						world.playSound(null, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, SoundEvents.PLAYER_LEVELUP, SoundCategory.BLOCKS, 0.5f, 1);
						memory.add(te);
						memory.sync();
					}
				}else player.displayClientMessage(new TranslationTextComponent("info.hearthstone.noPermission"), true);
			}else{
				Hearthstones.LOGGER.error("Tavern at {} is broken!", pos);
			}
		}
		return ActionResultType.SUCCESS;
	}

	@Override public boolean hasTileEntity(BlockState state){
		return true;
	}
	@Override public abstract BaseTavernTileEntity createTileEntity(BlockState state, IBlockReader world);

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack){
		if(!world.isClientSide&&placer instanceof PlayerEntity){
			PlayerEntity player = (PlayerEntity)placer;
			TileEntity _te = world.getBlockEntity(pos);
			if(_te instanceof Tavern){
				Tavern te = (Tavern)_te;
				if(stack.hasCustomHoverName()) te.setName(stack.getHoverName());
				if(!te.owner().hasOwner()){
					te.owner().setOwner(PlayerEntity.createPlayerUUID(player.getGameProfile()), player.getGameProfile().getName());
					te.owner().setAccessModifier(AccessModifier.PROTECTED);
				}
				PlayerTavernMemory.get(player).add(te);
			}
		}
	}

	@Override @SuppressWarnings("deprecation") public PushReaction getPistonPushReaction(BlockState p_149656_1_){
		return PushReaction.BLOCK;
	}

	@Override
	public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player){
		super.playerWillDestroy(world, pos, state, player);
		if(!world.isClientSide){
			TileEntity te = world.getBlockEntity(pos);
			if(te instanceof BaseTavernTileEntity) PlayerTavernMemory.get(player).delete(new TavernPos(te));
		}
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player){
		ItemStack stack = super.getPickBlock(state, target, world, pos, player);
		TileEntity _te = world.getBlockEntity(pos);
		if(_te instanceof BaseTavernTileEntity){
			BaseTavernTileEntity te = (BaseTavernTileEntity)_te;
			CompoundNBT subnbt = te.writeNBTForStack();
			if(!subnbt.isEmpty()){
				CompoundNBT nbt = new CompoundNBT();
				nbt.put("BlockEntityTag", subnbt);
				stack.setTag(nbt);
			}
			if(te.hasCustomName()){
				stack.setHoverName(te.getName());
				te.getDisplayName();
			}
		}
		return stack;
	}

	@Override public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		addTipInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslationTextComponent("info.hearthstones.tavern.help"));

		CompoundNBT tag = stack.getTag();
		if(tag!=null&&tag.contains("BlockEntityTag", NBT.TAG_COMPOUND)){
			CompoundNBT nbt = tag.getCompound("BlockEntityTag");
			if(nbt.contains("name", NBT.TAG_STRING)){
				tooltip.add(new StringTextComponent(" ").append(new TranslationTextComponent("info.hearthstones.tavern.name", ITextComponent.Serializer.fromJson(nbt.getString("name")))));
			}
			if(nbt.contains("owner", NBT.TAG_COMPOUND)){
				CompoundNBT nbtOwner = nbt.getCompound("owner");
				UUID uid = nbtOwner.hasUUID("owner") ? nbtOwner.getUUID("owner") : null;
				String ownerName = nbtOwner.getString("ownerName");

				tooltip.add(new StringTextComponent(" ").append(uid!=null ?
						new TranslationTextComponent("info.hearthstones.tavern.owner", ownerName, uid) :
						new TranslationTextComponent("info.hearthstones.tavern.owner.no_id", ownerName)));
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	protected void addTipInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("info.hearthstones.tavern.tooltip"));
	}

	@Override @SuppressWarnings("deprecation") public BlockState rotate(BlockState state, Rotation rot){
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rot.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}
	@Override @SuppressWarnings("deprecation") public BlockState mirror(BlockState state, Mirror mirrorIn){
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, mirrorIn.mirror(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}

	@Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}
}
