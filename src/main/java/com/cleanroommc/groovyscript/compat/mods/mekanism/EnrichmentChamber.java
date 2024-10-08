package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.EnrichmentRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class EnrichmentChamber extends VirtualizedMekanismRegistry<EnrichmentRecipe> {

    public EnrichmentChamber() {
        super(RecipeHandler.Recipe.ENRICHMENT_CHAMBER, Alias.generateOfClassAnd(EnrichmentChamber.class, "Enricher"));
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay_ball')).output(item('minecraft:nether_star'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "item('minecraft:clay_ball'), item('minecraft:nether_star')", commented = true))
    public EnrichmentRecipe add(IIngredient ingredient, ItemStack output) {
        return recipeBuilder().output(output).input(ingredient).register();
    }

    @MethodDescription(example = @Example("item('minecraft:diamond')"))
    public boolean removeByInput(IIngredient ingredient) {
        if (IngredientHelper.isEmpty(ingredient)) {
            removeError("input must not be empty");
            return false;
        }
        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            EnrichmentRecipe recipe = recipeRegistry.get().remove(new ItemStackInput(itemStack));
            if (recipe != null) {
                addBackup(recipe);
                found = true;
            }
        }
        if (!found) {
            removeError("could not find recipe for {}", ingredient);
        }
        return found;
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<EnrichmentRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Enrichment Chamber recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable EnrichmentRecipe register() {
            if (!validate()) return null;
            EnrichmentRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                EnrichmentRecipe r = new EnrichmentRecipe(itemStack, output.get(0));
                if (recipe == null) recipe = r;
                ModSupport.MEKANISM.get().enrichmentChamber.add(r);
            }
            return recipe;
        }
    }
}
