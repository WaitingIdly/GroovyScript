package com.cleanroommc.groovyscript.compat.mods.astralsorcery.starlightaltar;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.IJEIRemoval;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.JeiRemovalHelper;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.OperationHandler;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.google.common.collect.ImmutableList;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry;
import hellfirepvp.astralsorcery.common.integrations.ModIntegrationJEI;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.altar.AltarAttunementRecipeWrapper;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.altar.AltarConstellationRecipeWrapper;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.altar.AltarDiscoveryRecipeWrapper;
import hellfirepvp.astralsorcery.common.integrations.mods.jei.altar.AltarTraitRecipeWrapper;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RegistryDescription
public class StarlightAltar extends VirtualizedRegistry<AbstractAltarRecipe> implements IJEIRemoval.Default {

    private static List<String> registryNameRemovalMethod(AbstractAltarRecipe recipe) {
        return Collections.singletonList(JeiRemovalHelper.format("remove", GroovyScriptCodeConverter.asGroovyCode(recipe.getNativeRecipe().getRegistryName(), true)));
    }

    private static OperationHandler.IOperation discoveryOperation() {
        return new OperationHandler.WrapperOperation<>(AltarDiscoveryRecipeWrapper.class, wrapper -> registryNameRemovalMethod(wrapper.getRecipe()));
    }

    private static OperationHandler.IOperation attunementOperation() {
        return new OperationHandler.WrapperOperation<>(AltarAttunementRecipeWrapper.class, wrapper -> registryNameRemovalMethod(wrapper.getRecipe()));
    }

    private static OperationHandler.IOperation constellationOperation() {
        return new OperationHandler.WrapperOperation<>(AltarConstellationRecipeWrapper.class, wrapper -> registryNameRemovalMethod(wrapper.getRecipe()));
    }

    private static OperationHandler.IOperation traitOperation() {
        return new OperationHandler.WrapperOperation<>(AltarTraitRecipeWrapper.class, wrapper -> registryNameRemovalMethod(wrapper.getRecipe()));
    }

