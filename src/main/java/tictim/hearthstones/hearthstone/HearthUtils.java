package tictim.hearthstones.hearthstone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.blockentity.TavernBlockEntity;
import tictim.hearthstones.tavern.TavernPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public final class HearthUtils{
	private HearthUtils(){}

	private static final Random SOUND_RNG = new Random();

	@Nullable
	public static TavernBlockEntity getTavernAt(TavernPos pos){
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if(server==null) return null;
		Level level = ServerLifecycleHooks
				.getCurrentServer()
				.getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, pos.dim()));
		if(level==null) return null;
		BlockEntity te = level.getBlockEntity(pos.pos());
		if(te instanceof TavernBlockEntity tavern) return tavern;
		return null;
	}

	@SuppressWarnings("ConstantConditions")
	public static BlockPos getWarpPos(TavernBlockEntity tavern){
		BlockState state = tavern.getBlockState();
		Direction facing = null;
		if(state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)){
			facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
			BlockPos pos2 = tavern.getBlockPos().relative(facing);
			if(hasRoomForPlayer(tavern.getLevel(), pos2)) return pos2;
		}
		BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
		for(int i = 0; i<4; i++){
			Direction current = Direction.from2DDataValue(i);
			if(facing!=current){
				mpos.set(tavern.getBlockPos()).move(current);
				if(hasRoomForPlayer(tavern.getLevel(), mpos)) return mpos.immutable();
			}
		}
		return hasRoomForPlayer(tavern.getLevel(),
				mpos.set(tavern.getBlockPos())) ? mpos.immutable() : mpos.above();
	}

	private static boolean hasRoomForPlayer(Level world, BlockPos pos){
		return Block.canSupportCenter(world, pos.below(), Direction.UP)&&
				isNotSolidNorLiquid(world.getBlockState(pos))&&
				isNotSolidNorLiquid(world.getBlockState(pos.above()));
	}
	private static boolean isNotSolidNorLiquid(BlockState blockState){
		return !blockState.getMaterial().isSolid()&&!blockState.getMaterial().isLiquid();
	}

	public static void warp(Entity entity, ResourceLocation destDimension, BlockPos destPos, boolean playSound){
		if(ModCfg.traceHearthstoneUsage())
			log("Moving Entity {} to {}:{}", entity, destDimension, destPos);
		entity.fallDistance = 0f;

		Level originalLevel = entity.level;
		double originX = entity.getX();
		double originY = entity.getY();
		double originZ = entity.getZ();
		double destX = destPos.getX()+.5;
		double destY = destPos.getY();
		double destZ = destPos.getZ()+.5;
		boolean inSameDimension = entity.level.dimension().location().equals(destDimension);
		ServerLevel destLevel = inSameDimension ?
				originalLevel instanceof ServerLevel s ? s : null :
				ServerLifecycleHooks.getCurrentServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, destDimension));
		if(destLevel==null){
			Hearthstones.LOGGER.error("World {} doesn't exists.", destDimension);
			return;
		}

		if(entity instanceof ServerPlayer player){
			destLevel.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, new ChunkPos(destPos), 1, entity.getId());
			player.stopRiding();
			if(player.isSleeping()) player.stopSleeping();
			if(inSameDimension) player.connection.teleport(destX, destY, destZ, player.getYRot(), player.getXRot(), Collections.emptySet());
			else player.teleportTo(destLevel, destX, destY, destZ, player.getYRot(), player.getXRot());
		}else{
			entity.unRide();
			if(inSameDimension) entity.moveTo(destX, destY, destZ, entity.getYRot(), entity.getXRot());
			else{
				Entity e2 = entity.getType().create(destLevel);
				if(e2==null){
					Hearthstones.LOGGER.warn("Failed to move Entity {}", entity);
					return;
				}
				e2.restoreFrom(entity);
				e2.moveTo(destX, destY, destZ, e2.getYRot(), e2.getXRot());
				destLevel.addFreshEntity(e2);
				entity.remove(Entity.RemovalReason.CHANGED_DIMENSION);
			}
		}
		if(playSound){
			playSound(originalLevel, originX, originY, originZ);
			playSound(destLevel, destX, destY, destZ);
		}
		if(ModCfg.traceHearthstoneUsage())
			log("Moved Entity {} to {}:{}", entity, destDimension, destPos);
	}

	private static void log(String message, Entity entity, ResourceLocation destDimension, BlockPos destPos){
		Hearthstones.LOGGER.debug(message, entity, destDimension, destPos);
		if(entity.isVehicle())
			Hearthstones.LOGGER.debug("Passengers: {}",
					entity.getPassengers().stream()
							.map(Entity::toString)
							.collect(Collectors.joining(", ")));
		if(entity.isPassenger())
			Hearthstones.LOGGER.debug("Entity riding: {}", entity.getVehicle());
	}

	private static void playSound(Level level, double x, double y, double z){
		level.playSound(null, x, y, z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 20, 0.95f+SOUND_RNG.nextFloat()*0.1f);
	}
}
