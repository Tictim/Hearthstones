package tictim.hearthstones.contents.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import tictim.hearthstones.contents.block.TavernBlock;
import tictim.hearthstones.contents.tileentity.BinderLecternTile;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernBinderData;
import tictim.hearthstones.tavern.TavernMemories;
import tictim.hearthstones.tavern.TavernRecord;
import tictim.hearthstones.tavern.TavernTextFormat;

import javax.annotation.Nullable;
import java.util.List;

public class TavernWaypointItem extends RareItem{
	@Override public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand){
		if(world.isRemote) return EnumActionResult.PASS;
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof Tavern){
			Tavern tavern = (Tavern)te;
			ItemStack stack = player.getHeldItem(hand);
			if(stack.getCount()==1){
				setTavern(stack, tavern);
			}else{
				ItemStack s2 = stack.splitStack(1);
				setTavern(s2, tavern);
				if(player==null){
					world.spawnEntity(new EntityItem(world, hitX, hitY, hitZ, s2));
				}else if(!player.inventory.addItemStackToInventory(s2)){
					world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, s2));
				}
			}
			if(player!=null)
				player.sendStatusMessage(new TextComponentTranslation("info.hearthstones.waypoint.saved"), true);
		}else if(te instanceof BinderLecternTile){
			BinderLecternTile binderLectern = (BinderLecternTile)te;
			TavernBinderData data = binderLectern.getData();
			if(data!=null){
				ItemStack stack = player.getHeldItem(hand);
				Tavern tavern = getTavern(stack);
				if(tavern==null){
					if(data.addEmptyWaypoint(1)){
						stack.shrink(1);
						binderLectern.setChanged();
					}
				}else if(!data.memory.has(tavern.pos())){
					data.memory.addOrUpdate(tavern);
					TavernBlock.playSyncSound(world, pos);
					if(!data.isInfiniteWaypoints()) stack.shrink(1);
					binderLectern.setChanged();
				}
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	@Override public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		ItemStack stack = player.getHeldItem(hand);
		if(!hasTavern(stack))
			return EnumActionResult.PASS;
		if(!world.isRemote){
			TavernRecord tavern = getTavern(stack);
			if(tavern!=null){
				PlayerTavernMemory m = TavernMemories.player(player);
				if(!m.has(tavern.pos())) m.addOrUpdate(tavern);
				TavernBlock.playSyncSound(world, player);
			}
		}
		return EnumActionResult.PASS;
	}

	@Override public boolean hasEffect(ItemStack stack){
		return hasTavern(stack);
	}

	@Override public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		TavernRecord tavern = getTavern(stack);
		if(tavern!=null)
			tooltip.add(I18n.format("info.hearthstones.waypoint.tooltip.tavern",
					TavernTextFormat.name(tavern),
					TavernTextFormat.position(tavern)));
		tooltip.add(I18n.format("info.hearthstones.waypoint.tooltip.0"));
		tooltip.add(I18n.format("info.hearthstones.waypoint.tooltip.1"));
	}

	@Nullable public static TavernRecord getTavern(ItemStack stack){
		NBTTagCompound tag = stack.getTagCompound();
		return tag==null||!tag.hasKey("Tavern", Constants.NBT.TAG_COMPOUND) ? null :
				new TavernRecord(tag.getCompoundTag("Tavern"));
	}

	public static boolean hasTavern(ItemStack stack){
		NBTTagCompound tag = stack.getTagCompound();
		return tag!=null&&tag.hasKey("Tavern", Constants.NBT.TAG_COMPOUND);
	}

	public static void setTavern(ItemStack stack, @Nullable Tavern tavern){
		NBTTagCompound tag = stack.getTagCompound();
		if(tavern==null){
			if(tag!=null) tag.removeTag("Tavern");
		}else{
			if(tag==null) stack.setTagCompound(tag = new NBTTagCompound());
			tag.setTag("Tavern", TavernRecord.write(tavern));
		}
	}
}
