package tictim.hearthstones.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tictim.hearthstones.contents.ModBlocks;
import tictim.hearthstones.contents.ModItems;

import java.util.Random;

@SuppressWarnings("deprecation")
public class AmethystClusterBlock extends AmethystBlock{
	protected final AxisAlignedBB northAabb;
	protected final AxisAlignedBB southAabb;
	protected final AxisAlignedBB eastAabb;
	protected final AxisAlignedBB westAabb;
	protected final AxisAlignedBB upAabb;
	protected final AxisAlignedBB downAabb;
	private final boolean dropsAmethyst;

	public AmethystClusterBlock(SoundType soundType, boolean dropsAmethyst, double y, double xz){
		super(ModBlocks.ModMaterials.AMETHYST_CRYSTAL);
		this.dropsAmethyst = dropsAmethyst;
		this.upAabb = new AxisAlignedBB(xz, 0, xz, 1-xz, y, 1-xz);
		this.downAabb = new AxisAlignedBB(xz, 1-y, xz, 1-xz, 1, 1-xz);
		this.northAabb = new AxisAlignedBB(xz, xz, 1-y, 1-xz, 1-xz, 1);
		this.southAabb = new AxisAlignedBB(xz, xz, 0, 1-xz, 1-xz, y);
		this.eastAabb = new AxisAlignedBB(0, xz, xz, y, 1-xz, 1-xz);
		this.westAabb = new AxisAlignedBB(1-y, xz, xz, 1, 1-xz, 1-xz);

		this.setSoundType(soundType);
		this.setTickRandomly(true);
		this.setHardness(1.5f);
	}

	@Override public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		switch(state.getValue(BlockDirectional.FACING)){
			case DOWN:
				return downAabb;
			case NORTH:
				return northAabb;
			case SOUTH:
				return southAabb;
			case WEST:
				return westAabb;
			case EAST:
				return eastAabb;
			default: // case UP:
				return upAabb;
		}
	}

	@Override public boolean isOpaqueCube(IBlockState state){
		return false;
	}
	@Override public boolean isFullCube(IBlockState state){
		return false;
	}
	@Override public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face){
		return BlockFaceShape.UNDEFINED;
	}

	@Override public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side){
		BlockPos offset = pos.offset(side.getOpposite());
		return world.getBlockState(offset).isSideSolid(world, offset, side);
	}
	@Override public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getDefaultState().withProperty(BlockDirectional.FACING, facing);
	}

	@Override public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos){
		super.neighborChanged(state, world, pos, block, fromPos);
		this.checkAndDropBlock(world, pos, state);
	}

	@Override public void updateTick(World world, BlockPos pos, IBlockState state, Random rand){
		this.checkAndDropBlock(world, pos, state);
	}

	protected void checkAndDropBlock(World world, BlockPos pos, IBlockState state){
		EnumFacing facing = state.getValue(BlockDirectional.FACING);
		if(!canPlaceBlockOnSide(world, pos, facing)){
			this.dropBlockAsItem(world, pos, state, 0);
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	@Override public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune){
		if(!dropsAmethyst) return;
		super.dropBlockAsItemWithChance(world, pos, state, chance, fortune);
	}

	@Override public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return ModItems.AMETHYST_SHARD;
	}

	@Override public int quantityDroppedWithBonus(int fortune, Random random){
		return fortune>0 ?
				this.quantityDropped(random)*Math.min(1, random.nextInt(fortune+2)) :
				this.quantityDropped(random);
	}

	@Override public int quantityDropped(Random random){
		return 4;
	}

	@Override protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, BlockDirectional.FACING);
	}

	@Override public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.byIndex(meta));
	}
	@Override public int getMetaFromState(IBlockState state){
		return state.getValue(BlockDirectional.FACING).getIndex();
	}

	@Override public IBlockState withRotation(IBlockState state, Rotation rot){
		return state.withProperty(BlockDirectional.FACING, rot.rotate(state.getValue(BlockDirectional.FACING)));
	}
	@Override public IBlockState withMirror(IBlockState state, Mirror mirror){
		return state.withRotation(mirror.toRotation(state.getValue(BlockDirectional.FACING)));
	}

	@Override public EnumPushReaction getPushReaction(IBlockState state){
		return EnumPushReaction.DESTROY;
	}
	@Override public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.CUTOUT;
	}
}
