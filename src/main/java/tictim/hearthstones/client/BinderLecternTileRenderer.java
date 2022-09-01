package tictim.hearthstones.client;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import tictim.hearthstones.contents.tileentity.BinderLecternTile;

import static tictim.hearthstones.Hearthstones.MODID;

public class BinderLecternTileRenderer extends TileEntitySpecialRenderer<BinderLecternTile>{
	private static final ResourceLocation TEXTURE_BOOK = new ResourceLocation(MODID, "textures/binder_lectern.png");
	private final ModelBook modelBook = new ModelBook();

	@SuppressWarnings("ConstantConditions")
	@Override
	public void render(BinderLecternTile te, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
		if(te==null||!te.hasBinder()) return;
		GL11.glPushMatrix();
		GL11.glTranslated(x+0.5, y+1.0625, z+0.5);
		float f = te.getWorld().getBlockState(te.getPos()).getValue(BlockHorizontal.FACING).rotateY().getHorizontalAngle();
		GL11.glRotated(-f, 0, 1, 0);
		GL11.glRotated(67.5, 0, 0, 1);
		GL11.glTranslated(0, -0.125, 0);
		this.bindTexture(TEXTURE_BOOK);
		this.modelBook.render(null, 0, 0.1f, 0.9f, 1.2f, 0, 0.0625f);
		GL11.glPopMatrix();
	}
}
