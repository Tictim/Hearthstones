package tictim.hearthstones.contents.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.contents.item.TavernItem;
import tictim.hearthstones.contents.tileentity.GlobalTavernTile;
import tictim.hearthstones.contents.tileentity.NormalTavernTile;
import tictim.hearthstones.contents.tileentity.ShabbyTavernTile;
import tictim.hearthstones.contents.tileentity.TavernTile;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.OpenTavernScreenMsg;
import tictim.hearthstones.tavern.AccessModifier;
import tictim.hearthstones.tavern.Owner;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernType;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.block.BlockHorizontal.FACING;

@SuppressWarnings("deprecation")
public class TavernBlock extends Block{
	public static final IProperty<TavernType> TAVERN_TYPE = PropertyEnum.create("type", TavernType.class);

	public TavernBlock(){
		super(Material.WOOD);
		this.setDefaultState(this.getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(TAVERN_TYPE, TavernType.NORMAL));
		this.setSoundType(SoundType.WOOD).setHardness(2.5f).setResistance(6000000);
	}

	@Override public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(world.isRemote) return true;
		world.notifyBlockUpdate(pos, state, state, 0);
		TileEntity te = world.getTileEntity(pos);
		if(!(te instanceof Tavern)){
			Hearthstones.LOGGER.error("Tavern at {} is broken!", pos);
			return true;
		}
		TavernTile tavern = (TavernTile)te;
		if(tavern.hasAccessPermission(player)){
			PlayerTavernMemory memory = TavernMemories.player(player);
			memory.addOrUpdate(tavern);
			if(!player.isSneaking())
				playSyncSound(world, pos);
			else if(tavern.hasAccessPermission(player)&&player instanceof EntityPlayerMP)
				ModNet.CHANNEL.sendTo(new OpenTavernScreenMsg(tavern, player, tavern.pos().equals(memory.getHomePos())), (EntityPlayerMP)player);
		}else player.sendStatusMessage(new TextComponentTranslation("info.hearthstones.hearthstone.no_permission"), true);
		return true;
	}

	@Override public boolean hasTileEntity(IBlockState state){
		return true;
	}
	@Override public TileEntity createTileEntity(World world, IBlockState state){
		switch(state.getValue(TAVERN_TYPE)){
			case NORMAL:
				return new NormalTavernTile();
			case SHABBY:
				return new ShabbyTavernTile();
			case GLOBAL:
				return new GlobalTavernTile();
			default:
				throw new IllegalStateException("Unreachable");
		}
	}

	@Override public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items){
		for(int i = 0; i<3; i++) items.add(new ItemStack(this, 1, i));
	}

	@Override public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
		if(world.isRemote||!(placer instanceof EntityPlayer)) return;
		TileEntity te = world.getTileEntity(pos);
		if(!(te instanceof TavernTile)) return;
		TavernTile tavern = (TavernTile)te;
		if(stack.hasDisplayName()) tavern.setName(stack.getDisplayName());
		EntityPlayer player = (EntityPlayer)placer;
		if(!tavern.owner().hasOwner()){
			tavern.setOwner(Owner.of(player));
			tavern.setAccess(AccessModifier.PROTECTED);
		}
		TavernMemories.player(player).addOrUpdate(tavern);
		if(tavern.type()==TavernType.GLOBAL)
			TavernMemories.global().addOrUpdate(tavern);
	}

	@Override public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
		if(world.isRemote) return;
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TavernTile){
			TavernTile tavern = (TavernTile)te;
			TavernMemories.player(player).delete(tavern.pos());
			if(player.capabilities.isCreativeMode){
				tavern.setDropDisabled(true);
			}
		}
	}

	@Override public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune){}

	@Override public void breakBlock(World world, BlockPos pos, IBlockState state){
		if(!world.isRemote){
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof TavernTile){
				TavernTile tavern = (TavernTile)te;
				if(tavern.type()==TavernType.GLOBAL)
					TavernMemories.global().delete(tavern.pos());

				if(!tavern.isDropDisabled())
					spawnAsEntity(world, pos, createTavernStack(tavern));
			}
		}
		super.breakBlock(world, pos, state);
	}

	@Override public int damageDropped(IBlockState state){
		return state.getValue(TAVERN_TYPE).ordinal();
	}

	@Override public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag){
		tooltip.add(I18n.format("info.hearthstones.tavern.tooltip"));
		TavernType type = TavernItem.type(stack);
		switch(type){
			case GLOBAL:
				tooltip.add(I18n.format("info.hearthstones.tavern.global.tooltip.0"));
				break;
			case SHABBY:
				tooltip.add(I18n.format("info.hearthstones.tavern.shabby.tooltip"));
		}
		tooltip.add(I18n.format("info.hearthstones.tavern.help"));

		NBTTagCompound tag = stack.getTagCompound();
		if(tag==null||!tag.hasKey("BlockEntityTag", Constants.NBT.TAG_COMPOUND)) return;
		NBTTagCompound nbt = tag.getCompoundTag("BlockEntityTag");
		if(nbt.hasKey("owner", Constants.NBT.TAG_COMPOUND)){
			Owner owner = Owner.read(nbt.getCompoundTag("owner"));
			tooltip.add(" "+I18n.format("info.hearthstones.tavern.owner", owner.getName(), owner.getId()));
		}
		if(type==TavernType.GLOBAL)
			tooltip.add(I18n.format("info.hearthstones.tavern.global.tooltip.1"));
	}

	@Override public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	@Override public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.CUTOUT;
	}

	@Override public boolean hasCustomBreakingProgress(IBlockState state){
		return true;
	}
	@Override public boolean isFullCube(IBlockState state){
		return false;
	}
	@Override public boolean isOpaqueCube(IBlockState state){
		return false;
	}
	@Override public boolean isTopSolid(IBlockState state){
		return true;
	}
	@Override public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face){
		return face==EnumFacing.UP ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}
	@Override public IBlockState getStateFromMeta(int meta){
		return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta%4)).withProperty(TAVERN_TYPE, TavernType.of(meta/4));
	}
	@Override public int getMetaFromState(IBlockState state){
		return state.getValue(FACING).getHorizontalIndex()+state.getValue(TAVERN_TYPE).ordinal()*4;
	}
	@Override public IBlockState withRotation(IBlockState state, Rotation rot){
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}
	@Override public IBlockState withMirror(IBlockState state, Mirror mirrorIn){
		return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
	}
	@Override protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, FACING, TAVERN_TYPE);
	}

	public static ItemStack createTavernStack(Tavern tavern){
		ItemStack stack = TavernItem.stack(tavern.type());
		String name = tavern.name();
		if(name!=null) stack.setStackDisplayName(name);
		if(tavern.owner().hasOwner()||tavern.access().ordinal()!=0||tavern.skin()!=null){
			NBTTagCompound tileTag = new NBTTagCompound();
			if(tavern.owner().hasOwner()) tileTag.setTag("owner", tavern.owner().write());
			if(tavern.access().ordinal()!=0) tileTag.setByte("access", (byte)tavern.access().ordinal());
			if(tavern.skin()!=null) Tavern.writeSkin(tileTag, tavern.skin());
			NBTTagCompound tag = new NBTTagCompound();
			tag.setTag("BlockEntityTag", tileTag);
			stack.setTagCompound(tag);
		}
		return stack;
	}

	public static void playSyncSound(World world, BlockPos pos){
		playSyncSound(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5);
	}
	public static void playSyncSound(World world, Entity entity){
		playSyncSound(world, entity.posX, entity.posY, entity.posZ);
	}
	public static void playSyncSound(World world, double x, double y, double z){
		world.playSound(null, x, y, z, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 0.5f, 1);
	}
}