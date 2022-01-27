package tictim.hearthstones.contents.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import tictim.hearthstones.contents.ModItems;
import tictim.hearthstones.contents.ModRecipes;
import tictim.hearthstones.contents.item.hearthstone.TavernWaypointBinderItem;
import tictim.hearthstones.contents.item.hearthstone.TavernWaypointItem;
import tictim.hearthstones.tavern.Tavern;
import tictim.hearthstones.tavern.TavernRecord;

import java.util.ArrayList;
import java.util.List;

public class BinderRecipe implements CraftingRecipe{
	private final ResourceLocation id;

	public BinderRecipe(ResourceLocation id){
		this.id = id;
	}

	@Override public boolean matches(CraftingContainer container, Level level){
		boolean hasBook = false;
		boolean hasWaypoint = false;

		for(int i = container.getContainerSize(); i>0; i--){
			ItemStack stack = container.getItem(i);
			if(stack.isEmpty()) continue;
			Item item = stack.getItem();
			if(item==Items.BOOK||item==ModItems.WAYPOINT_BINDER.get()){
				if(hasBook) return false;
				hasBook = true;
			}else if(item==ModItems.WAYPOINT.get())
				hasWaypoint = true;
			else return false;
		}
		return hasBook&&hasWaypoint;
	}
	@Override public ItemStack assemble(CraftingContainer container){
		boolean hasBook = false;
		int waypoints = 0;
		ItemStack book = ItemStack.EMPTY;
		List<Tavern> taverns = new ArrayList<>();

		for(int i = container.getContainerSize(); i>0; i--){
			ItemStack stack = container.getItem(i);
			if(stack.isEmpty()) continue;
			Item item = stack.getItem();
			if(item==Items.BOOK){
				if(hasBook) return ItemStack.EMPTY;
				hasBook = true;
				book = stack;
			}else if(item==ModItems.WAYPOINT_BINDER.get()){
				if(hasBook) return ItemStack.EMPTY;
				hasBook = true;
			}else if(item==ModItems.WAYPOINT.get()){
				waypoints++;
				TavernRecord tavern = TavernWaypointItem.getTavern(stack);
				if(tavern!=null) taverns.add(tavern);
			}else return ItemStack.EMPTY;
		}

		ItemStack newStack = book.isEmpty() ? new ItemStack(ModItems.WAYPOINT_BINDER.get()) : book.copy();
		TavernWaypointBinderItem.Data data = TavernWaypointBinderItem.data(newStack);
		if(data==null||Integer.MAX_VALUE-waypoints-data.getWaypoints()<0)
			return ItemStack.EMPTY;
		data.setWaypoints(data.getWaypoints()+waypoints);
		for(Tavern t : taverns)
			data.memory.addOrUpdate(t);
		return newStack;
	}
	@Override public boolean canCraftInDimensions(int width, int height){
		return width*height>=2;
	}
	private final ItemStack resultItem = new ItemStack(ModItems.WAYPOINT_BINDER.get());
	@Override public ItemStack getResultItem(){
		return resultItem;
	}
	@Override public ResourceLocation getId(){
		return id;
	}
	@Override public RecipeSerializer<?> getSerializer(){
		return ModRecipes.BINDER_RECIPE.get();
	}
}
