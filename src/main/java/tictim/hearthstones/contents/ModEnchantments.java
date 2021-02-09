package tictim.hearthstones.contents;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import tictim.hearthstones.enchantment.EnchantQuickcast;
import tictim.hearthstones.logic.HearthstoneItem;

import static tictim.hearthstones.Hearthstones.MODID;

public final class ModEnchantments{
	private ModEnchantments(){}

	public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);

	public static final EnchantmentType HEARTHSTONE = EnchantmentType.create("HEARTHSTONE", i -> i instanceof HearthstoneItem);

	public static final RegistryObject<Enchantment> QUICKCAST = ENCHANTMENTS.register("quickcast", () -> new EnchantQuickcast(HEARTHSTONE));
}
