package tictim.hearthstones.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import tictim.hearthstones.tavern.Tavern;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class TavernMemoryScreen extends AbstractScreen{
	private int yOffset = 0;
	private double yOffsetFloat = 0;
	private double yOffsetDest = 0;
	private int screenY;

	private boolean initialized;

	protected final List<TavernButton> tavernButtons = new ArrayList<>();
	@Nullable private ConfirmDeleteWidget confirmDelete;
	private boolean closeConfirmDelete;

	@Override protected void onInit(){
		refreshTavernButtons();
	}
	@Override protected void onResize(){
		this.xSize = this.width;
		this.ySize = this.height;
	}

	public void refreshTavernButtons(){
		initialized = true;
		for(TavernButton tavernButton : tavernButtons)
			buttonList.remove(tavernButton);
		tavernButtons.clear();

		screenY = 0;

		for(TavernButton b : createTavernButtons()){
			b.x = getLeft();
			b.y = getTop()+screenY;
			tavernButtons.add(addButton(b));
			screenY += TavernButton.HEIGHT+(7*2)+6;
		}
	}

	public int getYOffset(){
		return yOffset;
	}

	public boolean isInitialized(){
		return initialized;
	}

	public void setCloseConfirmDelete(boolean closeConfirmDelete){
		this.closeConfirmDelete = closeConfirmDelete;
	}

	/**
	 * Get list of taverns to be displayed, unsorted.
	 */
	protected abstract Collection<TavernButton> createTavernButtons();

	protected abstract void select(Tavern tavern);

	protected void askForDeletion(Tavern tavern){
		if(confirmDelete==null){
			confirmDelete = new ConfirmDeleteWidget(this, tavern);
			addButton(confirmDelete.getYes());
			addButton(confirmDelete.getNo());
		}
	}

	protected abstract void delete(Tavern tavern);

	@Override public void drawScreen(int mouseX, int mouseY, float partialTicks){
		if(closeConfirmDelete){
			if(confirmDelete!=null) closeConfirmDeleteWidget();
		}
		GlStateManager.disableDepth();
		this.drawDefaultBackground();

		GL11.glPushMatrix();
		GL11.glTranslated(0, -yOffset, 0);
		super.drawScreen(mouseX, mouseY+yOffset, partialTicks);

		if(this.tavernButtons.isEmpty()){
			String emptyScreenMessage = getEmptyScreenMessage();
			if(emptyScreenMessage!=null)
				drawCenteredString(fontRenderer, emptyScreenMessage, this.xSize/2, this.ySize/2-5, 0xFFFFFF);
		}

		if(confirmDelete!=null)
			confirmDelete.render(mouseX, mouseY, partialTicks);
		GL11.glPopMatrix();
		if(confirmDelete==null)
			drawTooltip(mouseX, mouseY);

		GlStateManager.enableDepth();

		int scroll = Mouse.getDWheel();
		if(scroll!=0) yOffsetDest -= scroll*40;
		this.yOffsetFloat = MathHelper.clampedLerp(0.4, yOffsetFloat, this.yOffsetDest = MathHelper.clamp(yOffsetDest, 0, Math.max(0, screenY-width/2)));
		this.yOffset = (int)Math.round(yOffsetFloat);
		if(confirmDelete!=null) confirmDelete.updateYOffset();
	}

	@Nullable protected abstract String getEmptyScreenMessage();

	@Override protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
		if(confirmDelete==null||!confirmDelete.mouseClicked(mouseX, mouseY+yOffset, mouseButton))
			super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	@Override protected void mouseReleased(int mouseX, int mouseY, int state){
		super.mouseReleased(mouseX, mouseY+yOffset, state);
	}
	@Override protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick){
		super.mouseClickMove(mouseX, mouseY+yOffset, clickedMouseButton, timeSinceLastClick);
	}

	@Override protected void keyTyped(char typedChar, int keyCode){
		if(keyCode==1||mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)){
			if(confirmDelete!=null) closeConfirmDeleteWidget();
			else{
				this.mc.displayGuiScreen(null);
				if(this.mc.currentScreen==null)
					this.mc.setIngameFocus();
			}
		}
	}

	private void closeConfirmDeleteWidget(){
		if(confirmDelete==null) return;
		buttonList.remove(confirmDelete.getYes());
		buttonList.remove(confirmDelete.getNo());
		confirmDelete = null;
		closeConfirmDelete = false;
	}
}
