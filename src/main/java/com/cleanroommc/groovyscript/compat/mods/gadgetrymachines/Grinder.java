package com.cleanroommc.groovyscript.compat.mods.gadgetrymachines;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.gadgetry.Gadgetry;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import epicsquid.gadgetry.machines.recipe.GrindingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Grinder extends StandardListRegistry<GrindingRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond') * 5).output(item('minecraft:clay'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond') * 3)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void afterScriptLoad() {
        getRecipes().forEach(x -> GrindingRecipe.grindables.add(x.inputs.get(0)));
    }

    @Override
    public Collection<GrindingRecipe> getRecipes() {
        return GrindingRecipe.recipes;
    }

    @MethodDescription(example = @Example("item('minecraft:coal_ore')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> Gadgetry.isMatch(input, r.inputs) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("item('minecraft:sand')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<GrindingRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Gadgetry Grinder recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable GrindingRecipe register() {
            if (!validate()) return null;
            GrindingRecipe recipe = null;
            for (var item_input : Gadgetry.toGadgetryInput(input.getOrEmpty(0))) {
                recipe = new GrindingRecipe(output.get(0), item_input);
                ModSupport.GADGETRY_MACHINES.get().grinder.add(recipe);
            }
            return recipe;
        }
    }
}
