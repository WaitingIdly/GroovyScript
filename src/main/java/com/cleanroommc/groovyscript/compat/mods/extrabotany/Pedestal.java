package com.cleanroommc.groovyscript.compat.mods.extrabotany;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import com.meteor.extrabotany.api.ExtraBotanyAPI;
import com.meteor.extrabotany.common.crafting.recipe.RecipePedestal;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class Pedestal extends StandardListRegistry<RecipePedestal> {

    @Override
    public Collection<RecipePedestal> getRecipes() {
        return ExtraBotanyAPI.pedestalRecipes;
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:gold_ingot'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond') * 2)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public List<RecipePedestal> add(IIngredient input, ItemStack output) {
        return recipeBuilder().input(input).output(output).register();
    }

    @MethodDescription(example = @Example("item('minecraft:cobblestone')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> {
            if (input.test(r.getInput())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:flint')"))
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
    public static class RecipeBuilder extends AbstractRecipeBuilder<RecipePedestal> {

        @Override
        public String getErrorMsg() {
            return "Error adding Extra Botania Pedestal recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @NotNull List<RecipePedestal> register() {
            if (!validate()) return Collections.emptyList();
            List<RecipePedestal> list = new ArrayList<>();
            for (ItemStack matchingStack : input.get(0).getMatchingStacks()) {
                var recipe = new RecipePedestal(output.get(0), matchingStack);
                list.add(recipe);
                ModSupport.EXTRA_BOTANY.get().pedestal.add(recipe);
            }
            return list;
        }
    }
}
