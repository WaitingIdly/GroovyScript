
// Auto generated groovyscript example file
// MODS_LOADED: gadgetrycore

log.info 'mod \'gadgetrycore\' detected, running script'

// Alloy Furnace:
// Converts up to three input itemstacks into an output itemstack.

mods.gadgetrycore.alloy_furnace.removeByInput(item('minecraft:iron_ingot'))
mods.gadgetrycore.alloy_furnace.removeByOutput(item('gadgetrycore:redmetal_ingot'))
// mods.gadgetrycore.alloy_furnace.removeAll()

mods.gadgetrycore.alloy_furnace.recipeBuilder()
    .input(item('minecraft:clay'))
    .output(item('minecraft:diamond'))
    .register()

mods.gadgetrycore.alloy_furnace.recipeBuilder()
    .input(item('minecraft:diamond') * 5, item('minecraft:gold_ingot'), item('minecraft:gold_block'))
    .output(item('minecraft:clay'))
    .register()


