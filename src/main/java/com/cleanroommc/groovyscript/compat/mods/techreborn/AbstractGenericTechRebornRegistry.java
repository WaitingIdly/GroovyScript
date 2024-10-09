package com.cleanroommc.groovyscript.compat.mods.techreborn;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import reborncore.api.recipe.IBaseRecipeType;
import reborncore.api.recipe.RecipeHandler;
import reborncore.common.recipes.RecipeTranslator;

import java.util.Collection;

public abstract class AbstractGenericTechRebornRegistry extends StandardListRegistry<IBaseRecipeType> {

    abstract String reference();

    @Override
    public Collection<IBaseRecipeType> getRecipes() {
        return RecipeHandler.getRecipeClassFromName(reference());
    }

    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(recipe -> recipe.getInputs().stream().map(RecipeTranslator::getStackFromObject).anyMatch(input) && doAddBackup(recipe));
    }

    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(recipe -> recipe.getOutputs().stream().anyMatch(output) && doAddBackup(recipe));
    }

}
