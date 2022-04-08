package tictim.hearthstones.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class BinderScreen extends TavernMemoryScreen{
	private TavernMemory memory = new TavernMemory();
	private int blankWaypoints;
	private boolean infiniteWaypoints;

	@Override protected void onInit(){
		super.onInit();
		addRenderableOnly(new WaypointWidget());
	}

	protected void updateData(TavernMemory memory, int blankWaypoints, boolean infiniteWaypoints){
		this.memory = memory;
		this.blankWaypoints = blankWaypoints;
		this.infiniteWaypoints = infiniteWaypoints;
	}

	@Override protected Collection<TavernButton> createTavernButtons(){
		List<TavernButton> buttons = new ArrayList<>();
		for(TavernRecord t : memory.taverns().values()){
			TavernButton b = new TavernButton(this, t);
			b.canDelete = blankWaypoints!=Integer.MAX_VALUE;
			buttons.add(b);
		}
		buttons.sort((b1, b2) -> {
			Tavern o1 = b1.tavern, o2 = b2.tavern;
			int i;

			// dimension
			i = o1.pos().dim().compareTo(o2.pos().dim());
			if(i!=0) return i;

			// name
			String n1 = o1.name();
			String n2 = o2.name();
			if(n1==null){
				if(n2!=null) return -1;
			}else{
				if(n2==null) return 1;
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

	@Nullable @Override protected Component getEmptyScreenMessage(){
		return null;
	}

	public static class Inventory extends BinderScreen{
		private int binderInventoryPosition;

		public void updateData(OpenBinderScreenMsg packet){
			this.binderInventoryPosition = packet.binderInventoryPosition();
			updateData(packet.memory(), packet.blankWaypoints(), packet.infiniteWaypoints());
			if(isInitialized()) refreshTavernButtons();
		}

		@Override protected void sendDeletePacket(Tavern tavern){
			ModNet.CHANNEL.sendToServer(new RemoveBinderWaypointMsg(binderInventoryPosition, tavern.pos()));
		}
	}

	public static class Lectern extends BinderScreen{
		private BlockPos lecternPos;

		public void updateData(OpenLecternBinderScreenMsg packet){
			this.lecternPos = packet.lecternPos();
			updateData(packet.memory(), packet.blankWaypoints(), packet.infiniteWaypoints());
			if(isInitialized()) refreshTavernButtons();
		}

		@Override protected void sendDeletePacket(Tavern tavern){
			ModNet.CHANNEL.sendToServer(new RemoveLecternBinderWaypointMsg(lecternPos, tavern.pos()));
		}
	}

	private final class WaypointWidget extends AbstractWidget{
		private final ItemStack stack = new ItemStack(ModItems.WAYPOINT.get());
		private int blankWaypointsCache, waypointsCache;
		private boolean infiniteWaypointsCache;
		@Nullable private Component waypointText;

		public WaypointWidget(){
			super(0, 0, 0, 0, TextComponent.EMPTY);
		}

		private void updateSize(){
			this.width = 24+font.width(getWaypointText());
			this.height = 16;
			this.x = BinderScreen.this.width-8-this.width;
			this.y = 8+getYOffset();
		}

		private Component getWaypointText(){
			int waypoints = memory.taverns().size();
			if(waypointText==null||
					blankWaypoints!=blankWaypointsCache||
					waypointsCache!=waypoints||
					infiniteWaypointsCache!=infiniteWaypoints){
				this.waypointText = infiniteWaypoints ?
						new TextComponent(waypoints+"") :
						new TextComponent(waypoints+" / ").append(formatTotalWaypoints(waypoints, blankWaypoints));
				this.blankWaypointsCache = blankWaypoints;
				this.waypointsCache = waypoints;
				this.infiniteWaypointsCache = infiniteWaypoints;
			}
			return waypointText;
		}

		private static Component formatTotalWaypoints(int waypoints, int blankWaypoints){
			TextComponent text = new TextComponent((long)waypoints+blankWaypoints+"");
			if(blankWaypoints<=0)
				text.withStyle(waypoints<=0 ? ChatFormatting.RED : ChatFormatting.GOLD);
			return text;
		}

		@Override public void renderButton(PoseStack pose, int mouseX, int mouseY, float partialTick){
			updateSize();
			drawString(pose, font, getWaypointText(), x+24, y+2, 0xFFFFFF);

			RenderSystem.enableDepthTest();
			itemRenderer.renderAndDecorateFakeItem(stack, x, y-getYOffset());
			RenderSystem.disableDepthTest();
		}

		@Override public void updateNarration(NarrationElementOutput narrationElementOutput){}

		private static final List<Component> infiniteBinderHelp = List.of(
				new TranslatableComponent("info.hearthstones.screen.binder.help.0"),
				new TranslatableComponent("info.hearthstones.screen.binder.help.1"),
				new TranslatableComponent("info.hearthstones.screen.binder.help.2"),
				new TranslatableComponent("info.hearthstones.screen.binder.help.3"),
				new TranslatableComponent("info.hearthstones.screen.binder.help.4"),
				new TranslatableComponent("info.hearthstones.screen.binder.help.6"),
				new TranslatableComponent("info.hearthstones.screen.binder.help.7"),
				new TranslatableComponent("info.hearthstones.screen.binder.help.8")
		);
		private static final List<Component> binderHelp = List.of(
				new TranslatableComponent("info.hearthstones.screen.binder.help.0"),
				new TranslatableComponent("info.hearthstones.screen.binder.help.1"),
				new TranslatableComponent("info.hearthstones.screen.binder.help.2"),
				new TranslatableComponent("info.hearthstones.screen.binder.help.3"),
				new TranslatableComponent("info.hearthstones.screen.binder.help.4"),
				new TranslatableComponent("info.hearthstones.screen.binder.help.5"),
				new TranslatableComponent("info.hearthstones.screen.binder.help.6"),
				new TranslatableComponent("info.hearthstones.screen.binder.help.7"),
				new TranslatableComponent("info.hearthstones.screen.binder.help.8")
		);

		@Override public void renderToolTip(PoseStack pose, int mouseX, int mouseY){
			if(isHoveredOrFocused())
				renderTooltip(pose, hasShiftDown() ?
						infiniteWaypoints ? infiniteBinderHelp : binderHelp :
						List.of(
								infiniteWaypoints ?
										new TranslatableComponent("info.hearthstones.screen.binder.waypoints.infinite", waypointsCache) :
										new TranslatableComponent("info.hearthstones.screen.binder.waypoints", waypointsCache, blankWaypointsCache),
								new TranslatableComponent("info.hearthstones.screen.binder.help.collapsed")
						), Optional.empty(), mouseX, mouseY);
		}
	}
}
