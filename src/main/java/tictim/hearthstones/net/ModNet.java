package tictim.hearthstones.net;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.client.screen.HearthstoneScreen;
import tictim.hearthstones.client.screen.TavernScreen;
import tictim.hearthstones.data.GlobalTavernMemory;
import tictim.hearthstones.data.Owner;
import tictim.hearthstones.data.PlayerTavernMemory;
import tictim.hearthstones.data.TavernPos;
import tictim.hearthstones.logic.Tavern;
import tictim.hearthstones.utils.AccessModifier;
import tictim.hearthstones.utils.Accessibility;
import tictim.hearthstones.utils.TavernType;

import javax.annotation.Nullable;
import java.util.Objects;

import static tictim.hearthstones.Hearthstones.MODID;
import static tictim.hearthstones.net.ModNet.Client.handleOpenTavernScreen;
import static tictim.hearthstones.net.ModNet.Client.handleSyncTavernMemory;

public final class ModNet{
	private ModNet(){}

	public static final String NETVERSION = "1.0";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "master"), () -> NETVERSION, NETVERSION::equals, NETVERSION::equals);

	public static void init(){
		ModNet.CHANNEL.registerMessage(0, UpdateTavern.class, (packet, buffer) -> {
			packet.pos.write(buffer);
			writeOptionalName(buffer, packet.name);
			buffer.writeByte(packet.access.ordinal());
		}, buffer -> new UpdateTavern(
				new TavernPos(buffer),
				readOptionalName(buffer),
				AccessModifier.fromMeta(buffer.readUnsignedByte())
		), (packet, contextSupplier) -> {
			NetworkEvent.Context context = contextSupplier.get();
			if(context.getDirection()!=NetworkDirection.PLAY_TO_SERVER) return;
			context.enqueueWork(() -> {
				ServerPlayerEntity player = context.getSender();
				if(player==null){
					Hearthstones.LOGGER.error("Sender doesn't exists.");
					return;
				}
				World w = Objects.requireNonNull(player.getServer()).getWorld(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, packet.pos.dim));
				if(w==null){
					Hearthstones.LOGGER.error("Dimension {} is not loaded yet.", packet.pos.dim);
					return;
				}
				TileEntity te = w.getTileEntity(packet.pos.pos);
				if(!(te instanceof Tavern)){
					Hearthstones.LOGGER.error("There's no tavern block in {}.", packet.pos);
					return;
				}
				Tavern tavern = (Tavern)te;
				Owner o = tavern.owner();
				if(!o.hasModifyPermission(player)){
					Hearthstones.LOGGER.warn("{} cannot modify option of Tavern on {}.", player, packet.pos);
					return;
				}
				tavern.setName(packet.name);
				if(o.hasOwner()&&o.isOwnerOrOp(player)) o.setAccessModifier(packet.access);
				BlockState s = w.getBlockState(packet.pos.pos);
				w.notifyBlockUpdate(tavern.pos(), s, s, 2);
				PlayerTavernMemory.get(player).add(tavern);
			});
			context.setPacketHandled(true);
		});

		ModNet.CHANNEL.registerMessage(1, SyncTavernMemory.class, (packet, buffer) -> {
			buffer.writeCompoundTag(packet.player);
			buffer.writeCompoundTag(packet.global);
		}, buffer -> new SyncTavernMemory(Objects.requireNonNull(buffer.readCompoundTag()), Objects.requireNonNull(buffer.readCompoundTag())), (packet, contextSupplier) -> {
			NetworkEvent.Context context = contextSupplier.get();
			if(context.getDirection().getOriginationSide()==LogicalSide.SERVER) context.enqueueWork(() -> handleSyncTavernMemory(packet));
			context.setPacketHandled(true);
		});

		ModNet.CHANNEL.registerMessage(2, TavernMemoryOperation.class, (packet, buffer) -> {
			packet.pos.write(buffer);
			buffer.writeByte(packet.operation);
		}, buffer -> new TavernMemoryOperation(
				new TavernPos(buffer),
				buffer.readByte()
		), (packet, contextSupplier) -> {
			NetworkEvent.Context context = contextSupplier.get();
			if(context.getDirection()!=NetworkDirection.PLAY_TO_SERVER) return;
			context.enqueueWork(() -> {
				ServerPlayerEntity player = context.getSender();
				if(player==null){
					Hearthstones.LOGGER.error("Sender doesn't exists.");
					return;
				}
				PlayerTavernMemory memory = PlayerTavernMemory.get(player);
				switch(packet.operation){
					case TavernMemoryOperation.SELECT:
						memory.select(packet.pos);
						break;
					case TavernMemoryOperation.DELETE:
						memory.delete(packet.pos);
						break;
					case TavernMemoryOperation.SET_HOME:
						memory.setHomeTavern(packet.pos);
						break;
					default:
						Hearthstones.LOGGER.warn("Unknown operation {} on TavernMemory#operate", packet.operation);
						return;
				}
				ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncTavernMemory(memory, GlobalTavernMemory.get(), player));
			});
			context.setPacketHandled(true);
		});

		ModNet.CHANNEL.registerMessage(3, SyncTavernMemoryRequest.class, (packet, buffer) -> {}, buffer -> new SyncTavernMemoryRequest(), (packet, contextSupplier) -> {
			NetworkEvent.Context context = contextSupplier.get();
			if(context.getDirection()!=NetworkDirection.PLAY_TO_SERVER) return;
			ServerPlayerEntity player = context.getSender();
			if(player==null){
				Hearthstones.LOGGER.error("Sender doesn't exists.");
				return;
			}
			ModNet.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncTavernMemory(PlayerTavernMemory.get(player), GlobalTavernMemory.get(), player));
			context.setPacketHandled(true);
		});

		ModNet.CHANNEL.registerMessage(4, OpenTavernScreen.class, (packet, buffer) -> {
			packet.pos.write(buffer);
			buffer.writeByte(packet.type.id);
			writeOptionalName(buffer, packet.name);
			buffer.writeByte(packet.access.ordinal());
			buffer.writeCompoundTag(packet.owner.serializeNBT());
			buffer.writeBoolean(packet.isHome);
		}, buffer -> new OpenTavernScreen(
				new TavernPos(buffer),
				TavernType.of(buffer.readByte()),
				readOptionalName(buffer),
				Accessibility.fromMeta(buffer.readUnsignedByte()),
				new Owner(Objects.requireNonNull(buffer.readCompoundTag())),
				buffer.readBoolean()
		), (packet, contextSupplier) -> {
			NetworkEvent.Context context = contextSupplier.get();
			if(context.getDirection()!=NetworkDirection.PLAY_TO_CLIENT) return;
			context.enqueueWork(() -> handleOpenTavernScreen(packet));
			context.setPacketHandled(true);
		});
	}

	private static void writeOptionalName(PacketBuffer buffer, @Nullable ITextComponent name){
		buffer.writeBoolean(name!=null);
		if(name!=null) buffer.writeString(ITextComponent.Serializer.toJson(name));
	}

	@Nullable
	private static ITextComponent readOptionalName(PacketBuffer buffer){
		return buffer.readBoolean() ? ITextComponent.Serializer.getComponentFromJson(buffer.readString(32767)) : null;
	}

	@SuppressWarnings("unused")
	static final class Client{
		private Client(){}

		public static void handleSyncTavernMemory(SyncTavernMemory packet){
			PlayerEntity p = Minecraft.getInstance().player;
			if(p!=null){
				PlayerTavernMemory.get(p).deserializeNBT(packet.player);
				GlobalTavernMemory.get().deserializeNBT(packet.global);
				//Hearthstones.LOGGER.debug("Synced Tavern Memory.");
				if(Minecraft.getInstance().currentScreen instanceof HearthstoneScreen){
					HearthstoneScreen screen = (HearthstoneScreen)Minecraft.getInstance().currentScreen;
					screen.flagResetButtons = true;
					//Hearthstones.LOGGER.debug("Updated Screen.");
				}
			}else Hearthstones.LOGGER.error("Player does not exists.");
		}

		public static void handleOpenTavernScreen(OpenTavernScreen packet){
			if(packet.pos!=null) Minecraft.getInstance().displayGuiScreen(new TavernScreen(packet.pos, packet.type, packet.name, packet.access, packet.owner, packet.isHome));
		}
	}
}
