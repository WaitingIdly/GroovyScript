package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.GasRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.OxidationRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ChemicalOxidizer extends VirtualizedMekanismRegistry<OxidationRecipe> {

    public ChemicalOxidizer() {
        super(RecipeHandler.Recipe.CHEMICAL_OXIDIZER, VirtualizedRegistry.generateAliases("Oxidizer"));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public OxidationRecipe add(IIngredient ingredient, GasStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Oxidizer recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(Mekanism.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        output = output.copy();
        OxidationRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            OxidationRecipe recipe = new OxidationRecipe(itemStack.copy(), output);
            if (recipe1 == null) recipe1 = recipe;
            recipeRegistry.put(recipe);
            addScripted(recipe);
        }
        return recipe1;
    }

    public boolean removeByInput(IIngredient ingredient) {
        if (IngredientHelper.isEmpty(ingredient)) {
            removeError("input must not be empty");
            return false;
        }
        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            OxidationRecipe recipe = recipeRegistry.get().remove(new ItemStackInput(itemStack));
            if (recipe != null) {
                addBackup(recipe);
                found = true;
            }
        }
        if (!found) {
            removeError("could not find recipe for {}", ingredient);
        }
        return found;
    }

    public static class RecipeBuilder extends GasRecipeBuilder<OxidationRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Chemical Oxidizer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg);
            validateGases(msg, 0, 0, 1, 1);
        }

        @Override
        public @Nullable OxidationRecipe register() {
            if (!validate()) return null;
            OxidationRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                OxidationRecipe r = new OxidationRecipe(itemStack.copy(), gasOutput.get(0));
                if (recipe == null) recipe = r;
                ModSupport.MEKANISM.get().chemicalOxidizer.add(r);
            }
            return recipe;
        }
    }
}