    @RecipeBuilderDescription(priority = 100, requirement = {
            @Property(property = "ingredientMatrix", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "9", type = Comp.Type.LTE)}),
            @Property(property = "starlightRequired", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "1000", type = Comp.Type.LTE)})
    }, example = @Example(".output(item('minecraft:water_bucket')).row('   ').row(' B ').row('   ').key('B', item('minecraft:bucket')).starlight(500).craftTime(10)"))
    public static AltarRecipeBuilder.Shaped discoveryRecipeBuilder() {
        return new AltarRecipeBuilder.Shaped(3, 3, TileAltar.AltarLevel.DISCOVERY);
    }

    @RecipeBuilderDescription(priority = 200, requirement = {
            @Property(property = "ingredientMatrix", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "13", type = Comp.Type.LTE)}),
            @Property(property = "starlightRequired", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "2000", type = Comp.Type.LTE)})
    })
    public static AltarRecipeBuilder.Shaped attunementRecipeBuilder() {
        return new AltarRecipeBuilder.Shaped(5, 5, TileAltar.AltarLevel.ATTUNEMENT);
    }

    @RecipeBuilderDescription(priority = 300, requirement = {
            @Property(property = "ingredientMatrix", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "21", type = Comp.Type.LTE)}),
            @Property(property = "starlightRequired", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "4000", type = Comp.Type.LTE)})
    }, example = @Example(".output(item('minecraft:pumpkin')).matrix('ss ss', 's   s', '  d  ', 's   s', 'ss ss').key('s', item('minecraft:pumpkin_seeds')).key('d', ore('dirt'))"))
    public static AltarRecipeBuilder.Shaped constellationRecipeBuilder() {
        return new AltarRecipeBuilder.Shaped(5, 5, TileAltar.AltarLevel.CONSTELLATION_CRAFT);
    }

    @RecipeBuilderDescription(priority = 400, requirement = {
            @Property(property = "ingredientMatrix", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "25", type = Comp.Type.LTE)}),
            @Property(property = "outerIngredients", valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "24", type = Comp.Type.LTE)}),
            @Property(property = "starlightRequired", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "8000", type = Comp.Type.LTE)}),
            @Property(property = "requiredConstellation")
    }, example = @Example(".output(item('astralsorcery:itemrockcrystalsimple').setSize(300).setPurity(50).setCutting(50)).matrix('sssss', 'sgggs', 'sgdgs', 'sgggs', 'sssss').key('s', item('minecraft:pumpkin')).key('g', ore('treeLeaves')).key('d', item('minecraft:diamond_block')).outerInput(item('astralsorcery:blockmarble')).outerInput(ore('ingotAstralStarmetal')).outerInput(fluid('astralsorcery.liquidstarlight') * 1000).outerInput(ore('treeSapling')).constellation(constellation('discidia'))"))
    public static AltarRecipeBuilder.Shaped traitRecipeBuilder() {
        return new AltarRecipeBuilder.Shaped(5, 5, TileAltar.AltarLevel.TRAIT_CRAFT);
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(r -> AltarRecipeRegistry.recipes.get(r.getNeededLevel()).removeIf(rec -> rec.equals(r)));
        restoreFromBackup().forEach(r -> AltarRecipeRegistry.recipes.get(r.getNeededLevel()).add(r));
    }

    @Override
    public void afterScriptLoad() {
        AltarRecipeRegistry.compileRecipes();
    }


    public AbstractAltarRecipe add(AbstractAltarRecipe recipe) {
        addScripted(recipe);
        AltarRecipeRegistry.recipes.get(recipe.getNeededLevel()).add(recipe);
        return recipe;
    }

    @MethodDescription(description = "groovyscript.wiki.astralsorcery.starlight_altar.removeByOutput0")
    public void removeByOutput(ItemStack output) {
        removeByOutput(output, TileAltar.AltarLevel.DISCOVERY);
        removeByOutput(output, TileAltar.AltarLevel.ATTUNEMENT);
        removeByOutput(output, TileAltar.AltarLevel.CONSTELLATION_CRAFT);
        removeByOutput(output, TileAltar.AltarLevel.TRAIT_CRAFT);
    }

    @MethodDescription(description = "groovyscript.wiki.astralsorcery.starlight_altar.removeByOutput1")
    public void removeByOutput(ItemStack output, TileAltar.AltarLevel altarLevel) {
        AltarRecipeRegistry.recipes.get(altarLevel).removeIf(recipe -> {
            if (recipe.getOutputForMatching().isItemEqual(output)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    private boolean remove(AbstractAltarRecipe recipe) {
        return AltarRecipeRegistry.recipes.get(recipe.getNeededLevel()).removeIf(rec -> rec.equals(recipe));
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<AbstractAltarRecipe> streamRecipes() {
        return new SimpleObjectStream<>(AltarRecipeRegistry.recipes.entrySet().stream().flatMap(r -> r.getValue().stream()).collect(Collectors.toList()))
                .setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        AltarRecipeRegistry.recipes.forEach((level, recipes) -> {
            recipes.forEach(this::addBackup);
            recipes.clear();
        });
    }

    @Override
    public @NotNull Collection<String> getCategories() {
        return ImmutableList.of(ModIntegrationJEI.idAltarDiscovery, ModIntegrationJEI.idAltarAttunement, ModIntegrationJEI.idAltarConstellation, ModIntegrationJEI.idAltarTrait);
    }

    @Override
    public @NotNull List<OperationHandler.IOperation> getJEIOperations() {
        return ImmutableList.of(discoveryOperation(), attunementOperation(), constellationOperation(), traitOperation(), OperationHandler.ItemOperation.defaultItemOperation().include(0), OperationHandler.FluidOperation.defaultFluidOperation());
    }

}
