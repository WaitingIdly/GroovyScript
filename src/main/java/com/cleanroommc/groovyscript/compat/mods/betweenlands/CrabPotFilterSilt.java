package com.cleanroommc.groovyscript.compat.mods.betweenlands;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thebetweenlands.CrabPotFilterRecipeSiltAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import org.jetbrains.annotations.NotNull;
import thebetweenlands.api.recipes.ICrabPotFilterRecipeSilt;
import thebetweenlands.common.recipe.misc.CrabPotFilterRecipeSilt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class CrabPotFilterSilt extends StandardListRegistry<ICrabPotFilterRecipeSilt> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<ICrabPotFilterRecipeSilt> getRecipes() {
        return CrabPotFilterRecipeSiltAccessor.getRecipes();
    }

    @MethodDescription(example = @Example("item('thebetweenlands:mud')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> input.test(r.getInput()) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('thebetweenlands:mud')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput(r.getInput())) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ICrabPotFilterRecipeSilt> {

        @Override
        public String getErrorMsg() {
            return "Error adding Betweenlands Crab Pot Filter Silt recipe";
        }

        @Override
        @GroovyBlacklist
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @NotNull Collection<ICrabPotFilterRecipeSilt> register() {
            if (!validate()) return Collections.emptyList();
            List<ICrabPotFilterRecipeSilt> list = new ArrayList<>();
            for (var stack : input.get(0).getMatchingStacks()) {
                ICrabPotFilterRecipeSilt recipe = new CrabPotFilterRecipeSilt(output.get(0), stack);
                list.add(recipe);
                ModSupport.BETWEENLANDS.get().crabPotFilterSilt.add(recipe);
            }
            return list;
        }
    }
}
