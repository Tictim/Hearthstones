package tictim.hearthstones.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class AbstractScreen extends Screen{
	protected int xSize, ySize;
	private int leftPos;
	private int topPos;

	protected AbstractScreen(Component title){
		super(title);
	}

	public int getLeft(){
		return leftPos;
	}
	public int getTop(){
		return topPos;
	}

	protected abstract void onInit();
	protected void onResize(){}

	protected void drawTooltip(PoseStack pose, int mouseX, int mouseY){
		pose.pushPose();
		pose.translate(leftPos, topPos, 0);
		for(var w : this.renderables)
			if(w instanceof AbstractWidget w2)
				w2.renderToolTip(pose, mouseX, mouseY);
		pose.popPose();
	}

	@Override protected void init(){
		onResize();
		this.leftPos = (this.width-this.xSize)/2;
		this.topPos = (this.height-this.ySize)/2;
		onInit();
	}

	@Override
	public boolean isPauseScreen(){
		return false;
	}
}
