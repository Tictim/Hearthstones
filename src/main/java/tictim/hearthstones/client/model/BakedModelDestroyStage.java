package tictim.hearthstones.client.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.stream.Collectors;

public class BakedModelDestroyStage implements IBakedModel{
	private final IBakedModel delegate;
	private final TextureAtlasSprite texture;
	private final int color4ub;

	public BakedModelDestroyStage(IBakedModel delegate, TextureAtlasSprite texture, int color4ub){
		this.delegate = delegate;
		this.texture = texture;
		this.color4ub = color4ub;
	}

	@Override public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
		return delegate.getQuads(state, side, rand).stream()
				.map(q -> new BakedQuadRecolored(q, texture, color4ub))
				.collect(Collectors.toList());
	}

	@Override public boolean isAmbientOcclusion(){
		return delegate.isAmbientOcclusion();
	}
	@Override public boolean isGui3d(){
		return delegate.isGui3d();
	}
	@Override public boolean isBuiltInRenderer(){
		return delegate.isBuiltInRenderer();
	}
	@Override public TextureAtlasSprite getParticleTexture(){
		return delegate.getParticleTexture();
	}
	@SuppressWarnings("deprecation") @Override public ItemCameraTransforms getItemCameraTransforms(){
		return delegate.getItemCameraTransforms();
	}
	@Override public ItemOverrideList getOverrides(){
		return delegate.getOverrides();
	}
	@Override public boolean isAmbientOcclusion(IBlockState state){
		return delegate.isAmbientOcclusion(state);
	}
	@Override public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType){
		return delegate.handlePerspective(cameraTransformType);
	}
}
