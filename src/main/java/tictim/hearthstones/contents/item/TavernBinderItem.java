package tictim.hearthstones.contents.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import tictim.hearthstones.Caps;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.block.TavernBlock;
import tictim.hearthstones.contents.tileentity.BinderLecternTile;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.OpenBinderScreenMsg;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernBinderData;
import tictim.hearthstones.tavern.TavernMemories;

import javax.annotation.Nullable;
import java.util.List;

public class TavernBinderItem extends RareItem{
	private final boolean infiniteWaypoints;

	public TavernBinderItem(boolean infiniteWaypoints){
		this.infiniteWaypoints = infiniteWaypoints;
		this.setMaxStackSize(1);
	}

	@Override public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand){
		if(world.isRemote) return EnumActionResult.PASS;
		ItemStack heldItem = player.getHeldItem(hand);
		TavernBinderData data = data(heldItem);
		if(data==null) return EnumActionResult.PASS;
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof Tavern){
			Tavern tavern = (Tavern)te;
			boolean result = data.addOrUpdateWaypoint(tavern);
			if(player!=null)
				player.sendStatusMessage(new TextComponentTranslation(result ?
						"info.hearthstones.binder.saved" :
						"info.hearthstones.binder.no_waypoint"), true);
		}else if(te instanceof BinderLecternTile){
			BinderLecternTile binderLectern = (BinderLecternTile)te;
			if(binderLectern.hasBinder()){
				TavernBinderData d2 = binderLectern.getData();
				if(d2!=null){
					boolean r1 = data.syncFrom(d2.memory), r2 = d2.syncFrom(data.memory);
					if(r1||r2){
						if(r2) binderLectern.setChanged();
						if(player!=null)
							player.sendStatusMessage(new TextComponentTranslation("info.hearthstones.binder.combined"), true);
					}
					TavernBlock.playSyncSound(world, pos);
				}
			}else{
				binderLectern.setBinder(player, heldItem);
				heldItem.shrink(heldItem.getCount());
				world.playSound(null, pos, SoundEvents.ENTITY_ITEMFRAME_ADD_ITEM, SoundCategory.BLOCKS, 1, 1);
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	@Override public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		ItemStack stack = player.getHeldItem(hand);
		if(!world.isRemote){
			TavernBinderData data = data(stack);
			if(data!=null){
				if(player.isSneaking()){
					if(player instanceof EntityPlayerMP){
						EntityPlayerMP mp = (EntityPlayerMP)player;
						ModNet.CHANNEL.sendTo(new OpenBinderScreenMsg(hand==EnumHand.OFF_HAND ?
								40 /* off-hand slot */ : player.inventory.currentItem,
								data.memory, data.getEmptyWaypoints(), data.isInfiniteWaypoints()), mp);
					}
				}else{
					data.syncTo(TavernMemories.player(player));
					TavernBlock.playSyncSound(world, player);
				}
			}
		}
		return EnumActionResult.SUCCESS;
	}

	@Override public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items){
		if(this.isInCreativeTab(tab)&&(infiniteWaypoints||!ModCfg.easyMode))
			items.add(new ItemStack(this));
	}

	@Override public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		TavernBinderData data = data(stack);
		if(data!=null&&(data.getWaypoints()>0||data.getEmptyWaypoints()>0)){
			tooltip.add(infiniteWaypoints ?
					I18n.format("info.hearthstones.binder.tooltip.waypoints.infinite",
							data.getWaypoints()) :
					I18n.format("info.hearthstones.binder.tooltip.waypoints",
							data.getWaypoints(), data.getEmptyWaypoints()));
		}
		tooltip.add(I18n.format("info.hearthstones.binder.tooltip"));
		if(infiniteWaypoints&&!ModCfg.easyMode)
			tooltip.add(I18n.format("info.hearthstones.binder.tooltip.infinite"));
	}

	@Override public boolean hasEffect(ItemStack stack){
		return infiniteWaypoints||super.hasEffect(stack);
	}

	@Nullable @Override public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt){
		return new TavernBinderData(infiniteWaypoints);
	}

	@Nullable @Override public NBTTagCompound getNBTShareTag(ItemStack stack){
		NBTTagCompound tag = stack.getTagCompound();
		TavernBinderData data = data(stack);
		if(data==null) return tag;
		if(tag==null) return data.serializeNBT();
		NBTTagCompound copy = tag.copy();
		copy.merge(data.serializeNBT());
		return copy;
	}

	@Override public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt){
		super.readNBTShareTag(stack, nbt);
		TavernBinderData data = data(stack);
		if(data!=null&&nbt!=null)
			data.deserializeNBT(nbt);
	}

	@Nullable public static TavernBinderData data(ItemStack stack){
		return stack.getCapability(Caps.BINDER_DATA, null);
	}
}
