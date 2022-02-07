package tictim.hearthstones.contents.item.hearthstone;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import tictim.hearthstones.Caps;
import tictim.hearthstones.client.Rendering;
import tictim.hearthstones.contents.ModEnchantments;
import tictim.hearthstones.contents.item.RareItem;
import tictim.hearthstones.hearthstone.Hearthstone;
import tictim.hearthstones.hearthstone.WarpContext;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernRecord;

import javax.annotation.Nullable;

public class HearthstoneItem extends RareItem{
	private final Hearthstone hearthstone;

	public HearthstoneItem(Hearthstone hearthstone){
		setMaxStackSize(1);
		this.hearthstone = hearthstone;
	}

	public Hearthstone getHearthstone(){
		return hearthstone;
	}

	@SuppressWarnings("deprecation") @Override public int getMaxDamage(){
		return hearthstone.getMaxDamage();
	}
	@Override public boolean isRepairable(){
		return false;
	}
	@Override public boolean isEnchantable(ItemStack stack){
		return true;
	}
	@Override public int getItemEnchantability(){
		return 5;
	}

	@Override public EnumAction getItemUseAction(ItemStack stack){
		return EnumAction.BOW;
	}
	@Override public int getMaxItemUseDuration(ItemStack stack){
		int lv = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.QUICKCAST, stack);
		return 120-21*lv;
	}

	@Override public boolean isDamageable(){
		return hearthstone.getMaxDamage()>0;
	}
	@Override public boolean isDamaged(ItemStack stack){
		return hearthstone.getMaxDamage()>0&&stack.getItemDamage()>0;
	}
	@Override public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt){
		return new Data(stack);
	}

	@Override public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected){
		if(!world.isRemote&&world.getTotalWorldTime()%2==0&&entity instanceof EntityPlayer){
			Data data = data(stack);
			if(data==null) return;
			data.read();
			WarpContext warpContext = new WarpContext(stack, (EntityPlayer)entity);
			data.updateHasCooldown(warpContext.hasCooldown());
			data.updateDestination(hearthstone.previewWarp(warpContext));
		}
	}

	@Override public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		ItemStack itemInHand = player.getHeldItem(hand);
		if(!world.isRemote){
			if(!new WarpContext(itemInHand, player, hand).hasCooldown()) player.setActiveHand(hand);
			else{
				player.getCooldownTracker().setCooldown(this, 20);
				player.sendStatusMessage(new TextComponentTranslation("info.hearthstones.hearthstone.cooldown"), true);
			}
		}
		return EnumActionResult.SUCCESS; // TODO ?
	}

	@Override public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count){
		if(!entity.world.isRemote){
			if(entity.getRNG().nextInt()%42==0) entity.world.playSound(null,
					entity.posX,
					entity.posY+entity.getEyeHeight(),
					entity.posZ,
					SoundEvents.BLOCK_PORTAL_AMBIENT,
					SoundCategory.PLAYERS,
					0.3f,
					entity.getRNG().nextFloat()*0.4f+0.8f);
		}else{
			int amount = (int)(entity.getItemInUseMaxCount()*0.05);
			if(amount>0&&entity.getRNG().nextInt()%3==0)
				Rendering.renderHearthstoneParticles(entity, amount);
		}
	}

	@Nullable public static Data data(ICapabilityProvider capabilityProvider){
		return capabilityProvider.getCapability(Caps.HEARTHSTONE_DATA, null);
	}

	public static final class Data implements ICapabilityProvider{
		private final ItemStack stack;
		public boolean hasCooldown;
		@Nullable public TavernRecord destination;
		@Nullable private Tavern destinationCache;

		public Data(ItemStack stack){
			this.stack = stack;
		}

		public void updateHasCooldown(boolean hasCooldown){
			if(this.hasCooldown==hasCooldown) return;
			this.hasCooldown = hasCooldown;
			NBTTagCompound tag = stack.getTagCompound();
			if(hasCooldown){
				if(tag==null) stack.setTagCompound(tag = new NBTTagCompound());
				tag.setBoolean("HasCooldown", true);
			}else if(tag!=null) tag.removeTag("HasCooldown");
		}

		public void updateDestination(@Nullable Tavern destination){
			if(this.destination==null){
				if(destination==null) return;
			}else if(destination!=null&&destinationCache==destination)
				return;
			this.destinationCache = destination;
			this.destination = destination!=null ? destination.toRecord() : null;
			NBTTagCompound tag = stack.getTagCompound();
			if(this.destination!=null){
				if(tag==null) stack.setTagCompound(tag = new NBTTagCompound());
				tag.setTag("Destination", this.destination.write());
			}else if(tag!=null) tag.removeTag("Destination");
		}

		@Override public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing facing){
			return cap==Caps.HEARTHSTONE_DATA;
		}
		@Nullable @Override public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing side){
			return cap==Caps.HEARTHSTONE_DATA ? Caps.HEARTHSTONE_DATA.cast(this) : null;
		}

		private boolean read;

		public void read(){
			if(read) return;
			read = true;
			NBTTagCompound tag = stack.getTagCompound();
			if(tag==null){
				hasCooldown = false;
				destination = null;
			}else{
				hasCooldown = tag.getBoolean("HasCooldown");
				destination = tag.hasKey("Destination", Constants.NBT.TAG_COMPOUND) ? new TavernRecord(tag.getCompoundTag("Destination")) : null;
			}
		}
	}
}
