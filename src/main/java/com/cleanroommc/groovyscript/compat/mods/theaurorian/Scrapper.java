package com.cleanroommc.groovyscript.compat.mods.theaurorian;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.shiroroku.theaurorian.Recipes.ScrapperRecipe;
import com.shiroroku.theaurorian.Recipes.ScrapperRecipeHandler;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class Scrapper extends StandardListRegistry<ScrapperRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:stone_sword')).output(item('minecraft:cobblestone'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<ScrapperRecipe> getRecipes() {
        return ScrapperRecipeHandler.allRecipes;
    }

    @MethodDescription(example = @Example("item('minecraft:iron_sword')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> {
            if (input.test(r.getInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('theaurorian:scrapaurorianite')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ScrapperRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Scrapper recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @NotNull Collection<ScrapperRecipe> register() {
            if (!validate()) return Collections.emptyList();
            List<ScrapperRecipe> list = new ArrayList<>();
            for (ItemStack input1 : input.get(0).getMatchingStacks()) {
                var recipe = new ScrapperRecipe(input1, output.get(0));
                list.add(recipe);
                ModSupport.THE_AURORIAN.get().scrapper.add(recipe);
            }
            return list;
        }
    }
}
