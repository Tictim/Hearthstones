package tictim.hearthstones.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

public abstract class AbstractScreen extends GuiScreen{
	protected int xSize, ySize;
	private int leftPos;
	private int topPos;

	public int getLeft(){
		return leftPos;
	}
	public int getTop(){
		return topPos;
	}

	protected abstract void onInit();
	protected void onResize(){}

	protected void drawTooltip(int mouseX, int mouseY){
		GL11.glPushMatrix();
		GL11.glTranslated(leftPos, topPos, 0);
		for(GuiButton w : this.buttonList)
			if(w instanceof TooltipComponent)
				((TooltipComponent)w).renderTooltip(mouseX, mouseY);
		GL11.glPopMatrix();
	}

	@Override public void initGui(){
		onResize();
		this.leftPos = (this.width-this.xSize)/2;
		this.topPos = (this.height-this.ySize)/2;
		onInit();
	}

	@Override public boolean doesGuiPauseGame(){
		return false;
	}

	public interface TooltipComponent{
		void renderTooltip(int mouseX, int mouseY);
	}
}
