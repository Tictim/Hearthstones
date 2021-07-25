package tictim.hearthstones.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class AbstractScreen extends Screen{
	protected int xSize, ySize;
	private int leftPos;
	private int topPos;

	protected AbstractScreen(Component titleIn){
		super(titleIn);
	}

	public int getLeft(){
		return leftPos;
	}
	public int getTop(){
		return topPos;
	}

	protected abstract void onInit();
	protected void onResize(){}

	protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY){}
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY){}
	protected void drawTooltip(PoseStack pose, int mouseX, int mouseY){
		for(var w : this.renderables)
			if(w instanceof AbstractWidget w2)
				w2.renderToolTip(pose, mouseX, mouseY);
	}

	@Override protected void init(){
		onResize();
		this.leftPos = (this.width-this.xSize)/2;
		this.topPos = (this.height-this.ySize)/2;
		onInit();
	}

	// ripping off ContainerScreen? Yes papa
	@Override public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks){
		this.renderBg(pose, partialTicks, mouseX, mouseY);
		RenderSystem.disableDepthTest();
		super.render(pose, mouseX, mouseY, partialTicks);

		PoseStack modelView = RenderSystem.getModelViewStack();
		modelView.pushPose();
		modelView.translate(leftPos, topPos, 0);
		RenderSystem.applyModelViewMatrix();
		RenderSystem.setShaderColor(1, 1, 1, 1);

		this.renderLabels(pose, mouseX, mouseY);

		modelView.popPose();
		drawTooltip(pose, mouseX, mouseY);
		RenderSystem.applyModelViewMatrix();
		RenderSystem.enableDepthTest();
	}

	@Override
	public boolean isPauseScreen(){
		return false;
	}
}
