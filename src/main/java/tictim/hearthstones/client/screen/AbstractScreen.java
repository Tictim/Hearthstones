package tictim.hearthstones.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
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

	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY){}
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY){}
	protected void drawTooltip(MatrixStack matrixStack, int mouseX, int mouseY){
		for(Widget button : this.buttons) button.renderToolTip(matrixStack, mouseX, mouseY);
	}

	@Override protected void init(){
		onResize();
		this.guiLeft = (this.width-this.xSize)/2;
		this.guiTop = (this.height-this.ySize)/2;
		onInit();
	}

	// ripping off ContainerScreen? Yes papa
	@Override public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		this.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);

		//noinspection deprecation
		RenderSystem.disableRescaleNormal();
		RenderSystem.disableDepthTest();
		super.render(matrixStack, mouseX, mouseY, partialTicks);

		matrixStack.push();
		matrixStack.translate(guiLeft, guiTop, 0);
		//noinspection deprecation
		RenderSystem.color4f(1, 1, 1, 1);
		//noinspection deprecation
		RenderSystem.enableRescaleNormal();

		//noinspection deprecation
		RenderSystem.glMultiTexCoord2f(GL13.GL_TEXTURE2, 240, 240);
		//noinspection deprecation
		RenderSystem.color4f(1, 1, 1, 1);

		this.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);

		matrixStack.pop();
		drawTooltip(matrixStack, mouseX, mouseY);
		RenderSystem.enableDepthTest();
	}

	@Override
	public boolean isPauseScreen(){
		return false;
	}
}
