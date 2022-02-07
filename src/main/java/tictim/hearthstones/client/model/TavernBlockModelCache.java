package tictim.hearthstones.client.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import tictim.hearthstones.contents.tileentity.TavernTile;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernType;

import java.util.EnumMap;
import java.util.Map;

public class TavernBlockModelCache{
	private static final ResourceLocation[] DESTROY_STAGES = {
			new ResourceLocation("blocks/destroy_stage_0"),
			new ResourceLocation("blocks/destroy_stage_1"),
			new ResourceLocation("blocks/destroy_stage_2"),
			new ResourceLocation("blocks/destroy_stage_3"),
			new ResourceLocation("blocks/destroy_stage_4"),
			new ResourceLocation("blocks/destroy_stage_5"),
			new ResourceLocation("blocks/destroy_stage_6"),
			new ResourceLocation("blocks/destroy_stage_7"),
			new ResourceLocation("blocks/destroy_stage_8"),
			new ResourceLocation("blocks/destroy_stage_9")
	};

	private final Map<TavernType, IModel> tavernclothModel;
	private final IModel bodyModel;
	private final VertexFormat format;
	private final Map<EnumFacing, TavernModelCache> cacheMap = new EnumMap<>(EnumFacing.class);
	@SuppressWarnings("unchecked")
	private final Map<EnumFacing, TavernModelCache>[] destroyStageCacheMaps = new Map[10];

	public TavernBlockModelCache(Map<TavernType, IModel> tavernclothModel, IModel bodyModel, VertexFormat format){
		this.tavernclothModel = tavernclothModel;
		this.bodyModel = bodyModel;
		this.format = format;
		for(int i = 0; i<destroyStageCacheMaps.length; i++) destroyStageCacheMaps[i] = new EnumMap<>(EnumFacing.class);
	}

	public IBakedModel getModel(EnumFacing facing, TavernTile te){
		return getCache(facing).getModel(te.type(), te.skin()!=null ? te.skin() : Tavern.getDefaultSkin());
	}
	public IBakedModel getDestroyStageModel(int destroyStage, EnumFacing facing, TavernTile te){
		if(destroyStage<0) return getModel(facing, te);
		TavernModelCache cache = getCache(facing);
		return destroyStageCacheMaps[Math.min(destroyStage, 9)]
				.computeIfAbsent(facing, f -> new TavernModelCache(tavernclothModel, bodyModel, format, f){
					@Override
					protected IBakedModel computeCombinedModel(TavernType type, IBlockState skin){
						return new BakedModelDestroyStage(cache.getModel(type, skin),
								Minecraft.getMinecraft()
										.getTextureMapBlocks()
										.getAtlasSprite(DESTROY_STAGES[destroyStage].toString()), 0xFFFFFF80);
					}
				}).getModel(te.type(), te.skin()!=null ? te.skin() : Tavern.getDefaultSkin());
	}

	private TavernModelCache getCache(EnumFacing facing){
		return cacheMap.computeIfAbsent(facing, f -> new TavernModelCache(tavernclothModel, bodyModel, format, f));
	}
}
