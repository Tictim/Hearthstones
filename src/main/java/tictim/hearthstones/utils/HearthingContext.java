package tictim.hearthstones.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.data.TavernPos;
import tictim.hearthstones.data.TavernRecord;
import tictim.hearthstones.logic.Hearthstone;
import tictim.hearthstones.logic.HearthstoneItem;
import tictim.hearthstones.logic.Tavern;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public final class HearthingContext{
	public static final double FAR_AWAY = 5000;
	public static final double NEARBY = 5;

	private static final Random SOUND_RNG = new Random();

	private final PlayerEntity player;
	private final Hand hand;
	private final PlayerTavernMemory memory;
	private final ItemStack stack;
	private final Hearthstone hearthstone;
	@Nullable private final TavernRecord dest;
	@Nullable private Tavern te;

	private final Vector3d originPos;
	private final ResourceLocation originDim;
	@Nullable private Vector3d destPos;

	public HearthingContext(PlayerEntity player, Hand hand){
		this.player = player;
		this.hand = hand;
		this.memory = PlayerTavernMemory.get(player);
		this.stack = player.getItemInHand(hand);
		this.hearthstone = ((HearthstoneItem)stack.getItem()).getHearthstone();
		this.dest = hearthstone.getDestination(this);
		this.originPos = player.position();
		this.originDim = player.level.dimension().location();
	}

	public PlayerEntity getPlayer(){
		return player;
	}
	public Hand getHand(){
		return hand;
	}
	public PlayerTavernMemory getMemory(){
		return memory;
	}
	public ItemStack getStack(){
		return stack;
	}
	public Hearthstone getHearthstone(){
		return hearthstone;
	}
	@Nullable public TavernRecord getDestinationMemory(){
		return dest;
	}
	@Nullable public Tavern getDestinationTavern(){
		return te;
	}
	public Vector3d getOriginPos(){
		return originPos;
	}
	public ResourceLocation getOriginDim(){
		return originDim;
	}
	@Nullable public Vector3d getDestinationPos(){
		return destPos;
	}
	@Nullable public ResourceLocation getDestinationDim(){
		return destPos!=null ? dest.getDimensionType() : null;
	}

	public boolean isFarAway(){
		return getConvertedDistance()>=FAR_AWAY;
	}
	public boolean isNearby(){
		return getConvertedDistance()<=NEARBY;
	}

	public double getConvertedDistance(){
		return destPos!=null ? getDestinationDim()!=originDim ? Double.POSITIVE_INFINITY : originPos.distanceTo(destPos) : Double.NaN;
	}

	public boolean hasCooldown(){
		return !player.isCreative()&&memory.hasCooldown();
	}

	public void warp(){
		if(hasCooldown()) player.displayClientMessage(new TranslationTextComponent("info.hearthstones.hearthstone.cooldown"), true);
		else{
			this.te = dest!=null ? getTavernAt(dest.getDimensionType(), dest.getPos()) : null;
			if(te!=null){
				dest.setMissing(false);
				if(te.canTeleportTo(this)){
					BlockPos pos = getWarpPos();
					destPos = new Vector3d(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5);
					playWarpSound();
					hearthstone.onWarp(this);
					dest.update(te);
					memory.add(te);
					applyCooldown();
					hearthstone.applyDamage(this);
					return;
				}else player.displayClientMessage(new TranslationTextComponent("info.hearthstones.hearthstone.no_permission"), true);
			}else if(dest!=null){
				player.displayClientMessage(hearthstone.invalidDestinationError(), true);
				dest.setMissing(true);
			}else player.displayClientMessage(hearthstone.noSelectionError(), true);
		}
		memory.sync();
	}

	@Nullable
	private static Tavern getTavernAt(ResourceLocation dim, BlockPos pos){
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if(server!=null){
			World w = ServerLifecycleHooks.getCurrentServer().getLevel(RegistryKey.create(Registry.DIMENSION_REGISTRY, dim));
			TileEntity te = w.getBlockEntity(pos);
			if(te instanceof Tavern) return (Tavern)te;
		}
		return null;
	}

	// @see TeleportCommand#teleport
	public void warpEntity(Entity entity){
		if(ModCfg.traceHearthstoneUsage()){
			log("Moving Entity {} to {}", entity, dest.getTavernPos());
		}
		entity.fallDistance = 0f;

		if(entity instanceof ServerPlayerEntity){
			ServerPlayerEntity player = (ServerPlayerEntity)entity;
			boolean inSameDimension = dest.isInSameDimension(player);
			ServerWorld w;
			if(inSameDimension) w = (ServerWorld)player.level;
			else{
				w = ServerLifecycleHooks.getCurrentServer().getLevel(RegistryKey.create(Registry.DIMENSION_REGISTRY, dest.getDimensionType()));
				if(w==null){
					Hearthstones.LOGGER.error("World {} doesn't exists.", dest.getDimensionType());
					return;
				}
			}
			w.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, new ChunkPos(new BlockPos(destPos)), 1, entity.getId());
			player.stopRiding();
			if(player.isSleeping()) player.stopSleeping();
			if(inSameDimension) player.connection.teleport(destPos.x, destPos.y, destPos.z, player.yRot, player.xRot, Collections.emptySet());
			else{
				player.teleportTo(w, destPos.x, destPos.y, destPos.z, player.yRot, player.xRot);
			}
		}else{
			entity.unRide();
			if(!dest.isInSameDimension(entity)){
				ServerWorld w = ServerLifecycleHooks.getCurrentServer().getLevel(RegistryKey.create(Registry.DIMENSION_REGISTRY, dest.getDimensionType()));
				Entity e2 = entity.getType().create(w);
				if(e2!=null){
					e2.restoreFrom(entity);
					e2.moveTo(destPos.x, destPos.y, destPos.z, e2.yRot, e2.xRot);
					w.addFreshEntity(e2);
					entity.remove();
				}else Hearthstones.LOGGER.warn("Failed to move Entity {}", entity);
			}else entity.moveTo(destPos.x, destPos.y, destPos.z, player.yRot, player.xRot);
		}
		if(ModCfg.traceHearthstoneUsage()){
			log("Moved Entity {} to {}", entity, dest.getTavernPos());
		}
	}

	private void log(String message, Entity entity, TavernPos dest){
		Hearthstones.LOGGER.debug(message, entity, dest);
		if(entity.isVehicle())
			Hearthstones.LOGGER.debug("Passengers: {}",
					entity.getPassengers().stream()
							.map(Entity::toString)
							.collect(Collectors.joining(", ")));
		if(entity.isPassenger())
			Hearthstones.LOGGER.debug("Entity riding: {}", entity.getVehicle());
	}

	private void playWarpSound(){
		player.level.playSound(null, player.getX(), player.getEyeY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 20, 0.95f+SOUND_RNG.nextFloat()*0.1f);
		te.world().playSound(null, destPos.x, destPos.y+player.getEyeHeight(), destPos.z, SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 20, 0.95f+SOUND_RNG.nextFloat()*0.1f);
	}

	private void applyCooldown(){
		memory.setCooldown(hearthstone.getCooldown(this));
	}

	private BlockPos getWarpPos(){
		BlockState state = te.world().getBlockState(te.pos());
		Direction facing = null;
		if(state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)){
			facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
			BlockPos pos2 = te.pos().relative(facing);
			if(hasRoomForPlayer(te.world(), pos2)) return pos2;
		}
		return calculateSpawnPos(te.world(), te.pos(), facing);
	}

	@Override
	public String toString(){
		return "HearthingContext{"+
				"player="+player.getGameProfile().getName()+
				", hand="+hand+
				", stack="+stack+
				", hearthstone="+hearthstone.getClass().getSimpleName()+
				", dest="+dest+
				", te="+(te==null ? "null" : te.getClass().getSimpleName())+
				", originPos="+originPos+
				", originDim="+originDim+
				", destPos="+destPos+
				'}';
	}

	private static BlockPos calculateSpawnPos(World world, BlockPos pos, @Nullable Direction defaultFacing){
		BlockPos.Mutable mpos = new BlockPos.Mutable();
		for(int i = 0; i<4; i++){
			Direction current = Direction.from2DDataValue(i);
			if(defaultFacing!=current){
				mpos.set(pos).move(current);
				if(hasRoomForPlayer(world, mpos)) return mpos.immutable();
			}
		}
		return hasRoomForPlayer(world, mpos.set(pos)) ? mpos.immutable() : mpos.above();
	}

	private static boolean hasRoomForPlayer(World world, BlockPos pos){
		return Block.canSupportCenter(world, pos.below(), Direction.UP)&&
				isNotSolidNorLiquid(world.getBlockState(pos))&&
				isNotSolidNorLiquid(world.getBlockState(pos.above()));
	}
	private static boolean isNotSolidNorLiquid(BlockState blockState){
		return !blockState.getMaterial().isSolid()&&!blockState.getMaterial().isLiquid();
	}
}
