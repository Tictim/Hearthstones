package tictim.hearthstones.client.model;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BakedQuadRetextured;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.config.ModCfg;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BakedModelTavernBody implements IBakedModel{
	private final IBakedModel bodyModel;
	private final IBakedModel textureModel;
	private final IBlockState textureState;
	private final Map<EnumFacing, TextureAtlasSprite> textures = new EnumMap<>(EnumFacing.class);
	@Nullable
	private final TextureAtlasSprite nullTexture;

	public static BakedModelTavernBody createFromState(IBakedModel bodyModel, IBlockState textureState, EnumFacing facing){
		IProperty<EnumFacing> p = getApplicableFacingProperty(textureState, facing);
		if(p!=null) textureState = textureState.withProperty(p, facing);
		return new BakedModelTavernBody(bodyModel, Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(textureState), textureState);
	}
	@SuppressWarnings("unchecked") @Nullable private static IProperty<EnumFacing> getApplicableFacingProperty(IBlockState textureState, EnumFacing facing){
		for(IProperty<?> p : textureState.getPropertyKeys()){
			if(p.getName().equals("facing")&&p.getValueClass()==EnumFacing.class&&p.getAllowedValues().contains(facing)){
				return (IProperty<EnumFacing>)p;
			}
		}
		return null;
	}

	public BakedModelTavernBody(IBakedModel bodyModel, IBakedModel texture, IBlockState textureState){
		this.bodyModel = bodyModel;
		this.textureModel = texture;
		this.textureState = textureState;
		for(EnumFacing f : EnumFacing.values()) textures.put(f, getSpriteForFacing(f));
		this.nullTexture = getSpriteForFacing(null);
	}

	@Nullable private TextureAtlasSprite getSpriteForFacing(@Nullable EnumFacing facing){
		List<BakedQuad> quads = textureModel.getQuads(textureState, facing, 0);
		switch(quads.size()){
			case 0:
				if(facing!=null&&ModCfg.logModelWarnings)
					Hearthstones.LOGGER.warn("It seems like {} side of Texture Model {} is missing.", facing.name().toLowerCase(), textureState);
				return null;
			default:
				if(ModCfg.logModelWarnings)
					Hearthstones.LOGGER.warn("It seems like {} side of Texture Model {} has multiple quads.", facing==null ? "null" : facing.name().toLowerCase(), textureState);
			case 1:
				return quads.get(0).getSprite();
		}
	}

	@Override public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
		return bodyModel.getQuads(state, side, rand)
				.stream()
				.map(q -> new BakedQuadRetextured(q, getSprite(q)))
				.collect(Collectors.toList());
	}

	private TextureAtlasSprite getSprite(BakedQuad quad){
		if(quad.getFace()==null) return nullTexture==null ? quad.getSprite() : nullTexture;
		TextureAtlasSprite sprite = textures.get(quad.getFace());
		return sprite==null ? quad.getSprite() : sprite;
	}

	@Override public boolean isAmbientOcclusion(){
		return bodyModel.isAmbientOcclusion();
	}
	@Override public boolean isGui3d(){
		return bodyModel.isGui3d();
	}
	@Override public boolean isBuiltInRenderer(){
		return false;
	}
	@Override public TextureAtlasSprite getParticleTexture(){
		return textureModel.getParticleTexture();
	}
	@Override public ItemOverrideList getOverrides(){
		return bodyModel.getOverrides();
	}
}