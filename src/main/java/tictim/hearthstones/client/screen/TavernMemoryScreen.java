package tictim.hearthstones.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import tictim.hearthstones.tavern.Tavern;

import javax.annotation.Nullable;
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

	public TavernMemoryScreen(){
		super(NarratorChatListener.NO_TITLE);
	}

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
			removeWidget(tavernButton);
		tavernButtons.clear();

		screenY = 0;

		for(TavernButton b : createTavernButtons()){
			b.x = getLeft();
			b.y = getTop()+screenY;
			tavernButtons.add(addRenderableWidget(b));
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
			addRenderableWidget(confirmDelete.getYes());
			addRenderableWidget(confirmDelete.getNo());
		}
	}

	protected abstract void delete(Tavern tavern);

	@Override public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks){
		if(closeConfirmDelete){
			if(confirmDelete!=null) closeConfirmDeleteWidget();
		}
		RenderSystem.disableDepthTest();
		this.renderBackground(pose);

		pose.pushPose();
		pose.translate(0, -yOffset, 0);
		super.render(pose, mouseX, mouseY+yOffset, partialTicks);

		if(this.tavernButtons.isEmpty()){
			Component emptyScreenMessage = getEmptyScreenMessage();
			if(emptyScreenMessage!=null)
				drawCenteredString(pose, font, emptyScreenMessage, this.xSize/2, this.ySize/2-5, 0xFFFFFF);
		}

		if(confirmDelete!=null)
			confirmDelete.render(pose, mouseX, mouseY, partialTicks);
		pose.popPose();
		if(confirmDelete==null)
			drawTooltip(pose, mouseX, mouseY);

		RenderSystem.enableDepthTest();

		this.yOffsetFloat = Mth.lerp(0.4, yOffsetFloat, this.yOffsetDest = Mth.clamp(yOffsetDest, 0, Math.max(0, screenY-width/2)));
		this.yOffset = (int)Math.round(yOffsetFloat);
		if(confirmDelete!=null) confirmDelete.updateYOffset();
	}

	@Nullable
	protected abstract Component getEmptyScreenMessage();

	@Override public boolean mouseClicked(double mouseX, double mouseY, int button){
		return confirmDelete==null ?
				super.mouseClicked(mouseX, mouseY+yOffset, button) :
				confirmDelete.mouseClicked(mouseX, mouseY+yOffset, button);
	}
	@Override public boolean mouseReleased(double mouseX, double mouseY, int button){
		return super.mouseReleased(mouseX, mouseY+yOffset, button);
	}
	@Override public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY){
		return super.mouseDragged(mouseX, mouseY+yOffset, mouseButton, dragX, dragY);
	}
	@Override public boolean mouseScrolled(double mouseX, double mouseY, double scroll){
		if(confirmDelete!=null) return false;
		this.yOffsetDest -= scroll*40;
		return true;
	}

	@Override public boolean keyPressed(int keyCode, int scanCode, int modifier){
		//noinspection ConstantConditions
		if((keyCode==256&&this.shouldCloseOnEsc())||
				this.minecraft.options.keyInventory.getKey().equals(InputConstants.getKey(keyCode, scanCode))){
			if(confirmDelete!=null) closeConfirmDeleteWidget();
			else onClose();
			return true;
		}else return super.keyPressed(keyCode, scanCode, modifier);
	}

	private void closeConfirmDeleteWidget(){
		if(confirmDelete==null) return;
		removeWidget(confirmDelete.getYes());
		removeWidget(confirmDelete.getNo());
		confirmDelete = null;
		closeConfirmDelete = false;
	}
}
