package tictim.hearthstones.client.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import tictim.hearthstones.tavern.TavernType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class TavernModelCache{
	private final Map<TavernType, IModel> tavernclothModel;
	private final IModel bodyModel;
	private final VertexFormat format;
	private final EnumFacing facing;

	private final Map<TavernType, Map<IBlockState, IBakedModel>> combinedModels = new EnumMap<>(TavernType.class);
	private final Map<IBlockState, BakedModelTavernBody> tavernBodyModel = new HashMap<>();
	private final Map<TavernType, IBakedModel> bakedTavernclothModel = new EnumMap<>(TavernType.class);

	private IBakedModel bakedBodyModel;

	public TavernModelCache(Map<TavernType, IModel> tavernclothModel, IModel bodyModel, VertexFormat format, EnumFacing facing){
		this.tavernclothModel = tavernclothModel;
		this.bodyModel = bodyModel;
		this.format = format;
		this.facing = facing;
	}

	public IBakedModel getModel(TavernType type, IBlockState skin){
		return this.combinedModels.computeIfAbsent(type, k -> new HashMap<>())
				.computeIfAbsent(skin, s -> computeCombinedModel(type, s));
	}

	private IBakedModel getClothModel(TavernType type){
		return bakedTavernclothModel.computeIfAbsent(type, t -> bake(tavernclothModel.get(t)));
	}

	private BakedModelTavernBody getSkinModel(IBlockState skin){
		BakedModelTavernBody model = tavernBodyModel.get(skin);
		if(model==null){
			if(bakedBodyModel==null) this.bakedBodyModel = bake(bodyModel);
			tavernBodyModel.put(skin, model = new BakedModelTavernBody(this.bakedBodyModel, skin));
		}
		return model;
	}

	private IBakedModel bake(IModel model){
		TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
		return model.bake(composeModelState(model.getDefaultState()),
				format, loc -> textureMap.getAtlasSprite(loc.toString()));
	}

	private IModelState composeModelState(IModelState modelState){
		return modelState instanceof TRSRTransformation ?
				TRSRTransformation.from(facing).compose((TRSRTransformation)modelState) :
				modelState;
	}

	protected IBakedModel computeCombinedModel(TavernType type, IBlockState skin){
		return new BakedModelCombined(getClothModel(type), getSkinModel(skin));
	}
}
