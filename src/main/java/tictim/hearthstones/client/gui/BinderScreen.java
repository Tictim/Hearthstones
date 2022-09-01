package tictim.hearthstones.client.gui;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.net.ModNet;
import tictim.hearthstones.net.OpenBinderScreenMsg;
import tictim.hearthstones.net.OpenLecternBinderScreenMsg;
import tictim.hearthstones.net.RemoveBinderWaypointMsg;
import tictim.hearthstones.net.RemoveLecternBinderWaypointMsg;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernMemory;
import tictim.hearthstones.tavern.TavernRecord;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class BinderScreen extends TavernMemoryScreen{
	private TavernMemory memory = new TavernMemory();
	private int blankWaypoints;
	private boolean infiniteWaypoints;

	@Override protected void onInit(){
		super.onInit();
		addButton(new WaypointWidget(0));
	}

	protected void updateData(TavernMemory memory, int blankWaypoints, boolean infiniteWaypoints){
		this.memory = memory;
		this.blankWaypoints = blankWaypoints;
		this.infiniteWaypoints = infiniteWaypoints;
	}

	@Override protected Collection<TavernButton> createTavernButtons(){
		List<TavernButton> buttons = new ArrayList<>();
		int id = 1;
		for(TavernRecord t : memory.taverns().values()){
			TavernButton b = new TavernButton(id++, this, t);
			b.canDelete = blankWaypoints!=Integer.MAX_VALUE;
			buttons.add(b);
		}
		buttons.sort((b1, b2) -> {
			Tavern o1 = b1.tavern, o2 = b2.tavern;
			int i;

			// dimension
			i = Integer.compare(o1.pos().dim(), o2.pos().dim());
			if(i!=0) return i;

			// name
			String n1 = o1.name();
			String n2 = o2.name();
			if(n1==null){
				if(n2!=null) return -1;
			}else if(n2==null) return 1;
			else{
				i = n1.compareTo(n2);
				if(i!=0) return i;
			}

			// static position
			return o1.blockPos().compareTo(o2.blockPos());
		});
		return buttons;
	}

	@Override protected void select(Tavern tavern){}
	@Override protected void delete(Tavern tavern){
		sendDeletePacket(tavern);
		memory.delete(tavern.pos());
		refreshTavernButtons();
	}

	protected abstract void sendDeletePacket(Tavern tavern);

	@Nullable @Override protected String getEmptyScreenMessage(){
		return null;
	}

	public static class Inventory extends BinderScreen{
		private int binderInventoryPosition;

		public void updateData(OpenBinderScreenMsg packet){
			this.binderInventoryPosition = packet.getBinderInventoryPosition();
			updateData(packet.getMemory(), packet.getBlankWaypoints(), packet.isInfiniteWaypoints());
			if(isInitialized()) refreshTavernButtons();
		}

		@Override protected void sendDeletePacket(Tavern tavern){
			ModNet.CHANNEL.sendToServer(new RemoveBinderWaypointMsg(binderInventoryPosition, tavern.pos()));
		}
	}

	public static class Lectern extends BinderScreen{
		private BlockPos lecternPos;

		public void updateData(OpenLecternBinderScreenMsg packet){
			this.lecternPos = packet.getLecternPos();
			updateData(packet.getMemory(), packet.getBlankWaypoints(), packet.isInfiniteWaypoints());
			if(isInitialized()) refreshTavernButtons();
		}

		@Override protected void sendDeletePacket(Tavern tavern){
			ModNet.CHANNEL.sendToServer(new RemoveLecternBinderWaypointMsg(lecternPos, tavern.pos()));
		}
	}

	private static String formatTotalWaypoints(int waypoints, int blankWaypoints){
		String text = (long)waypoints+blankWaypoints+"";
		if(blankWaypoints<=0)
			return (waypoints<=0 ? ChatFormatting.RED : ChatFormatting.GOLD)+text+ChatFormatting.RESET;
		return text;
	}

	private final class WaypointWidget extends GuiButton implements TooltipComponent{
		private final ItemStack stack = new ItemStack(ModItems.WAYPOINT);
		private int blankWaypointsCache, waypointsCache;
		private boolean infiniteWaypointsCache;
		@Nullable private String waypointText;

		public WaypointWidget(int id){
			super(id, 0, 0, 0, 0, "");
		}

		private void updateSize(){
			this.width = 24+fontRenderer.getStringWidth(getWaypointText());
			this.height = 16;
			this.x = BinderScreen.this.width-8-this.width;
			this.y = 8+BinderScreen.this.getYOffset();
		}

		private String getWaypointText(){
			int waypoints = memory.taverns().size();
			if(waypointText==null||
					blankWaypoints!=blankWaypointsCache||
					waypointsCache!=waypoints||
					infiniteWaypointsCache!=infiniteWaypoints){
				this.waypointText = infiniteWaypoints ?
						waypoints+"" :
						waypoints+" / "+formatTotalWaypoints(waypoints, blankWaypoints);
				this.blankWaypointsCache = blankWaypoints;
				this.waypointsCache = waypoints;
				this.infiniteWaypointsCache = infiniteWaypoints;
			}
			return waypointText;
		}

		@Override public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks){
			if(visible){
				this.hovered = mouseX>=this.x&&mouseY>=this.y&&mouseX<this.x+this.width&&mouseY<this.y+this.height;

				updateSize();
				drawString(fontRenderer, getWaypointText(), x+24, y+2, 0xFFFFFF);

				GlStateManager.enableDepth();
				itemRender.renderItemIntoGUI(stack, x, y-getYOffset());
				GlStateManager.disableDepth();
			}
		}

		@Override public boolean mousePressed(Minecraft mc, int mouseX, int mouseY){
			return false;
		}

		@Override public void renderTooltip(int mouseX, int mouseY){
			if(hovered)
				drawHoveringText(isShiftKeyDown() ?
						infiniteWaypoints ? Arrays.asList(
								I18n.format("info.hearthstones.screen.binder.help.0"),
								I18n.format("info.hearthstones.screen.binder.help.1"),
								I18n.format("info.hearthstones.screen.binder.help.2"),
								I18n.format("info.hearthstones.screen.binder.help.3"),
								I18n.format("info.hearthstones.screen.binder.help.4"),
								I18n.format("info.hearthstones.screen.binder.help.6"),
								I18n.format("info.hearthstones.screen.binder.help.7"),
								I18n.format("info.hearthstones.screen.binder.help.8")
						) : Arrays.asList(
								I18n.format("info.hearthstones.screen.binder.help.0"),
								I18n.format("info.hearthstones.screen.binder.help.1"),
								I18n.format("info.hearthstones.screen.binder.help.2"),
								I18n.format("info.hearthstones.screen.binder.help.3"),
								I18n.format("info.hearthstones.screen.binder.help.4"),
								I18n.format("info.hearthstones.screen.binder.help.5"),
								I18n.format("info.hearthstones.screen.binder.help.6"),
								I18n.format("info.hearthstones.screen.binder.help.7"),
								I18n.format("info.hearthstones.screen.binder.help.8")
						) : Arrays.asList(
						infiniteWaypoints ?
								I18n.format("info.hearthstones.screen.binder.waypoints.infinite", waypointsCache) :
								I18n.format("info.hearthstones.screen.binder.waypoints", waypointsCache, blankWaypointsCache),
						I18n.format("info.hearthstones.screen.binder.help.collapsed")
				), mouseX, mouseY);
		}
	}
}