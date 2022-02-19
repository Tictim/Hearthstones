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
	private int waypoints;

	@Override protected void onInit(){
		super.onInit();
		addRenderableOnly(new WaypointWidget());
	}

	protected void updateData(TavernMemory memory, int waypoints){
		this.memory = memory;
		this.waypoints = waypoints;
	}

	@Override protected Collection<TavernButton> createTavernButtons(){
		List<TavernButton> buttons = new ArrayList<>();
		for(TavernRecord t : memory.taverns().values()){
			TavernButton b = new TavernButton(this, t);
			b.canDelete = waypoints!=Integer.MAX_VALUE;
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
		waypoints++;
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
			updateData(packet.memory(), packet.waypoints());
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
			updateData(packet.memory(), packet.waypoints());
			if(isInitialized()) refreshTavernButtons();
		}

		@Override protected void sendDeletePacket(Tavern tavern){
			ModNet.CHANNEL.sendToServer(new RemoveLecternBinderWaypointMsg(lecternPos, tavern.pos()));
		}
	}

	private final class WaypointWidget extends AbstractWidget{
		private final ItemStack stack = new ItemStack(ModItems.WAYPOINT.get());
		private int leftoverWaypointsCache, waypointsCache;
		private Component waypointText;

		public WaypointWidget(){
			super(0, 0, 0, 0, TextComponent.EMPTY);
		}

		private void updateSize(){
			this.width = 24+font.width(getWaypointText());
			this.height = 16;
			this.x = BinderScreen.this.width-8-this.width;
			this.y = 8+BinderScreen.this.getYOffset();
		}

		private Component getWaypointText(){
			if(waypointText==null||waypoints!=leftoverWaypointsCache||waypointsCache!=memory.taverns().size()){
				waypointText = new TextComponent(memory.taverns().size()+" / ")
						.append(new TextComponent(waypointsCache+waypoints+"")
								.withStyle(waypoints<=0 ?
										memory.taverns().isEmpty() ? ChatFormatting.RED : ChatFormatting.GOLD :
										ChatFormatting.RESET));
				leftoverWaypointsCache = waypoints;
				waypointsCache = memory.taverns().size();
			}
			return waypointText;
		}

		@Override public void renderButton(PoseStack pose, int mouseX, int mouseY, float partialTick){
			updateSize();
			drawString(pose, font, getWaypointText(), x+24, y+2, 0xFFFFFF);

			RenderSystem.enableDepthTest();
			itemRenderer.renderAndDecorateFakeItem(stack, x, y);
			RenderSystem.disableDepthTest();
		}

		@Override public void updateNarration(NarrationElementOutput narrationElementOutput){}

		@Override public void renderToolTip(PoseStack pose, int mouseX, int mouseY){
			if(isHoveredOrFocused())
				renderTooltip(pose, List.of(
						new TranslatableComponent("info.hearthstones.screen.binder.waypoints", waypointsCache),
						new TranslatableComponent("info.hearthstones.screen.binder.leftover_waypoints", leftoverWaypointsCache),
						new TranslatableComponent("info.hearthstones.screen.help.binder"),
						new TranslatableComponent("info.hearthstones.screen.help.binder_on_lectern")
				), Optional.empty(), mouseX, mouseY);
		}
	}
}
