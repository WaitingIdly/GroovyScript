package com.cleanroommc.groovyscript.compat.mods.gadgetry;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientList;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.google.common.collect.Lists;
import epicsquid.gadgetry.core.recipe.AlloyRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RegistryDescription
public class AlloyFurnace extends StandardListRegistry<AlloyRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:diamond') * 5, item('minecraft:gold_ingot'), item('minecraft:gold_block')).output(item('minecraft:clay'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void afterScriptLoad() {
        AlloyRecipe.metals.clear();
        AlloyRecipe.additives1.clear();
        AlloyRecipe.additives2.clear();
        getRecipes().forEach(x -> {
            AlloyRecipe.metals.add(x.inputs.get(0));
            AlloyRecipe.additives1.add(x.inputs.get(1));
            AlloyRecipe.additives2.add(x.inputs.get(2));
        });
    }

    @Override
    public Collection<AlloyRecipe> getRecipes() {
        return AlloyRecipe.recipes;
    }

    @MethodDescription(example = @Example("item('minecraft:iron_ingot')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> Gadgetry.isMatch(input, r.inputs) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('gadgetrycore:redmetal_ingot')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 3))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<AlloyRecipe> {

        private static @NotNull List<List<Object>> convertInputs(@NotNull IngredientList<IIngredient> inputs) {
            List<List<Object>> entries = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                entries.add(Gadgetry.toGadgetryInput(inputs.getOrEmpty(i)));
            }
            return Lists.cartesianProduct(entries);
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Gadgetry Alloyer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 3, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable AlloyRecipe register() {
            if (!validate()) return null;
            AlloyRecipe recipe = null;
            for (var objects : convertInputs(input)) {
                recipe = new AlloyRecipe(output.get(0), objects.toArray());
                ModSupport.GADGETRY.get().alloyFurnace.add(recipe);
            }
            return recipe;
        }
    }
}
