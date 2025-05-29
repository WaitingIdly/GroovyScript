package com.cleanroommc.groovyscript.compat.mods.cyclic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.lothrazar.cyclicmagic.CyclicContent;
import com.lothrazar.cyclicmagic.block.melter.RecipeMelter;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class Melter extends StandardListRegistry<RecipeMelter> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot')).fluidOutput(fluid('water') * 175)"),
            @Example(".input(ore('logWood'), ore('sand'), ore('gravel'), item('minecraft:diamond')).fluidOutput(fluid('lava') * 500)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public boolean isEnabled() {
        return CyclicContent.melter.enabled();
    }

    @Override
    public Collection<RecipeMelter> getRecipes() {
        return RecipeMelter.recipes;
    }

    @MethodDescription(example = @Example("item('minecraft:snow')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(recipe -> {
            if (recipe.getRecipeInput().stream().anyMatch(input)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("fluid('amber')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(recipe -> {
            if (output.test(recipe.getOutputFluid())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(gte = 1, lte = 4))
    @Property(property = "fluidOutput", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipeMelter> {

        @Override
        public String getErrorMsg() {
            return "Error adding Cyclic Melter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 4, 0, 0);
            validateFluids(msg, 0, 0, 1, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @NotNull Collection<RecipeMelter> register() {
            if (!validate()) return Collections.emptyList();
            List<RecipeMelter> list = new ArrayList<>();
            List<List<ItemStack>> cartesian = IngredientHelper.cartesianProductItemStacks(input);
            for (List<ItemStack> stacks : cartesian) {
                var recipe = new RecipeMelter(stacks.toArray(new ItemStack[0]), fluidOutput.get(0).getFluid().getName(), fluidOutput.get(0).amount);
                list.add(recipe);
                ModSupport.CYCLIC.get().melter.add(recipe);
            }
            return list;
        }
    }
}
