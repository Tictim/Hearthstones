package tictim.hearthstones.net;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.client.HearthstoneOverlay;
import tictim.hearthstones.client.screen.BinderScreen;
import tictim.hearthstones.client.screen.HearthstoneScreen;
import tictim.hearthstones.client.screen.TavernScreen;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.contents.blockentity.BinderLecternBlockEntity;
import tictim.hearthstones.contents.blockentity.TavernBlockEntity;
import tictim.hearthstones.contents.item.TavernBinderItem;
import tictim.hearthstones.contents.item.TavernWaypointItem;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernBinderData;
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

		CHANNEL.registerMessage(5, OpenBinderScreenMsg.class,
				OpenBinderScreenMsg::write, OpenBinderScreenMsg::read,
				ModNet::handleOpenBinderScreen,
				Optional.of(NetworkDirection.PLAY_TO_CLIENT));

		CHANNEL.registerMessage(6, OpenLecternBinderScreenMsg.class,
				OpenLecternBinderScreenMsg::write, OpenLecternBinderScreenMsg::read,
				ModNet::handleOpenLecternBinderScreen,
				Optional.of(NetworkDirection.PLAY_TO_CLIENT));

		CHANNEL.registerMessage(7, RemoveBinderWaypointMsg.class,
				RemoveBinderWaypointMsg::write, RemoveBinderWaypointMsg::read,
				ModNet::handleRemoveBinderWaypoint,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));

		CHANNEL.registerMessage(8, RemoveLecternBinderWaypointMsg.class,
				RemoveLecternBinderWaypointMsg::write, RemoveLecternBinderWaypointMsg::read,
				ModNet::handleLecternRemoveBinderWaypoint,
				Optional.of(NetworkDirection.PLAY_TO_SERVER));
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

	private static void handleRemoveBinderWaypoint(RemoveBinderWaypointMsg packet, Supplier<NetworkEvent.Context> contextSupplier){
		NetworkEvent.Context context = contextSupplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if(player==null){
				Hearthstones.LOGGER.error("Sender doesn't exist.");
				return;
			}
			ItemStack stack = player.getInventory().getItem(packet.binderInventoryPosition());
			TavernBinderData data = TavernBinderItem.data(stack);
			if(data==null) return;
			Tavern deleted = data.memory.delete(packet.pos());
			if(deleted==null||data.isInfiniteWaypoints()) return;
			dropWaypoint(player, deleted);
		});
	}

	private static void handleLecternRemoveBinderWaypoint(RemoveLecternBinderWaypointMsg packet, Supplier<NetworkEvent.Context> contextSupplier){
		NetworkEvent.Context context = contextSupplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if(player==null){
				Hearthstones.LOGGER.error("Sender doesn't exist.");
				return;
			}
			if(!player.level.isLoaded(packet.lecternPos())||
					!(player.level.getBlockEntity(packet.lecternPos()) instanceof BinderLecternBlockEntity binderLectern)) return;
			TavernBinderData data = binderLectern.getData();
			if((binderLectern.getPlayer()!=null&&!binderLectern.getPlayer().equals(player.getUUID()))||data==null) return;
			Tavern deleted = data.memory.delete(packet.tavernPos());
			if(deleted==null) return;
			binderLectern.setChanged();
			if(!data.isInfiniteWaypoints())
				dropWaypoint(player, deleted);
		});
	}

	private static void dropWaypoint(Player player, Tavern tavern){
		ItemStack waypoint = new ItemStack(ModItems.WAYPOINT.get());
		TavernWaypointItem.setTavern(waypoint, tavern);
		player.getInventory().placeItemBackInInventory(waypoint);
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

	private static void handleOpenBinderScreen(OpenBinderScreenMsg packet, Supplier<NetworkEvent.Context> contextSupplier){
		NetworkEvent.Context context = contextSupplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> Client.handleOpenBinderScreen(packet));
	}

	private static void handleOpenLecternBinderScreen(OpenLecternBinderScreenMsg packet, Supplier<NetworkEvent.Context> contextSupplier){
		NetworkEvent.Context context = contextSupplier.get();
		context.setPacketHandled(true);
		context.enqueueWork(() -> Client.handleOpenLecternBinderScreen(packet));
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
			HearthstoneOverlay.homePos = packet.homePos();
		}

		private static void handleOpenBinderScreen(OpenBinderScreenMsg packet){
			if(Minecraft.getInstance().screen instanceof BinderScreen.Inventory screen){
				screen.updateData(packet);
			}else{
				BinderScreen.Inventory s = new BinderScreen.Inventory();
				s.updateData(packet);
				Minecraft.getInstance().setScreen(s);
			}
		}

		private static void handleOpenLecternBinderScreen(OpenLecternBinderScreenMsg packet){
			if(Minecraft.getInstance().screen instanceof BinderScreen.Lectern screen){
				screen.updateData(packet);
			}else{
				BinderScreen.Lectern s = new BinderScreen.Lectern();
				s.updateData(packet);
				Minecraft.getInstance().setScreen(s);
			}
		}
	}
}
