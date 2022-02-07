package tictim.hearthstones.client.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BakedModelCombined implements IBakedModel{
	private final IBakedModel main;
	private final IBakedModel[] subs;

	public BakedModelCombined(IBakedModel main, IBakedModel... subs){
		this.main = main;
		this.subs = subs;
	}

	@Override public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
		List<BakedQuad> list = new ArrayList<>(main.getQuads(state, side, rand));
		for(IBakedModel model : subs) list.addAll(model.getQuads(state, side, rand));
		return list;
	}

	@Override public boolean isAmbientOcclusion(){
		return main.isAmbientOcclusion();
	}
	@Override public boolean isGui3d(){
		return main.isGui3d();
	}
	@Override public boolean isBuiltInRenderer(){
		return false;
	}
	@Override public TextureAtlasSprite getParticleTexture(){
		return main.getParticleTexture();
	}
	@Override public ItemOverrideList getOverrides(){
		return ItemOverrideList.NONE;
	}
	@SuppressWarnings("deprecation") @Override public ItemCameraTransforms getItemCameraTransforms(){
		return main.getItemCameraTransforms();
	}
}
