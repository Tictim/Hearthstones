package tictim.hearthstones.net;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.client.OverlayRenderEventHandler;
import tictim.hearthstones.client.screen.HearthstoneScreen;
import tictim.hearthstones.client.screen.TavernScreen;
import tictim.hearthstones.contents.blockentity.TavernBlockEntity;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.TavernMemories;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static tictim.hearthstones.Hearthstones.MODID;

public final class ModNet{
	private ModNet(){}

	public static final String NETVERSION = "2.0";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "master"), () -> NETVERSION, NETVERSION::equals, NETVERSION::equals);

	public static void init(){
		CHANNEL.registerMessage(0, OpenHearthstoneScreenMsg.class,
				OpenHearthstoneScreenMsg::write, OpenHearthstoneScreenMsg::read,
				ModNet::handleOpenHearthstoneScreen,
				Optional.of(NetworkDirection.PLAY_TO_CLIENT));

		CHANNEL.registerMessage(1, OpenTavernScreenMsg.class,
				OpenTavernScreenMsg::write, OpenTavernScreenMsg::read,
				ModNet::handleOpenTavernScreen,
				Optional.of(NetworkDirection.PLAY_TO_CLIENT));

		CHANNEL.registerMessage(2, UpdateTavernMsg.class,
				UpdateTavernMsg::write, UpdateTavernMsg::read,
				ModNet::handleUpdateTavern,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));

		CHANNEL.registerMessage(3, TavernMemoryOperationMsg.class,
				TavernMemoryOperationMsg::write, TavernMemoryOperationMsg::read,
				ModNet::handleTavernMemoryOperation,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));

		CHANNEL.registerMessage(4, SyncHomePosMsg.class,
				SyncHomePosMsg::write, SyncHomePosMsg::read,
				ModNet::handleSyncHomePos,
				Optional.of(NetworkDirection.PLAY_TO_CLIENT));
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
			Level level = Objects.requireNonNull(player.getServer()).getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, packet.pos().dim()));
			if(level==null){
				Hearthstones.LOGGER.error("Dimension {} is not loaded yet.", packet.pos().dim());
				return;
			}
			BlockEntity te = level.getBlockEntity(packet.pos().pos());
			if(!(te instanceof TavernBlockEntity tavern)){
				Hearthstones.LOGGER.error("There's no tavern block in {}.", packet.pos());
				return;
			}
			if(!tavern.hasModifyPermission(player)){
				Hearthstones.LOGGER.warn("{} cannot modify option of Tavern on {}.", player, packet.pos());
				return;
			}
			if(packet.name()!=null&&packet.name().length()>50){
				tavern.setName(packet.name().substring(0, 50));
			}else tavern.setName(packet.name());
			if(tavern.owner().hasOwner()&&tavern.owner().isOwnerOrOp(player)) tavern.setAccess(packet.access());
			BlockState s = level.getBlockState(packet.pos().pos());
			level.sendBlockUpdated(tavern.blockPos(), s, s, 2);
			TavernMemories.player(player).addOrUpdate(tavern);
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
			PlayerTavernMemory memory = TavernMemories.player(player);
			switch(packet.operation()){
				case TavernMemoryOperationMsg.SELECT -> memory.select(packet.pos());
				case TavernMemoryOperationMsg.DELETE -> memory.delete(packet.pos());
				case TavernMemoryOperationMsg.SET_HOME -> {
					memory.setHomeTavern(packet.pos());
					CHANNEL.send(PacketDistributor.PLAYER.with(context::getSender), new SyncHomePosMsg(packet.pos()));
				}
				default -> Hearthstones.LOGGER.warn("Unknown operation {} on TavernMemory#operate", packet.operation());
			}
		});
	}

	private static void handleOpenHearthstoneScreen(OpenHearthstoneScreenMsg packet, Supplier<NetworkEvent.Context> contextSupplier){
		NetworkEvent.Context context = contextSupplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> Client.handleOpenHearthstoneScreen(packet));
	}

	private static void handleOpenTavernScreen(OpenTavernScreenMsg packet, Supplier<NetworkEvent.Context> contextSupplier){
		NetworkEvent.Context context = contextSupplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> Client.handleOpenTavernScreen(packet));
	}

	private static void handleSyncHomePos(SyncHomePosMsg packet, Supplier<NetworkEvent.Context> contextSupplier){
		NetworkEvent.Context context = contextSupplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> Client.handleSyncHomePos(packet));
	}

	private static final class Client{
		private Client(){}

		private static void handleOpenHearthstoneScreen(OpenHearthstoneScreenMsg packet){
			if(Minecraft.getInstance().screen instanceof HearthstoneScreen screen){
				screen.updateData(packet);
			}else{
				HearthstoneScreen s = new HearthstoneScreen();
				s.updateData(packet);
				Minecraft.getInstance().setScreen(s);
			}
		}

		private static void handleOpenTavernScreen(OpenTavernScreenMsg packet){
			Minecraft.getInstance().setScreen(new TavernScreen(packet.pos(), packet.type(), packet.name(), packet.accessibility(), packet.owner(), packet.access(), packet.isHome()));
		}

		private static void handleSyncHomePos(SyncHomePosMsg packet){
			OverlayRenderEventHandler.homePos = packet.homePos();
		}
	}
}
