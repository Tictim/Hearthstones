package tictim.hearthstones.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import org.lwjgl.opengl.GL11;
import tictim.hearthstones.client.screen.TavernScreen;
import tictim.hearthstones.utils.AccessModifier;
import tictim.hearthstones.utils.TavernType;

public final class TavernRenderHelper{
	private TavernRenderHelper(){}

	public static void renderAccess(MatrixStack matrixStack, AccessModifier access){
		ResourceLocation tex;
		switch(access){
			case PUBLIC:
				tex = TavernScreen.ACCESS_PUBLIC;
				break;
			case PROTECTED:
				tex = TavernScreen.ACCESS_PROTECTED;
				break;
			case TEAM:
				tex = TavernScreen.ACCESS_TEAM;
				break;
			case PRIVATE:
				tex = TavernScreen.ACCESS_PRIVATE;
				break;
			default:
				throw new IllegalArgumentException(access.name());
		}
		Minecraft.getInstance().getTextureManager().bind(tex);

		final float uScale = 1f/16;
		final float vScale = 1f/16;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder wr = tessellator.getBuilder();
		Matrix4f matrix = matrixStack.last().pose();

		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		wr.vertex(matrix, 0, 32, 0).uv(0*uScale, ((16)*vScale)).endVertex();
		wr.vertex(matrix, 32, 32, 0).uv((16)*uScale, ((16)*vScale)).endVertex();
		wr.vertex(matrix, 32, 0, 0).uv((16)*uScale, (0*vScale)).endVertex();
		wr.vertex(matrix, 0, 0, 0).uv(0*uScale, (0*vScale)).endVertex();
		tessellator.end();
	}

	public static void renderTavernUIBase(MatrixStack matrixStack, TavernType type, boolean selected){
		Minecraft.getInstance().getTextureManager().bind(type.tavernUITexture);

		final float uScale = 1f/256;
		final float vScale = 1f/256;

		float v = selected ? 20 : 0;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder wr = tessellator.getBuilder();
		Matrix4f matrix = matrixStack.last().pose();

		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		wr.vertex(matrix, 0, 20*2, 0).uv(0*uScale, (v+20)*vScale).endVertex();
		wr.vertex(matrix, 179*2, 20*2, 0).uv((179)*uScale, (v+20)*vScale).endVertex();
		wr.vertex(matrix, 179*2, 0, 0).uv((179)*uScale, v*vScale).endVertex();
		wr.vertex(matrix, 0, 0, 0).uv(0*uScale, v*vScale).endVertex();
		tessellator.end();
	}
}
