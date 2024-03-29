package tictim.hearthstones.contents;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tictim.hearthstones.contents.enchantment.EnchantQuickcast;
import tictim.hearthstones.contents.item.hearthstone.HearthstoneItem;

import static tictim.hearthstones.Hearthstones.MODID;

public final class ModEnchantments{
	private ModEnchantments(){}

	public static final DeferredRegister<Enchantment> REGISTER = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);

	public static final EnchantmentCategory HEARTHSTONE = EnchantmentCategory.create("HEARTHSTONE", i -> i instanceof HearthstoneItem);

	public static final RegistryObject<Enchantment> QUICKCAST = REGISTER.register("quickcast", () -> new EnchantQuickcast(HEARTHSTONE));
}
