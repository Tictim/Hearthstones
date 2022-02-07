package tictim.hearthstones.client;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;
import tictim.hearthstones.contents.tileentity.TavernTile;

public class TavernTileRenderer extends TileEntitySpecialRenderer<TavernTile>{
	@Override
	public void render(TavernTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		if(te==null) return;
		if(te.hasCustomName()&&
				this.rendererDispatcher.cameraHitResult!=null&&
				te.getPos().equals(this.rendererDispatcher.cameraHitResult.getBlockPos()))
			this.drawTavernNameplate(te, x, y, z);

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		TavernRenderer.renderBlock(te, this.rendererDispatcher.renderEngine, destroyStage, alpha);
		GL11.glPopMatrix();
	}

	private void drawTavernNameplate(TavernTile te, double x, double y, double z){
		switch(this.rendererDispatcher.cameraHitResult.sideHit){
			case EAST:
				x += 0.5D;
				break;
			case NORTH:
				z -= 0.5D;
				break;
			case SOUTH:
				z += 0.5D;
				break;
			case WEST:
				x -= 0.5D;
				break;
			case UP:
				y += 0.5D;
				break;
			case DOWN:
				--y;
		}

		this.setLightmapDisabled(true);
		this.drawNameplate(te, te.getName(), x, y-0.5D, z, 12);
		this.setLightmapDisabled(false);
	}
}
