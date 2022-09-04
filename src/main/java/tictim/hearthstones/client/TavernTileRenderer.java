package tictim.hearthstones.client;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;
import tictim.hearthstones.contents.tileentity.TavernTile;

public class TavernTileRenderer extends TileEntitySpecialRenderer<TavernTile>{
	@Override
	public void render(TavernTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		if(te==null) return;
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		TavernRenderer.renderBlock(te, this.rendererDispatcher.renderEngine, destroyStage, alpha);
		GL11.glPopMatrix();
	}
}
