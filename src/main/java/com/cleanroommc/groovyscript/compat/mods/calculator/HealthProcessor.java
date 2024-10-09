package com.cleanroommc.groovyscript.compat.mods.calculator;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import org.jetbrains.annotations.Nullable;
import sonar.calculator.mod.common.recipes.HealthProcessorRecipes;
import sonar.core.recipes.DefaultSonarRecipe;

import java.util.Arrays;
import java.util.Collection;

@RegistryDescription
public class HealthProcessor extends StandardListRegistry<DefaultSonarRecipe.Value> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay')).value(100)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<DefaultSonarRecipe.Value> getRecipes() {
        return HealthProcessorRecipes.instance().getRecipes();
    }

    @MethodDescription(example = @Example("item('minecraft:blaze_rod')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> r.recipeInputs.stream().map(ISonarRecipeObject::getJEIValue).flatMap(Collection::stream).anyMatch(input) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<DefaultSonarRecipe.Value> {

        @Property(comp = @Comp(gte = 1))
        private int value;

        @RecipeBuilderMethodDescription
        public RecipeBuilder value(int value) {
            this.value = value;
            return this;
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Calculator Health Processor Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg);
            msg.add(value <= 0, "value must be greater than or equal to 1, yet it was {}", value);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable DefaultSonarRecipe.Value register() {
            if (!validate()) return null;

            DefaultSonarRecipe.Value recipe = HealthProcessorRecipes.instance()
                    .buildDefaultRecipe(Calculator.toSonarRecipeObjectList(input), output, Arrays.asList(value), false);

            ModSupport.CALCULATOR.get().healthProcessor.add(recipe);
            return recipe;
        }
    }
}
