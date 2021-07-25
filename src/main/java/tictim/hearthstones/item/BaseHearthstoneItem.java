package tictim.hearthstones.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import tictim.hearthstones.contents.ModEnchantments;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.logic.Hearthstone;
import tictim.hearthstones.logic.HearthstoneItem;
import tictim.hearthstones.utils.HearthingContext;

import java.util.Objects;
import java.util.Random;

public abstract class BaseHearthstoneItem extends Item implements HearthstoneItem{
	private final Hearthstone hearthstone;

	public BaseHearthstoneItem(Properties properties, Hearthstone hearthstone){
		super(properties);
		this.hearthstone = Objects.requireNonNull(hearthstone);
	}

	@Override public Hearthstone getHearthstone(){
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

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean isSelected){
		if(!world.isClientSide&&entity instanceof Player player){
			PlayerTavernMemory memory = PlayerTavernMemory.get(player);

			boolean hasCooldown = !player.isCreative()&&memory.hasCooldown();

			CompoundTag tag = stack.getTag();
			if(hasCooldown!=(tag!=null&&tag.getBoolean("hasCooldown"))){
				CompoundTag nbt = tag;
				if(nbt==null) stack.setTag(nbt = new CompoundTag());
				nbt.putBoolean("hasCooldown", hasCooldown);
			}
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand){
		if(!world.isClientSide){
			HearthingContext ctx = new HearthingContext(player, hand);
			if(!ctx.hasCooldown()) player.startUsingItem(hand);
			else player.getCooldowns().addCooldown(ctx.getStack().getItem(), 20);
		}
		return new InteractionResultHolder<>(InteractionResult.CONSUME, player.getItemInHand(hand));
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity entity, int count){
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
			if(amount>0&&entity.getRandom().nextInt()%3==0) renderParticles(entity, amount);
		}
	}

	protected void renderParticles(LivingEntity entity, int amount){
		Client.renderParticles(entity, amount);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity){
		if(!world.isClientSide&&entity instanceof Player){
			HearthingContext ctx = new HearthingContext((Player)entity, entity.getUsedItemHand());
			ctx.warp();
			ctx.getPlayer().getCooldowns().addCooldown(stack.getItem(), 20);
		}
		return stack;
	}

	private static final class Client{
		private Client(){}

		private static final Random RNG = new Random();

		public static void renderParticles(LivingEntity entity, int amount){
			ParticleEngine particleManager = Minecraft.getInstance().particleEngine;

			for(int i = 0; i<amount; i++){
				double sy = rand(1.5, 0.75);
				Particle p = particleManager.createParticle(ParticleTypes.WITCH,
						rand(entity.getX(), 0.375),
						rand(entity.getY()+1, 0.75),
						rand(entity.getZ(), 0.375),
						0,
						sy,
						0);
				if(p!=null){
					// #02CCFC
					float r = (0x02)/255f;
					float g = (0xcc)/255f;
					float b = (0xfc)/255f;
					float chroma = 0.75F+RNG.nextFloat()*0.25F;
					p.setColor(r*chroma, g*chroma, b*chroma);
					p.setPower((float)sy);
				}
			}
		}

		private static double rand(double center, double spread){
			double rand = RNG.nextGaussian()*2-1;
			return center+spread*rand;
		}
	}
}
