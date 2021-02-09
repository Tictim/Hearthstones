package tictim.hearthstones.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL13;

public abstract class AbstractScreen extends Screen{
	protected int xSize, ySize;
	private int guiLeft;
	private int guiTop;

	protected AbstractScreen(ITextComponent titleIn){
		super(titleIn);
	}

	public int getLeft(){
		return guiLeft;
	}
	public int getTop(){
		return guiTop;
	}

	protected abstract void onInit();
	protected void onResize(){}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){}
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){}
	protected void drawTooltip(int mouseX, int mouseY){
		for(Widget button : this.buttons) button.renderToolTip(mouseX, mouseY);
	}

	@Override protected void init(){
		onResize();
		this.guiLeft = (this.width-this.xSize)/2;
		this.guiTop = (this.height-this.ySize)/2;
		onInit();
	}

	@Override public void setSize(int w, int h){
		super.setSize(w, h);
		onResize();
		this.guiLeft = (this.width-this.xSize)/2;
		this.guiTop = (this.height-this.ySize)/2;
	}

	// ripping off ContainerScreen? Yes papa
	@Override public void render(int mouseX, int mouseY, float partialTicks){
		this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

		RenderSystem.disableRescaleNormal();
		RenderSystem.disableDepthTest();
		super.render(mouseX, mouseY, partialTicks);

		RenderSystem.pushMatrix();
		RenderSystem.translatef(this.guiLeft, this.guiTop, 0);
		RenderSystem.color4f(1, 1, 1, 1);
		RenderSystem.enableRescaleNormal();

		RenderSystem.glMultiTexCoord2f(GL13.GL_TEXTURE2, 240, 240);
		RenderSystem.color4f(1, 1, 1, 1);

		this.drawGuiContainerForegroundLayer(mouseX, mouseY);

		RenderSystem.popMatrix();
		drawTooltip(mouseX, mouseY);
		RenderSystem.enableDepthTest();
	}

	@Override
	public boolean isPauseScreen(){
		return false;
	}
}
