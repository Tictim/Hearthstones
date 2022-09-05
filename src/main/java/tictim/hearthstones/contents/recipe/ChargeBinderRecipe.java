package tictim.hearthstones.contents.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.World;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.contents.item.TavernBinderItem;
import tictim.hearthstones.contents.item.TavernWaypointItem;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernBinderData;
import tictim.hearthstones.tavern.TavernRecord;

import java.util.ArrayList;
import java.util.List;

public class ChargeBinderRecipe extends SpecialRecipe{
	@Override public boolean matches(InventoryCrafting inv, World world){
		boolean hasBook = false;
		boolean hasWaypoint = false;

		for(int i = inv.getSizeInventory()-1; i>=0; i--){
			ItemStack stack = inv.getStackInSlot(i);
			if(stack.isEmpty()) continue;
			Item item = stack.getItem();
			if(item==ModItems.WAYPOINT_BINDER){
				if(hasBook) return false;
				hasBook = true;
			}else if(item==ModItems.WAYPOINT)
				hasWaypoint = true;
			else return false;
		}
		return hasBook&&hasWaypoint;
	}

	@Override public ItemStack getCraftingResult(InventoryCrafting inv){
		int waypoints = 0;
		ItemStack book = ItemStack.EMPTY;
		List<Tavern> taverns = new ArrayList<>();

		for(int i = inv.getSizeInventory()-1; i>=0; i--){
			ItemStack stack = inv.getStackInSlot(i);
			if(stack.isEmpty()) continue;
			Item item = stack.getItem();
			if(item==ModItems.WAYPOINT_BINDER){
				if(!book.isEmpty()) return ItemStack.EMPTY;
				book = stack;
			}else if(item==ModItems.WAYPOINT){
				waypoints++;
				TavernRecord tavern = TavernWaypointItem.getTavern(stack);
				if(tavern!=null) taverns.add(tavern);
			}else return ItemStack.EMPTY;
		}

		if(book.isEmpty()||waypoints==0) return ItemStack.EMPTY;
		ItemStack newStack = book.copy();
		TavernBinderData data = TavernBinderItem.data(newStack);
		if(data==null||!data.addEmptyWaypoint(waypoints)) return ItemStack.EMPTY;
		for(Tavern t : taverns) data.addOrUpdateWaypoint(t);
		return newStack;
	}

	@Override protected int minimumSize(){
		return 2;
	}
	@Override protected ItemStack createRecipeOutput(){
		return new ItemStack(ModItems.WAYPOINT_BINDER);
	}
	@Override protected Ingredient[] createIngredients(){
		return new Ingredient[]{
				Ingredient.fromItem(ModItems.WAYPOINT_BINDER),
				Ingredient.fromItem(ModItems.WAYPOINT)
		};
	}
}
