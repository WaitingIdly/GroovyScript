package com.cleanroommc.groovyscript.compat.mods.iceandfire;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.github.alexthe666.iceandfire.recipe.DragonForgeRecipe;
import com.github.alexthe666.iceandfire.recipe.IafRecipeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class IceForge extends StandardListRegistry<DragonForgeRecipe> {

    @Override
    public Collection<DragonForgeRecipe> getRecipes() {
        return IafRecipeRegistry.ICE_FORGE_RECIPES;
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot'), item('minecraft:gold_ingot')).output(item('minecraft:clay'))"),
            @Example(".input(item('minecraft:diamond'), item('minecraft:gold_ingot')).output(item('minecraft:clay'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = {
            @Example("item('minecraft:iron_ingot')"), @Example(value = "item('iceandfire:ice_dragon_blood')", commented = true)
    })
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> (input.test(r.getInput()) || input.test(r.getBlood())) && doAddBackup(r));
    }

    @MethodDescription(example = @Example(value = "item('iceandfire:dragonsteel_ice_ingot')", commented = true))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> output.test(r.getOutput()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 2))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<DragonForgeRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Ice And Fire Ice Forge recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @NotNull Collection<DragonForgeRecipe> register() {
            if (!validate()) return Collections.emptyList();
            List<DragonForgeRecipe> list = new ArrayList<>();
            for (var inputStack : input.get(0).getMatchingStacks()) {
                for (var blood : input.get(1).getMatchingStacks()) {
                    var recipe = new DragonForgeRecipe(inputStack, blood, output.get(0));
                    list.add(recipe);
                    ModSupport.ICE_AND_FIRE.get().iceForge.add(recipe);
                }
            }
            return list;
        }
    }
}
