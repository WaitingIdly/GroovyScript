package com.cleanroommc.groovyscript.compat.mods.calculator;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import org.jetbrains.annotations.Nullable;
import sonar.calculator.mod.common.recipes.AtomicCalculatorRecipes;
import sonar.calculator.mod.common.recipes.CalculatorRecipe;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.ArrayList;
import java.util.Collection;

@RegistryDescription
public class AtomicCalculator extends StandardListRegistry<CalculatorRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay')).output(item('minecraft:gold_ingot') * 4)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<CalculatorRecipe> getRecipes() {
        return AtomicCalculatorRecipes.instance().getRecipes();
    }

    @MethodDescription(example = @Example("item('minecraft:end_stone')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> r.recipeInputs.stream().map(ISonarRecipeObject::getJEIValue).flatMap(Collection::stream).anyMatch(input) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('calculator:firediamond')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> r.recipeOutputs.stream().map(ISonarRecipeObject::getJEIValue).flatMap(Collection::stream).anyMatch(output) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 3))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CalculatorRecipe> {

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

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
