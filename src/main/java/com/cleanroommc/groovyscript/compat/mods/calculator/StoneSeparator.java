package com.cleanroommc.groovyscript.compat.mods.calculator;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import org.jetbrains.annotations.Nullable;
import sonar.calculator.mod.common.recipes.CalculatorRecipe;
import sonar.calculator.mod.common.recipes.StoneSeparatorRecipes;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.ArrayList;
import java.util.Collection;

@RegistryDescription
public class StoneSeparator extends StandardListRegistry<CalculatorRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'), item('minecraft:diamond'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<CalculatorRecipe> getRecipes() {
        return StoneSeparatorRecipes.instance().getRecipes();
    }

    @MethodDescription(example = @Example("item('minecraft:gold_ore')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> r.recipeInputs.stream().map(ISonarRecipeObject::getJEIValue).flatMap(Collection::stream).anyMatch(input) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('calculator:reinforcedironingot')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> r.recipeOutputs.stream().map(ISonarRecipeObject::getJEIValue).flatMap(Collection::stream).anyMatch(output) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 2))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CalculatorRecipe> {

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Calculator Stone Separator Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 2, 2);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CalculatorRecipe register() {
            if (!validate()) return null;

            CalculatorRecipe recipe = StoneSeparatorRecipes.instance()
                    .buildDefaultRecipe(Calculator.toSonarRecipeObjectList(input), output, new ArrayList<>(), false);

            ModSupport.CALCULATOR.get().stoneSeparator.add(recipe);
            return recipe;
        }
    }
}
