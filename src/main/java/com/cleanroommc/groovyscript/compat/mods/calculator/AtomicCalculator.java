package com.cleanroommc.groovyscript.compat.mods.calculator;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import sonar.calculator.mod.common.recipes.AtomicCalculatorRecipes;
import sonar.calculator.mod.common.recipes.CalculatorRecipe;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.ArrayList;

@RegistryDescription
public class AtomicCalculator extends VirtualizedRegistry<CalculatorRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay')).output(item('minecraft:gold_ingot') * 4)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(AtomicCalculatorRecipes.instance().getRecipes()::remove);
        restoreFromBackup().forEach(AtomicCalculatorRecipes.instance().getRecipes()::add);
    }

    public void add(CalculatorRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        AtomicCalculatorRecipes.instance().getRecipes().add(recipe);
    }

    public boolean remove(CalculatorRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        AtomicCalculatorRecipes.instance().getRecipes().remove(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:end_stone')"))
    public boolean removeByInput(IIngredient input) {
        return AtomicCalculatorRecipes.instance().getRecipes().removeIf(r -> {
            for (ISonarRecipeObject recipeInput : r.recipeInputs) {
                for (ItemStack itemStack : recipeInput.getJEIValue()) {
                    if (input.test(itemStack)) {
                        addBackup(r);
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('calculator:firediamond')"))
    public boolean removeByOutput(IIngredient output) {
        return AtomicCalculatorRecipes.instance().getRecipes().removeIf(r -> {
            for (ISonarRecipeObject recipeOutput : r.recipeOutputs) {
                for (ItemStack itemStack : recipeOutput.getJEIValue()) {
                    if (output.test(itemStack)) {
                        addBackup(r);
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        AtomicCalculatorRecipes.instance().getRecipes().forEach(this::addBackup);
        AtomicCalculatorRecipes.instance().getRecipes().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<CalculatorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(AtomicCalculatorRecipes.instance().getRecipes())
                .setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("3"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CalculatorRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Calculator Atomic Calculator Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 3, 3, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CalculatorRecipe register() {
            if (!validate()) return null;

            CalculatorRecipe recipe = AtomicCalculatorRecipes.instance()
                    .buildDefaultRecipe(Calculator.toSonarRecipeObjectList(input), output, new ArrayList<>(), false);

            ModSupport.CALCULATOR.get().atomicCalculator.add(recipe);
            return recipe;
        }
    }
}
