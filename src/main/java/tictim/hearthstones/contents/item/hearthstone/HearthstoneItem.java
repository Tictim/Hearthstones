package tictim.hearthstones.contents.item.hearthstone;

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
import tictim.hearthstones.client.Rendering;
import tictim.hearthstones.contents.ModEnchantments;
import tictim.hearthstones.hearthstone.Hearthstone;
import tictim.hearthstones.hearthstone.WarpContext;

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
		if(!level.isClientSide&&entity instanceof Player player){
			boolean hasCooldown = new WarpContext(stack, player).hasCooldown();

			CompoundTag tag = stack.getTag();
			if(hasCooldown!=(tag!=null&&tag.getBoolean("hasCooldown"))){
				CompoundTag nbt = tag;
				if(nbt==null) stack.setTag(nbt = new CompoundTag());
				nbt.putBoolean("hasCooldown", hasCooldown);
			}
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

	@Override public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity){
		if(level.isClientSide||!(entity instanceof Player player)) return stack;
		WarpContext ctx = new WarpContext(stack, player, player.getUsedItemHand());
		Hearthstone.WarpSetup warpSetup = getHearthstone().setupWarp(ctx);
		if(warpSetup!=null) warpSetup.warp();

		player.getCooldowns().addCooldown(this, 20);
		return stack;
	}
}
