package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import reborncore.api.praescriptum.recipes.Recipe;
import reborncore.api.praescriptum.recipes.RecipeHandler;

import java.util.Arrays;
import java.util.Collection;

public abstract class AbstractPraescriptumRegistry extends StandardListRegistry<Recipe> {

    abstract RecipeHandler handler();

    @Override
    public Collection<Recipe> getRecipes() {
        return handler().getRecipes();
    }

    @MethodDescription
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(recipe -> recipe.getInputIngredients().stream().map(x -> x.ingredient).anyMatch(x -> {
            if (x instanceof ItemStack itemStack) return input.test(itemStack);
            if (x instanceof FluidStack fluidStack) return input.test(fluidStack);
            if (x instanceof String s) return Arrays.stream(new OreDictIngredient(s).getMatchingStacks()).anyMatch(input);
            return false;
        }) && doAddBackup(recipe));
    }

    @MethodDescription
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(recipe -> (Arrays.stream(recipe.getItemOutputs()).anyMatch(output) || Arrays.stream(recipe.getFluidOutputs()).anyMatch(output::test)) && doAddBackup(recipe));
    }

}
