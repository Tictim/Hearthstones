package tictim.hearthstones.net;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.client.HearthstoneOverlay;
import tictim.hearthstones.client.gui.BinderScreen;
import tictim.hearthstones.client.gui.HearthstoneScreen;
import tictim.hearthstones.client.gui.TavernScreen;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.contents.item.TavernBinderItem;
import tictim.hearthstones.contents.tileentity.BinderLecternTile;
import tictim.hearthstones.contents.tileentity.TavernTile;
import tictim.hearthstones.tavern.PlayerTavernMemory;
import tictim.hearthstones.tavern.TavernBinderData;
import tictim.hearthstones.tavern.TavernMemories;

import javax.annotation.Nullable;

import static tictim.hearthstones.Hearthstones.MODID;

public class ModNet{
	public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	public static void init(){
		CHANNEL.registerMessage(Client::handleOpenHearthstoneScreen, OpenHearthstoneScreenMsg.class, 0, Side.CLIENT);
		CHANNEL.registerMessage(Client::handleOpenTavernScreen, OpenTavernScreenMsg.class, 1, Side.CLIENT);
		CHANNEL.registerMessage(ModNet::handleUpdateTavern, UpdateTavernMsg.class, 2, Side.SERVER);
		CHANNEL.registerMessage(ModNet::handleTavernMemoryOperation, TavernMemoryOperationMsg.class, 3, Side.SERVER);
		CHANNEL.registerMessage(Client::handleSyncHomePos, SyncHomePosMsg.class, 4, Side.CLIENT);
		CHANNEL.registerMessage(Client::handleOpenBinderScreen, OpenBinderScreenMsg.class, 5, Side.CLIENT);
		CHANNEL.registerMessage(Client::handleOpenLecternBinderScreen, OpenLecternBinderScreenMsg.class, 6, Side.CLIENT);
		CHANNEL.registerMessage(ModNet::handleRemoveBinderWaypoint, RemoveBinderWaypointMsg.class, 7, Side.SERVER);
		CHANNEL.registerMessage(ModNet::handleLecternRemoveBinderWaypoint, RemoveLecternBinderWaypointMsg.class, 8, Side.SERVER);
	}

