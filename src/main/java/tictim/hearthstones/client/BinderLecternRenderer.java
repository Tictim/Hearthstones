package tictim.hearthstones.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LecternBlock;
import tictim.hearthstones.contents.blockentity.BinderLecternBlockEntity;

import static tictim.hearthstones.Hearthstones.MODID;

public class BinderLecternRenderer implements BlockEntityRenderer<BinderLecternBlockEntity>{
	public static final ResourceLocation TEXTURE = new ResourceLocation(MODID, "textures/binder_lectern.png");

	private final BookModel bookModel;

	public BinderLecternRenderer(BlockEntityRendererProvider.Context c){
		this.bookModel = new BookModel(c.bakeLayer(ModelLayers.BOOK));
	}

	public void render(BinderLecternBlockEntity be, float partialTick, PoseStack pose, MultiBufferSource bufferSource, int packedLight, int packedOverlay){
		pose.pushPose();
		pose.translate(0.5D, 1.0625D, 0.5D);
		float f = be.getBlockState().getValue(LecternBlock.FACING).getClockWise().toYRot();
		pose.mulPose(Vector3f.YP.rotationDegrees(-f));
		pose.mulPose(Vector3f.ZP.rotationDegrees(67.5F));
		pose.translate(0.0D, -0.125D, 0.0D);
		this.bookModel.setupAnim(0.0F, 0.1F, 0.9F, 1.2F);
		this.bookModel.render(pose, bufferSource.getBuffer(RenderType.entitySolid(TEXTURE)), packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
		pose.popPose();
	}
}