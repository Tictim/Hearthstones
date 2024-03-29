package tictim.hearthstones.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import tictim.hearthstones.client.HearthstoneOverlay;
import tictim.hearthstones.tavern.Tavern;

final class ConfirmDeleteWidget{
	private static final int BUTTON_WIDTH = 60;
	private static final int BUTTON_HEIGHT = 20;

	private final TavernMemoryScreen screen;
	private final Component confirmDeleteText;
	private final Tavern tavern;
	private final Button yes;
	private final Button no;

	public ConfirmDeleteWidget(TavernMemoryScreen screen, Tavern tavern){
		this.screen = screen;
		this.confirmDeleteText = Component.translatable("info.hearthstones.hearthstone.confirm_delete");
		this.tavern = tavern;

		this.yes = new Button(
				screen.width/2-5-BUTTON_WIDTH, buttonY(),
				BUTTON_WIDTH, BUTTON_HEIGHT,
				Component.translatable("info.hearthstones.hearthstone.confirm_delete.yes"),
				a -> {
					screen.delete(tavern);
					screen.setCloseConfirmDelete(true);
				});
		this.no = new Button(
				screen.width/2+5, buttonY(),
				BUTTON_WIDTH, BUTTON_HEIGHT,
				Component.translatable("info.hearthstones.hearthstone.confirm_delete.no"),
				a -> screen.setCloseConfirmDelete(true));
	}

	public Button getYes(){
		return yes;
	}
	public Button getNo(){
		return no;
	}

	public int buttonY(){
		return screen.height/2+20+4+screen.getYOffset();
	}

	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks){
		GuiComponent.fill(pose, 0, screen.getYOffset(), screen.width, screen.height+screen.getYOffset(), 0xc0101010);

		GuiComponent.drawCenteredString(pose, screen.getMinecraft().font, confirmDeleteText, screen.width/2, screen.height/2-20-4-10+screen.getYOffset(), 0xFFFFFFFF);

		HearthstoneOverlay.drawTavernOverlay(pose, screen.getMinecraft().player, screen.width/2, screen.height/2-20+screen.getYOffset(), tavern, null);

		yes.render(pose, mouseX, mouseY+screen.getYOffset(), partialTicks);
		no.render(pose, mouseX, mouseY+screen.getYOffset(), partialTicks);
	}

	public void updateYOffset(){
		yes.y = no.y = buttonY();
	}
	public boolean mouseClicked(double mouseX, double mouseY, int button){
		if(yes.mouseClicked(mouseX, mouseY, button)){
			screen.setFocused(yes);
			if(button==0) screen.setDragging(true);
			return true;
		}
		if(no.mouseClicked(mouseX, mouseY, button)){
			screen.setFocused(no);
			if(button==0) screen.setDragging(true);
			return true;
		}
		return false;
	}
}
