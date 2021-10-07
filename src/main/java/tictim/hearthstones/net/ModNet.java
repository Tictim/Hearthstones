package tictim.hearthstones.net;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.capability.PlayerTavernMemory;
import tictim.hearthstones.capability.TavernMemory;
import tictim.hearthstones.client.screen.HearthstoneScreen;
import tictim.hearthstones.client.screen.TavernScreen;
import tictim.hearthstones.config.ModCfg;
import tictim.hearthstones.contents.blockentity.TavernBlockEntity;
import tictim.hearthstones.tavern.Owner;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static tictim.hearthstones.Hearthstones.MODID;

public final class ModNet{
	private ModNet(){}

	public static final String NETVERSION = "1.0";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "master"), () -> NETVERSION, NETVERSION::equals, NETVERSION::equals);

	public static void init(){
		CHANNEL.registerMessage(0, UpdateTavernMsg.class,
				UpdateTavernMsg::write, UpdateTavernMsg::read,
				ModNet::handleUpdateTavern,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));

		CHANNEL.registerMessage(1, SyncTavernMemoryMsg.class,
				SyncTavernMemoryMsg::write, SyncTavernMemoryMsg::read,
				ModNet::handleSyncTavernMemory,
				Optional.of(NetworkDirection.PLAY_TO_CLIENT));

		CHANNEL.registerMessage(2, TavernMemoryOperationMsg.class,
				TavernMemoryOperationMsg::write, TavernMemoryOperationMsg::read,
				ModNet::handleTavernMemoryOperation,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));

		CHANNEL.registerMessage(3, SyncTavernMemoryRequestMsg.class,
				(packet, buffer) -> {}, buffer -> new SyncTavernMemoryRequestMsg(),
				ModNet::handleSyncTavernMemoryRequest,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));

		CHANNEL.registerMessage(4, OpenTavernScreenMsg.class,
				OpenTavernScreenMsg::write, OpenTavernScreenMsg::read,
				ModNet::handleOpenTavernScreen,
				Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}

	static void writeOptionalName(FriendlyByteBuf buffer, @Nullable Component name){
		buffer.writeBoolean(name!=null);
		if(name!=null) buffer.writeUtf(Component.Serializer.toJson(name));
	}

	@Nullable
	static Component readOptionalName(FriendlyByteBuf buffer){
		return buffer.readBoolean() ? Component.Serializer.fromJson(buffer.readUtf(32767)) : null;
	}

	private static void handleUpdateTavern(UpdateTavernMsg packet, Supplier<NetworkEvent.Context> contextSupplier){
		NetworkEvent.Context context = contextSupplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if(player==null){
				Hearthstones.LOGGER.error("Sender doesn't exist.");
				return;
			}
			Level level = Objects.requireNonNull(player.getServer()).getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, packet.pos.dim));
			if(level==null){
				Hearthstones.LOGGER.error("Dimension {} is not loaded yet.", packet.pos.dim);
				return;
			}
			BlockEntity te = level.getBlockEntity(packet.pos.pos);
			if(!(te instanceof TavernBlockEntity tavern)){
				Hearthstones.LOGGER.error("There's no tavern block in {}.", packet.pos);
				return;
			}
			Owner o = tavern.owner();
			if(!tavern.hasModifyPermission(player)){
				Hearthstones.LOGGER.warn("{} cannot modify option of Tavern on {}.", player, packet.pos);
				return;
			}
			tavern.setName(packet.name);
			if(o.hasOwner()&&o.isOwnerOrOp(player)) tavern.setAccess(packet.access);
			BlockState s = level.getBlockState(packet.pos.pos);
			level.sendBlockUpdated(tavern.blockPos(), s, s, 2);
			TavernMemory.expectFromPlayer(player).addOrUpdate(tavern);
		});
	}

	private static void handleTavernMemoryOperation(TavernMemoryOperationMsg packet, Supplier<NetworkEvent.Context> contextSupplier){
		NetworkEvent.Context context = contextSupplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if(player==null){
				Hearthstones.LOGGER.error("Sender doesn't exist.");
				return;
			}
			PlayerTavernMemory memory = TavernMemory.expectFromPlayer(player);
			switch(packet.operation()){
				case TavernMemoryOperationMsg.SELECT -> memory.select(packet.pos());
				case TavernMemoryOperationMsg.DELETE -> memory.delete(packet.pos());
				case TavernMemoryOperationMsg.SET_HOME -> memory.setHomeTavern(packet.pos());
				default -> {
					Hearthstones.LOGGER.warn("Unknown operation {} on TavernMemory#operate", packet.operation());
					return;
				}
			}
			memory.sync();
		});
	}

	private static void handleSyncTavernMemory(SyncTavernMemoryMsg packet, Supplier<NetworkEvent.Context> contextSupplier){
		NetworkEvent.Context context = contextSupplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> Client.handleSyncTavernMemory(packet));
	}

	private static void handleSyncTavernMemoryRequest(SyncTavernMemoryRequestMsg packet, Supplier<NetworkEvent.Context> contextSupplier){
		NetworkEvent.Context context = contextSupplier.get();
		context.setPacketHandled(true);
		ServerPlayer player = context.getSender();
		if(player==null){
			Hearthstones.LOGGER.error("Sender doesn't exist.");
			return;
		}
		TavernMemory.expectFromPlayer(player).sync();
	}

	private static void handleOpenTavernScreen(OpenTavernScreenMsg packet, Supplier<NetworkEvent.Context> contextSupplier){
		NetworkEvent.Context context = contextSupplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> Client.handleOpenTavernScreen(packet));
	}

	@SuppressWarnings("unused")
	private static final class Client{
		private Client(){}

		private static void handleSyncTavernMemory(SyncTavernMemoryMsg packet){
			LocalPlayer p = Minecraft.getInstance().player;
			if(p!=null){
				TavernMemory.expectFromPlayer(p).read(packet.player);
				TavernMemory.expectClientGlobal().read(packet.global);
				if(ModCfg.traceTavernMemorySync()) Hearthstones.LOGGER.debug("Synced Tavern Memory.");
				if(Minecraft.getInstance().screen instanceof HearthstoneScreen screen){
					screen.markForUpdate();
					if(ModCfg.traceTavernMemorySync()) Hearthstones.LOGGER.debug("Updated Screen.");
				}
			}else Hearthstones.LOGGER.error("Player does not exist.");
		}

		private static void handleOpenTavernScreen(OpenTavernScreenMsg packet){
			if(packet.pos!=null) Minecraft.getInstance()
					.setScreen(new TavernScreen(packet.pos, packet.type, packet.name, packet.accessibility, packet.owner, packet.access, packet.isHome));
		}
	}
}
