package tictim.hearthstones.contents.item.hearthstone;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import tictim.hearthstones.Caps;
import tictim.hearthstones.client.Rendering;
import tictim.hearthstones.contents.ModEnchantments;
import tictim.hearthstones.hearthstone.Hearthstone;
import tictim.hearthstones.hearthstone.WarpContext;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernRecord;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class HearthstoneItem extends Item{
	private final Hearthstone hearthstone;

	public HearthstoneItem(Properties properties, Hearthstone hearthstone){
		super(properties);
		this.hearthstone = Objects.requireNonNull(hearthstone);
	}

	public Hearthstone getHearthstone(){
		return hearthstone;
	}

	@Override public int getMaxDamage(ItemStack stack){
		return hearthstone.getMaxDamage();
	}
	@Override public boolean isRepairable(ItemStack stack){
		return false;
	}
	@Override public boolean isEnchantable(ItemStack stack){
		return true;
	}
	@Override public int getEnchantmentValue(){
		return 5;
	}

	@Override public UseAnim getUseAnimation(ItemStack stack){
		return UseAnim.BOW;
	}
	@Override public int getUseDuration(ItemStack stack){
		int lv = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.QUICKCAST.get(), stack);
		return 120-21*lv;
	}

	@Override public boolean canBeDepleted(){
		return hearthstone.getMaxDamage()>0;
	}
	@Override public boolean isDamaged(ItemStack stack){
		return getMaxDamage(stack)>0&&stack.getDamageValue()>0;
	}

	@Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected){
		if(!level.isClientSide&&level.getGameTime()%2==0&&entity instanceof Player player){
			Data data = data(stack);
			if(data==null) return;
			data.read();
			WarpContext warpContext = new WarpContext(stack, player);
			data.updateHasCooldown(warpContext.hasCooldown());
			data.updateDestination(hearthstone.previewWarp(warpContext));
		}
	}

	@Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand){
		ItemStack itemInHand = player.getItemInHand(hand);
		if(!level.isClientSide){
			WarpContext ctx = new WarpContext(itemInHand, player, hand);
			if(!ctx.hasCooldown()){
				if(hearthstone.setupWarp(ctx)!=null) player.startUsingItem(hand);
				else player.getCooldowns().addCooldown(this, 20);
			}else{
				player.getCooldowns().addCooldown(this, 20);
				player.displayClientMessage(new TranslatableComponent("info.hearthstones.hearthstone.cooldown"), true);
			}
		}
		return InteractionResultHolder.consume(itemInHand);
	}

	@Override public void onUsingTick(ItemStack stack, LivingEntity entity, int count){
		if(!entity.level.isClientSide){
			if(entity.getRandom().nextInt()%42==0) entity.level.playSound(null,
					entity.getX(),
					entity.getY()+entity.getEyeHeight(),
					entity.getZ(),
					SoundEvents.PORTAL_AMBIENT,
					SoundSource.PLAYERS,
					0.3f,
					entity.getRandom().nextFloat()*0.4f+0.8f);
		}else{
			int amount = (int)(entity.getTicksUsingItem()*0.05);
			if(amount>0&&entity.getRandom().nextInt()%3==0)
				Rendering.renderHearthstoneParticles(entity, amount);
		}
	}

	@Override public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged){
		if(entity instanceof Player player) player.getCooldowns().addCooldown(this, 5);
	}

	@Override public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity){
		if(level.isClientSide||!(entity instanceof Player player)) return stack;
		WarpContext ctx = new WarpContext(stack, player, player.getUsedItemHand());
		Hearthstone.WarpSetup warpSetup = getHearthstone().setupWarp(ctx);
		if(warpSetup!=null) warpSetup.warp();

		player.getCooldowns().addCooldown(this, 20);
		return stack;
	}

	@Override public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt){
		return new Data(stack);
	}

	@SuppressWarnings("ConstantConditions") @Nullable public static Data data(ICapabilityProvider capabilityProvider){
		return capabilityProvider.getCapability(Caps.HEARTHSTONE_DATA).orElse(null);
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
			if(hasCooldown) stack.getOrCreateTag().putBoolean("HasCooldown", true);
			else if(stack.getTag()!=null) stack.getTag().remove("HasCooldown");
		}

		public void updateDestination(@Nullable Tavern destination){
			if(this.destination==null){
				if(destination==null) return;
			}else if(destination!=null&&destinationCache==destination)
				return;
			this.destinationCache = destination;
			this.destination = destination!=null ? destination.toRecord() : null;
			if(this.destination!=null) stack.getOrCreateTag().put("Destination", this.destination.write());
			else if(stack.getTag()!=null) stack.getTag().remove("Destination");
		}

		private LazyOptional<Data> self;

		@Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
			if(cap==Caps.HEARTHSTONE_DATA){
				if(self==null) self = LazyOptional.of(() -> this);
				return self.cast();
			}
			return LazyOptional.empty();
		}

		private boolean read;

		public void read(){
			if(read) return;
			read = true;
			CompoundTag tag = stack.getTag();
			if(tag==null){
				hasCooldown = false;
				destination = null;
			}else{
				hasCooldown = tag.getBoolean("HasCooldown");
				destination = tag.contains("Destination", Constants.NBT.TAG_COMPOUND) ? new TavernRecord(tag.getCompound("Destination")) : null;
			}
		}
	}
}
