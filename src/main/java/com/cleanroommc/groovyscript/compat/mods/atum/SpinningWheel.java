package com.cleanroommc.groovyscript.compat.mods.atum;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import com.teammetallurgy.atum.api.recipe.RecipeHandlers;
import com.teammetallurgy.atum.api.recipe.spinningwheel.ISpinningWheelRecipe;
import com.teammetallurgy.atum.api.recipe.spinningwheel.SpinningWheelRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class SpinningWheel extends ForgeRegistryWrapper<ISpinningWheelRecipe> {

    public SpinningWheel() {
        super(RecipeHandlers.spinningWheelRecipes);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:clay')).rotations(1)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay') * 4).rotations(5)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public List<ISpinningWheelRecipe> add(IIngredient input, ItemStack output) {
        return add(input, output, 1);
    }

    public List<ISpinningWheelRecipe> add(IIngredient input, ItemStack output, int rotations) {
        return recipeBuilder()
                .rotations(rotations)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(example = @Example("item('atum:flax')"))
    public void removeByInput(IIngredient input) {
        for (ISpinningWheelRecipe recipe : getRegistry()) {
            if (recipe.getInput().stream().anyMatch(input)) {
                remove(recipe);
            }
        }
    }

    @MethodDescription(example = @Example("item('minecraft:string')"))
    public void removeByOutput(IIngredient output) {
        for (ISpinningWheelRecipe recipe : getRegistry()) {
            if (output.test(recipe.getOutput())) {
                remove(recipe);
            }
        }
    }

    @Property(property = "name")
    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ISpinningWheelRecipe> {

        @Property(comp = @Comp(gt = 0))
        private int rotations;

        @RecipeBuilderMethodDescription
        public RecipeBuilder rotations(int rotations) {
            this.rotations = rotations;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Atum 2 Spinning Wheel recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateName();
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(rotations <= 0, "rotations must be a greater than 0, yet it was {}", rotations);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @NotNull List<ISpinningWheelRecipe> register() {
            if (!validate()) return Collections.emptyList();
            if (input.get(0) instanceof OreDictIngredient oreDictIngredient) {
                ISpinningWheelRecipe recipe = new SpinningWheelRecipe(oreDictIngredient.getOreDict(), output.get(0), rotations);
                recipe.setRegistryName(super.name);
                ModSupport.ATUM.get().spinningWheel.add(recipe);
                return Collections.singletonList(recipe);
            }
            List<ISpinningWheelRecipe> list = new ArrayList<>();
            ItemStack[] matchingStacks = input.get(0).getMatchingStacks();
            for (int i = 0; i < matchingStacks.length; i++) {
                ISpinningWheelRecipe recipe = new SpinningWheelRecipe(matchingStacks[i], output.get(0), rotations);
                var location = new ResourceLocation(super.name.getNamespace(), super.name.getPath() + i);
                recipe.setRegistryName(location);
                list.add(recipe);
                ModSupport.ATUM.get().spinningWheel.add(recipe);
            }
            return list;
        }
    }
}