	@Nullable private static IMessage handleUpdateTavern(UpdateTavernMsg packet, MessageContext ctx){
		FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
			EntityPlayerMP player = ctx.getServerHandler().player;
			if(player==null){
				Hearthstones.LOGGER.error("Sender doesn't exist.");
				return;
			}
			World world = DimensionManager.getWorld(packet.getPos().dim(), true);
			if(world==null){
				Hearthstones.LOGGER.error("Dimension {} is not loaded yet.", packet.getPos().dim());
				return;
			}
			TileEntity te = world.getTileEntity(packet.getPos().pos());
			if(!(te instanceof TavernTile)){
				Hearthstones.LOGGER.error("There's no tavern block in {}.", packet.getPos());
				return;
			}
			TavernTile tavern = (TavernTile)te;
			if(!tavern.hasModifyPermission(player)){
				Hearthstones.LOGGER.warn("{} cannot modify option of Tavern on {}.", player, packet.getPos());
				return;
			}
			if(packet.getName()!=null&&packet.getName().length()>50){
				tavern.setName(packet.getName().substring(0, 50));
			}else tavern.setName(packet.getName());
			if(tavern.owner().hasOwner()&&tavern.owner().isOwnerOrOp(player)) tavern.setAccess(packet.getAccess());
			IBlockState s = world.getBlockState(packet.getPos().pos());
			world.notifyBlockUpdate(tavern.blockPos(), s, s, 2);
			TavernMemories.player(player).addOrUpdate(tavern);
		});
		return null;
	}

	@Nullable private static IMessage handleTavernMemoryOperation(TavernMemoryOperationMsg packet, MessageContext ctx){
		FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
			PlayerTavernMemory memory = TavernMemories.player(ctx.getServerHandler().player);
			switch(packet.getOperation()){
				case TavernMemoryOperationMsg.SELECT:
					memory.select(packet.getPos());
					break;
				case TavernMemoryOperationMsg.DELETE:
					memory.delete(packet.getPos());
					break;
				case TavernMemoryOperationMsg.SET_HOME:
					memory.setHomeTavern(packet.getPos());
					CHANNEL.sendTo(new SyncHomePosMsg(packet.getPos()), ctx.getServerHandler().player);
					break;
				default:
					Hearthstones.LOGGER.warn("Unknown operation {} on TavernMemory#operate", packet.getOperation());
			}
		});
		return null;
	}

	@Nullable private static IMessage handleRemoveBinderWaypoint(RemoveBinderWaypointMsg packet, MessageContext ctx){
		FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
			EntityPlayerMP player = ctx.getServerHandler().player;

			ItemStack stack = player.inventory.getStackInSlot(packet.getBinderInventoryPosition());
			if(stack.getItem()==ModItems.WAYPOINT_BINDER){
				TavernBinderData data = TavernBinderItem.data(stack);
				if(data!=null)
					data.memory.delete(packet.getPos());
			}
		});
		return null;
	}

	@Nullable private static IMessage handleLecternRemoveBinderWaypoint(RemoveLecternBinderWaypointMsg packet, MessageContext ctx){
		FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
			EntityPlayerMP player = ctx.getServerHandler().player;
			if(player.world.isBlockLoaded(packet.getLecternPos())){
				TileEntity te = player.world.getTileEntity(packet.getLecternPos());
				if(te instanceof BinderLecternTile){
					BinderLecternTile binderLectern = (BinderLecternTile)te;
					if((binderLectern.getPlayer()==null||binderLectern.getPlayer().equals(player.getUniqueID()))&&binderLectern.getData()!=null){
						binderLectern.getData().memory.delete(packet.getTavernPos());
					}
				}
			}
		});
		return null;
	}

	private static final class Client{
		@Nullable private static IMessage handleOpenHearthstoneScreen(OpenHearthstoneScreenMsg message, MessageContext ctx){
			Minecraft mc = Minecraft.getMinecraft();
			mc.addScheduledTask(() -> {
				if(mc.currentScreen instanceof HearthstoneScreen){
					HearthstoneScreen s = (HearthstoneScreen)mc.currentScreen;
					s.updateData(message);
				}else{
					HearthstoneScreen s = new HearthstoneScreen();
					s.updateData(message);
					mc.displayGuiScreen(s);
				}
			});
			return null;
		}

		@Nullable private static IMessage handleOpenTavernScreen(OpenTavernScreenMsg packet, MessageContext ctx){
			Minecraft mc = Minecraft.getMinecraft();
			mc.addScheduledTask(() -> mc.displayGuiScreen(
					new TavernScreen(packet.getPos(),
							packet.getType(),
							packet.getName(),
							packet.getAccessibility(),
							packet.getOwner(),
							packet.getAccess(),
							packet.isHome())));
			return null;
		}

		@Nullable private static IMessage handleSyncHomePos(SyncHomePosMsg packet, MessageContext ctx){
			HearthstoneOverlay.homePos = packet.getHomePos();
			return null;
		}

		@Nullable private static IMessage handleOpenBinderScreen(OpenBinderScreenMsg packet, MessageContext ctx){
			Minecraft mc = Minecraft.getMinecraft();
			mc.addScheduledTask(() -> {
				if(mc.currentScreen instanceof BinderScreen.Inventory){
					((BinderScreen.Inventory)mc.currentScreen).updateData(packet);
				}else{
					BinderScreen.Inventory s = new BinderScreen.Inventory();
					s.updateData(packet);
					mc.displayGuiScreen(s);
				}
			});
			return null;
		}

		@Nullable private static IMessage handleOpenLecternBinderScreen(OpenLecternBinderScreenMsg packet, MessageContext ctx){
			Minecraft mc = Minecraft.getMinecraft();
			mc.addScheduledTask(() -> {
				if(mc.currentScreen instanceof BinderScreen.Lectern){
					((BinderScreen.Lectern)mc.currentScreen).updateData(packet);
				}else{
					BinderScreen.Lectern s = new BinderScreen.Lectern();
					s.updateData(packet);
					mc.displayGuiScreen(s);
				}
			});
			return null;
		}
	}
}
