package tictim.hearthstones.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
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

	private final Player player;
	private final InteractionHand hand;
	private final PlayerTavernMemory memory;
	private final ItemStack stack;
	private final Hearthstone hearthstone;
	@Nullable private final TavernRecord dest;
	@Nullable private Tavern te;

	private final Vec3 originPos;
	private final ResourceLocation originDim;
	@Nullable private Vec3 destPos;

	public HearthingContext(Player player, InteractionHand hand){
		this.player = player;
		this.hand = hand;
		this.memory = PlayerTavernMemory.get(player);
		this.stack = player.getItemInHand(hand);
		this.hearthstone = ((HearthstoneItem)stack.getItem()).getHearthstone();
		this.dest = hearthstone.getDestination(this);
		this.originPos = player.position();
		this.originDim = player.level.dimension().location();
	}

	public Player getPlayer(){
		return player;
	}
	public InteractionHand getHand(){
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
	public Vec3 getOriginPos(){
		return originPos;
	}
	public ResourceLocation getOriginDim(){
		return originDim;
	}
	@Nullable public Vec3 getDestinationPos(){
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
		if(hasCooldown()) player.displayClientMessage(new TranslatableComponent("info.hearthstones.hearthstone.cooldown"), true);
		else{
			this.te = dest!=null ? getTavernAt(dest.getDimensionType(), dest.getPos()) : null;
			if(te!=null){
				dest.setMissing(false);
				if(te.canTeleportTo(this)){
					BlockPos pos = getWarpPos();
					destPos = new Vec3(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5);
					playWarpSound();
					hearthstone.onWarp(this);
					dest.update(te);
					memory.add(te);
					applyCooldown();
					hearthstone.applyDamage(this);
					return;
				}else player.displayClientMessage(new TranslatableComponent("info.hearthstones.hearthstone.no_permission"), true);
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
			Level w = ServerLifecycleHooks.getCurrentServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, dim));
			BlockEntity te = w.getBlockEntity(pos);
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

		if(entity instanceof ServerPlayer player){
			boolean inSameDimension = dest.isInSameDimension(player);
			ServerLevel w;
			if(inSameDimension) w = (ServerLevel)player.level;
			else{
				w = ServerLifecycleHooks.getCurrentServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, dest.getDimensionType()));
				if(w==null){
					Hearthstones.LOGGER.error("World {} doesn't exists.", dest.getDimensionType());
					return;
				}
			}
			w.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, new ChunkPos(new BlockPos(destPos)), 1, entity.getId());
			player.stopRiding();
			if(player.isSleeping()) player.stopSleeping();
			if(inSameDimension) player.connection.teleport(destPos.x, destPos.y, destPos.z, player.getYRot(), player.getXRot(), Collections.emptySet());
			else{
				player.teleportTo(w, destPos.x, destPos.y, destPos.z, player.getYRot(), player.getXRot());
			}
		}else{
			entity.unRide();
			if(!dest.isInSameDimension(entity)){
				ServerLevel w = ServerLifecycleHooks.getCurrentServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, dest.getDimensionType()));
				Entity e2 = entity.getType().create(w);
				if(e2!=null){
					e2.restoreFrom(entity);
					e2.moveTo(destPos.x, destPos.y, destPos.z, e2.getYRot(), e2.getXRot());
					w.addFreshEntity(e2);
					entity.remove(Entity.RemovalReason.CHANGED_DIMENSION);
				}else Hearthstones.LOGGER.warn("Failed to move Entity {}", entity);
			}else entity.moveTo(destPos.x, destPos.y, destPos.z, player.getYRot(), player.getXRot());
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
		player.level.playSound(null, player.getX(), player.getEyeY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 20, 0.95f+SOUND_RNG.nextFloat()*0.1f);
		te.world().playSound(null, destPos.x, destPos.y+player.getEyeHeight(), destPos.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 20, 0.95f+SOUND_RNG.nextFloat()*0.1f);
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

	private static BlockPos calculateSpawnPos(Level world, BlockPos pos, @Nullable Direction defaultFacing){
		BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
		for(int i = 0; i<4; i++){
			Direction current = Direction.from2DDataValue(i);
			if(defaultFacing!=current){
				mpos.set(pos).move(current);
				if(hasRoomForPlayer(world, mpos)) return mpos.immutable();
			}
		}
		return hasRoomForPlayer(world, mpos.set(pos)) ? mpos.immutable() : mpos.above();
	}

	private static boolean hasRoomForPlayer(Level world, BlockPos pos){
		return Block.canSupportCenter(world, pos.below(), Direction.UP)&&
				isNotSolidNorLiquid(world.getBlockState(pos))&&
				isNotSolidNorLiquid(world.getBlockState(pos.above()));
	}
	private static boolean isNotSolidNorLiquid(BlockState blockState){
		return !blockState.getMaterial().isSolid()&&!blockState.getMaterial().isLiquid();
	}
}
