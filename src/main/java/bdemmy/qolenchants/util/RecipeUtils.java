package bdemmy.qolenchants.util;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.world.ServerWorld;

public class RecipeUtils {
    public static ItemStack getFurnaceRecipe(ItemStack input, ServerWorld world){
        RecipeManager manager = world.getServer().getRecipeManager();
        SimpleInventory tempInv = new SimpleInventory(input);

        Recipe<?> recipe = manager.getFirstMatch(RecipeType.SMELTING, tempInv, world).orElse(null);
        if (recipe != null){
            return recipe.getOutput();
        }
        return ItemStack.EMPTY;
    }
}
