package tictim.hearthstones.contents.recipes;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.config.ModCfg;

public class EasyModeCondition implements ICondition{
	private static final ResourceLocation ID = new ResourceLocation(Hearthstones.MODID, "easy_mode");

	@Override
	public ResourceLocation getID(){
		return ID;
	}

	@Override
	public boolean test(IContext context){
		return ModCfg.easyMode();
	}

	public enum Serializer implements IConditionSerializer<EasyModeCondition>{
		INSTANCE;

		@Override
		public void write(JsonObject json, EasyModeCondition value){}

		@Override
		public EasyModeCondition read(JsonObject json){
			return new EasyModeCondition();
		}

		@Override
		public ResourceLocation getID(){
			return ID;
		}
	}
}
