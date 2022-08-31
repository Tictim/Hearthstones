package tictim.hearthstones.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

import java.util.Set;

import static tictim.hearthstones.Hearthstones.MODID;
import static tictim.hearthstones.Hearthstones.NAME;

@SuppressWarnings("unused")
public class GuiFactory implements IModGuiFactory{
	@Override public void initialize(Minecraft minecraftInstance){}

	@Override public boolean hasConfigGui(){
		return true;
	}

	@Override public GuiScreen createConfigGui(GuiScreen parentScreen){
		return new GuiConfig(parentScreen, MODID, NAME);
	}

	@Override public Set<RuntimeOptionCategoryElement> runtimeGuiCategories(){
		return null;
	}
}
