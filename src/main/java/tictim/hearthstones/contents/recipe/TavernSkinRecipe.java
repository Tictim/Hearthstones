package tictim.hearthstones.contents.recipe;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import tictim.hearthstones.Hearthstones;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.tavern.Tavern;

import javax.annotation.Nullable;

public class TavernSkinRecipe extends SpecialRecipe{
	private final Ingredient tavernIngredient;

	public TavernSkinRecipe(Ingredient tavernIngredient){
		this.tavernIngredient = tavernIngredient;
	}

	@Override public boolean matches(InventoryCrafting inv, World world){
		boolean tavern = false;
		boolean skin = false;

		for(int i = 0; i<inv.getSizeInventory(); i++){
			ItemStack stack = inv.getStackInSlot(i);
			if(stack.isEmpty()) continue;

			if(tavernIngredient.test(stack)){
				if(tavern) return false;
				else tavern = true;
			}else if(getSkinFromItem(stack)!=null){
				if(skin) return false;
				else skin = true;
			}else return false;
		}
		return tavern&&skin;
	}

	@Override public ItemStack getCraftingResult(InventoryCrafting inv){
		ItemStack tavern = null;
		IBlockState skin = null;

		for(int i = 0; i<inv.getSizeInventory(); i++){
			ItemStack stack = inv.getStackInSlot(i);
			if(stack.isEmpty()) continue;

			if(tavernIngredient.test(stack)){
				if(tavern==null) tavern = stack;
				else return ItemStack.EMPTY;
			}else{
				IBlockState skinFromItem = getSkinFromItem(stack);
				if(skinFromItem!=null){
					if(skin==null) skin = skinFromItem;
					else return ItemStack.EMPTY;
				}else return ItemStack.EMPTY;
			}
		}

		if(tavern==null||skin==null) return ItemStack.EMPTY;

		ItemStack t2 = tavern.copy();
		t2.setCount(1);
		NBTTagCompound nbt = t2.getOrCreateSubCompound("BlockEntityTag");
		Tavern.writeSkin(nbt, skin);

		return t2;
	}

	@Override public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv){
		NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
		for(int i = 0; i<ret.size(); i++){
			ItemStack stack = inv.getStackInSlot(i);
			if(!stack.isEmpty()){
				if(tavernIngredient.test(stack)) ret.set(i, ForgeHooks.getContainerItem(stack));
				else{
					ItemStack copy = stack.copy();
					copy.setCount(1);
					ret.set(i, copy);
				}
			}
		}
		return ret;
	}

	@Override protected int minimumSize(){
		return 2;
	}
	@Override protected ItemStack createRecipeOutput(){
		return new ItemStack(ModItems.TAVERN);
	}
	@Override protected Ingredient[] createIngredients(){
		return new Ingredient[]{
				Ingredient.fromItem(ModItems.TAVERN),
				TavernSkinIngredient.of()
		};
	}

	@Nullable public static IBlockState getSkinFromItem(ItemStack stack){
		Item item = stack.getItem();
		if(item instanceof ItemBlock){
			Block block = ((ItemBlock)item).getBlock();
			try{
				@SuppressWarnings("deprecation")
				IBlockState state = block.getStateFromMeta(stack.getMetadata());
				if(state.getRenderType()==EnumBlockRenderType.MODEL) return state;
			}catch(RuntimeException ex){
				// I don't know why the hell this shit is here, but now I cannot trust any of the shits above after seeing this super elaborate and noncryptic try-catch shit
				Hearthstones.LOGGER.warn("Unexpected error; ItemStack {}, Block {}", stack, block.getRegistryName(), ex);
			}
		}
		return null;
	}
}
