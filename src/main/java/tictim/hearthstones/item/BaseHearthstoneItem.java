package tictim.hearthstones.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
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
	@Override public int getItemEnchantability(){
		return 5;
	}

	@Override public UseAction getUseAction(ItemStack stack){
		return UseAction.BOW;
	}
	@Override public int getUseDuration(ItemStack stack){
		int lv = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.QUICKCAST.get(), stack);
		return 120-21*lv;
	}

	@Override public boolean isDamageable(){
		return hearthstone.getMaxDamage()>0;
	}
	@Override public boolean isDamaged(ItemStack stack){
		return getMaxDamage(stack)>0&&stack.getDamage()>0;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isSelected){
		if(!world.isRemote&&entity instanceof PlayerEntity){
			PlayerEntity player = (PlayerEntity)entity;
			PlayerTavernMemory memory = PlayerTavernMemory.get(player);

			boolean hasCooldown = !player.isCreative()&&memory.hasCooldown();

			CompoundNBT tag = stack.getTag();
			if(hasCooldown!=(tag!=null&&tag.getBoolean("hasCooldown"))){
				CompoundNBT nbt = tag;
				if(nbt==null) stack.setTag(nbt = new CompoundNBT());
				nbt.putBoolean("hasCooldown", hasCooldown);
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand){
		if(!world.isRemote){
			HearthingContext ctx = new HearthingContext(player, hand);
			if(!ctx.hasCooldown()) player.setActiveHand(hand);
			else player.getCooldownTracker().setCooldown(ctx.getStack().getItem(), 20);
		}
		return new ActionResult<>(ActionResultType.CONSUME, player.getHeldItem(hand));
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity entity, int count){
		int i = Item.random.nextInt();
		if(!entity.world.isRemote){
			if(i%42==0) entity.world.playSound(null,
					entity.getPosX(),
					entity.getPosY()+entity.getEyeHeight(),
					entity.getPosZ(),
					SoundEvents.BLOCK_PORTAL_AMBIENT,
					SoundCategory.PLAYERS,
					0.3f,
					entity.getRNG().nextFloat()*0.4f+0.8f);
		}else{
			int amount = (int)(entity.getItemInUseMaxCount()*0.05);
			if(amount>0&&i%3==0) renderParticles(entity, amount);
		}
	}

	protected void renderParticles(LivingEntity entity, int amount){
		Client.renderParticles(entity, amount);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entity){
		if(!world.isRemote&&entity instanceof PlayerEntity){
			HearthingContext ctx = new HearthingContext((PlayerEntity)entity, entity.getActiveHand());
			ctx.warp();
			ctx.getPlayer().getCooldownTracker().setCooldown(stack.getItem(), 20);
		}
		return stack;
	}

	private static final class Client{
		private Client(){}

		private static final Random RNG = new Random();

		public static void renderParticles(LivingEntity entity, int amount){
			ParticleManager particleManager = Minecraft.getInstance().particles;

			for(int i = 0; i<amount; i++){
				double sy = rand(1.5, 0.75);
				Particle p = particleManager.addParticle(ParticleTypes.WITCH,
						rand(entity.getPosX(), 0.375),
						rand(entity.getPosY()+1, 0.75),
						rand(entity.getPosZ(), 0.375),
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
					p.multiplyVelocity((float)sy);
				}
			}
		}

		private static double rand(double center, double spread){
			double rand = RNG.nextGaussian()*2-1;
			return center+spread*rand;
		}
	}
}
