package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.BottlingMachineRecipe;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class BottlingMachine extends VirtualizedRegistry<BottlingMachineRecipe> {

    public BottlingMachine() {
        super("Bottling", "bottling");
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> BottlingMachineRecipe.recipeList.removeIf(r -> r == recipe));
        BottlingMachineRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(BottlingMachineRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            BottlingMachineRecipe.recipeList.add(recipe);
        }
    }

    public BottlingMachineRecipe add(ItemStack output, IIngredient input, FluidStack fluidInput) {
        BottlingMachineRecipe recipe = new BottlingMachineRecipe(output.copy(), ImmersiveEngineering.toIngredientStack(input), fluidInput);
        add(recipe);
        return recipe;
    }

    public boolean remove(BottlingMachineRecipe recipe) {
        if (BottlingMachineRecipe.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public void removeByOutput(ItemStack output) {
        for (int i = 0; i < BottlingMachineRecipe.recipeList.size(); i++) {
            BottlingMachineRecipe recipe = BottlingMachineRecipe.recipeList.get(i);
            if (ApiUtils.stackMatchesObject(output, recipe.output, true)) {
                addBackup(recipe);
                BottlingMachineRecipe.recipeList.remove(i);
                break;
            }
        }
    }

    public void removeByInput(ItemStack input, FluidStack inputFluid) {
        BottlingMachineRecipe recipe = BottlingMachineRecipe.findRecipe(input, inputFluid);
        if (recipe != null) {
            addBackup(recipe);
            BottlingMachineRecipe.recipeList.remove(recipe);
        }
    }

    public void removeByInput(ItemStack input) {
        for (BottlingMachineRecipe recipe : BottlingMachineRecipe.recipeList) {
            if (recipe.input.matches(input)) {
                addBackup(recipe);
                BottlingMachineRecipe.recipeList.remove(recipe);
            }
        }
    }

    public SimpleObjectStream<BottlingMachineRecipe> stream() {
        return new SimpleObjectStream<>(BottlingMachineRecipe.recipeList).setRemover(this::remove);
    }

    public void removeAll() {
        BottlingMachineRecipe.recipeList.forEach(this::addBackup);
        BottlingMachineRecipe.recipeList.clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<BottlingMachineRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Bottling recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
        }

        @Override
        public @Nullable BottlingMachineRecipe register() {
            if (!validate()) return null;
            return ModSupport.IMMERSIVE_ENGINEERING.get().bottlingMachine.add(output.get(0), input.get(0), fluidInput.get(0));
        }
    }
}