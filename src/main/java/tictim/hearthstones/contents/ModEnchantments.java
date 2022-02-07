package tictim.hearthstones.contents;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import tictim.hearthstones.contents.enchantment.EnchantQuickcast;
import tictim.hearthstones.contents.item.hearthstone.HearthstoneItem;

import static tictim.hearthstones.Hearthstones.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class ModEnchantments{
	public static final EnumEnchantmentType HEARTHSTONE = EnumHelper.addEnchantmentType("HEARTHSTONE", i -> i instanceof HearthstoneItem);

	@GameRegistry.ObjectHolder(MODID+":quickcast")
	public static final Enchantment QUICKCAST = null;

	@SubscribeEvent
	public static void registerEnchantments(RegistryEvent.Register<Enchantment> event){
		IForgeRegistry<Enchantment> registry = event.getRegistry();
		registry.register(new EnchantQuickcast(HEARTHSTONE).setRegistryName("quickcast").setName("hearthstones.quickcast"));
	}
}
