package com.cleanroommc.groovyscript.compat.mods.primaltech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.primal_tech.StoneAnvilRecipesAccessor;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import primal_tech.recipes.StoneAnvilRecipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class StoneAnvil extends StandardListRegistry<StoneAnvilRecipes> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond') * 4)")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<StoneAnvilRecipes> getRecipes() {
        return StoneAnvilRecipesAccessor.getRecipes();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public Collection<StoneAnvilRecipes> add(ItemStack output, IIngredient input) {
        return recipeBuilder()
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(example = @Example(value = "item('primal_tech:flint_block')", commented = true))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(recipe -> {
            if (input.test(recipe.getInput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:flint')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(recipe -> {
            if (output.test(recipe.getOutput())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<StoneAnvilRecipes> {

        @Override
        public String getErrorMsg() {
            return "Error adding Primal Tech Stone Anvil recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @NotNull Collection<StoneAnvilRecipes> register() {
            if (!validate()) return Collections.emptyList();
            List<StoneAnvilRecipes> list = new ArrayList<>();
            for (ItemStack matchingStack : input.get(0).getMatchingStacks()) {
                var recipe = StoneAnvilRecipesAccessor.createStoneAnvilRecipes(output.get(0), matchingStack);
                list.add(recipe);
                ModSupport.PRIMAL_TECH.get().stoneAnvil.add(recipe);
            }
            return list;
        }
    }
}
