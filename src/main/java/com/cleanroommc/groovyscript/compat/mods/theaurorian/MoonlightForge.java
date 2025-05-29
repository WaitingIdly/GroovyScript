package com.cleanroommc.groovyscript.compat.mods.theaurorian;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.shiroroku.theaurorian.Recipes.MoonlightForgeRecipe;
import com.shiroroku.theaurorian.Recipes.MoonlightForgeRecipeHandler;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class MoonlightForge extends StandardListRegistry<MoonlightForgeRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:stone_sword'), item('minecraft:diamond')).output(item('minecraft:diamond_sword'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<MoonlightForgeRecipe> getRecipes() {
        return MoonlightForgeRecipeHandler.allRecipes;
    }

    @MethodDescription(example = @Example("item('theaurorian:moonstonesword'), item('theaurorian:aurorianiteingot')"))
    public boolean removeByInput(IIngredient input, IIngredient catalyst) {
        return getRecipes().removeIf(r -> {
            if (input.test(r.getInput1()) && catalyst.test(r.getInput2())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('theaurorian:queenschipper')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 2))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<MoonlightForgeRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Moonlight Forge recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @NotNull List<MoonlightForgeRecipe> register() {
            if (!validate()) return Collections.emptyList();
            List<MoonlightForgeRecipe> list = new ArrayList<>();
            for (ItemStack input1 : input.get(0).getMatchingStacks()) {
                for (ItemStack input2 : input.get(1).getMatchingStacks()) {
                    var recipe = new MoonlightForgeRecipe(input1, input2, output.get(0));
                    list.add(recipe);
                    ModSupport.THE_AURORIAN.get().moonlightForge.add(recipe);
                }
            }
            return list;
        }
    }
}
