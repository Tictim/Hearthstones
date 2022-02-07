package tictim.hearthstones.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import tictim.hearthstones.client.HearthstoneOverlay;
import tictim.hearthstones.tavern.Tavern;

final class ConfirmDeleteWidget{
	private static final int BUTTON_WIDTH = 60;
	private static final int BUTTON_HEIGHT = 20;

	private final TavernMemoryScreen screen;
	private final Tavern tavern;
	private final String confirmDeleteText;
	private final GuiButton yes;
	private final GuiButton no;

	public ConfirmDeleteWidget(TavernMemoryScreen screen, Tavern tavern){
		this.screen = screen;
		this.confirmDeleteText = I18n.format("info.hearthstones.hearthstone.confirm_delete", tavern.name());

		this.yes = new GuiButton(0,
				screen.width/2-5-BUTTON_WIDTH, buttonY(),
				BUTTON_WIDTH, BUTTON_HEIGHT,
				I18n.format("info.hearthstones.hearthstone.confirm_delete.yes"));
		this.no = new GuiButton(0,
				screen.width/2+5, buttonY(),
				BUTTON_WIDTH, BUTTON_HEIGHT,
				I18n.format("info.hearthstones.hearthstone.confirm_delete.no"));
		this.tavern = tavern;
	}

	public GuiButton getYes(){
		return yes;
	}
	public GuiButton getNo(){
		return no;
	}
	public int buttonY(){
		return screen.height/2+20+4+screen.getYOffset();
	}

	public void render(int mouseX, int mouseY, float partialTicks){
		Gui.drawRect(0, screen.getYOffset(), screen.width, screen.height+screen.getYOffset(), 0xc0101010);

		Minecraft mc = Minecraft.getMinecraft();
		mc.fontRenderer.drawString(confirmDeleteText,
				(screen.width-mc.fontRenderer.getStringWidth(confirmDeleteText))/2,
				screen.height/2-20-4-10+screen.getYOffset(), 0xFFFFFFFF);

		HearthstoneOverlay.drawTavernOverlay(mc.player, screen.width/2, screen.height/2-20+screen.getYOffset(), tavern, null);

		yes.drawButton(mc, mouseX, mouseY+screen.getYOffset(), partialTicks);
		no.drawButton(mc, mouseX, mouseY+screen.getYOffset(), partialTicks);
	}

	public void updateYOffset(){
		yes.y = no.y = buttonY();
	}

	public boolean mouseClicked(int mouseX, int mouseY, int button){
		if(button!=0) return false;
		Minecraft mc = Minecraft.getMinecraft();
		if(yes.mousePressed(mc, mouseX, mouseY)){
			yes.playPressSound(mc.getSoundHandler());
			yesAction();
			return true;
		}else if(no.mousePressed(mc, mouseX, mouseY)){
			no.playPressSound(mc.getSoundHandler());
			noAction();
			return true;
		}else return false;
	}

	private void yesAction(){
		screen.delete(tavern);
		screen.setCloseConfirmDelete(true);
	}

	private void noAction(){
		screen.setCloseConfirmDelete(true);
	}
}
