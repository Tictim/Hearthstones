package tictim.hearthstones.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tictim.hearthstones.contents.item.TavernBinderItem;
import tictim.hearthstones.contents.tileentity.BinderLecternTile;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.OpenLecternBinderScreenMsg;
import tictim.hearthstones.tavern.TavernBinderData;
import tictim.hearthstones.tavern.TavernMemories;

import javax.annotation.Nullable;

import static net.minecraft.block.BlockHorizontal.FACING;

@SuppressWarnings("deprecation")
public class BinderLecternBlock extends Block{
	public BinderLecternBlock(){
		super(Material.WOOD);
		this.setDefaultState(this.getDefaultState().withProperty(FACING, EnumFacing.NORTH));
		this.setSoundType(SoundType.WOOD).setHardness(2.5f);
	}

	@Override public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
	}

	@Override public boolean isFullCube(IBlockState state){
		return false;
	}
	@Override public boolean isOpaqueCube(IBlockState state){
		return false;
	}
	@Override public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face){
		return BlockFaceShape.UNDEFINED;
	}
	@Override public IBlockState getStateFromMeta(int meta){
		return this.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.byHorizontalIndex(meta%4));
	}
	@Override public int getMetaFromState(IBlockState state){
		return state.getValue(BlockHorizontal.FACING).getHorizontalIndex();
	}
	@Override public IBlockState withRotation(IBlockState state, Rotation rot){
		return state.withProperty(BlockHorizontal.FACING, rot.rotate(state.getValue(BlockHorizontal.FACING)));
	}
	@Override public IBlockState withMirror(IBlockState state, Mirror mirrorIn){
		return state.withRotation(mirrorIn.toRotation(state.getValue(BlockHorizontal.FACING)));
	}
	@Override protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, BlockHorizontal.FACING);
	}

	@Override public boolean hasTileEntity(IBlockState state){
		return true;
	}
	@Nullable @Override public TileEntity createTileEntity(World world, IBlockState state){
		return new BinderLecternTile();
	}

	@Override public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(!world.isRemote){
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof BinderLecternTile){
				BinderLecternTile binderLectern = (BinderLecternTile)te;
				TavernBinderData data = binderLectern.getData();
				if(binderLectern.hasBinder()&&data!=null){
					if(player.isSneaking()){
						if(player instanceof EntityPlayerMP)
							ModNet.CHANNEL.sendTo(new OpenLecternBinderScreenMsg(pos, data.memory, data.getEmptyWaypoints(), data.isInfiniteWaypoints()),
									(EntityPlayerMP)player);
					}else{
						data.syncTo(TavernMemories.player(player));
						TavernBlock.playSyncSound(world, player);
					}
				}
			}
		}
		return true;
	}

	@Override public void breakBlock(World world, BlockPos pos, IBlockState state){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof BinderLecternTile){
			BinderLecternTile binderLectern = (BinderLecternTile)te;
			if(binderLectern.getItem()!=null){
				ItemStack copy = binderLectern.getItem().copy();
				if(binderLectern.getData()!=null){
					TavernBinderData data = TavernBinderItem.data(copy);
					if(data!=null) data.overwrite(binderLectern.getData());
				}
				EnumFacing direction = state.getValue(FACING);
				float x = .25f*(float)direction.getXOffset();
				float z = .25f*(float)direction.getZOffset();
				EntityItem itemEntity = new EntityItem(world, pos.getX()+.5+x, pos.getY()+1, pos.getZ()+.5+z, copy);
				itemEntity.setDefaultPickupDelay();
				world.spawnEntity(itemEntity);
				binderLectern.setBinder(null, null);
			}
		}
		super.breakBlock(world, pos, state);
	}
}
