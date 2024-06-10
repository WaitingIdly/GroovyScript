package com.cleanroommc.groovyscript.compat.mods.calculator;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import sonar.calculator.mod.common.recipes.CalculatorRecipe;
import sonar.calculator.mod.common.recipes.ProcessingChamberRecipes;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class ProcessingChamber extends VirtualizedRegistry<CalculatorRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(ProcessingChamberRecipes.instance().getRecipes()::remove);
        restoreFromBackup().forEach(ProcessingChamberRecipes.instance().getRecipes()::add);
    }

    public void add(CalculatorRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ProcessingChamberRecipes.instance().getRecipes().add(recipe);
    }

    public boolean remove(CalculatorRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ProcessingChamberRecipes.instance().getRecipes().remove(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('calculator:circuitdamaged:4')"))
    public boolean removeByInput(IIngredient input) {
        return ProcessingChamberRecipes.instance().getRecipes().removeIf(r -> {
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

    @MethodDescription(example = @Example("item('calculator:circuitboard:1')"))
    public boolean removeByOutput(IIngredient output) {
        return ProcessingChamberRecipes.instance().getRecipes().removeIf(r -> {
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
        ProcessingChamberRecipes.instance().getRecipes().forEach(this::addBackup);
        ProcessingChamberRecipes.instance().getRecipes().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<CalculatorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ProcessingChamberRecipes.instance().getRecipes())
                .setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CalculatorRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Calculator Processing Chamber Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @NotNull List<CalculatorRecipe> register() {
            if (!validate()) return Collections.emptyList();

            CalculatorRecipe recipe = ProcessingChamberRecipes.instance()
                    .buildDefaultRecipe(Calculator.toSonarRecipeObjectList(input), output, new ArrayList<>(), false);

            ModSupport.CALCULATOR.get().processingChamber.add(recipe);
            return Collections.singletonList(recipe);
        }
    }
}
