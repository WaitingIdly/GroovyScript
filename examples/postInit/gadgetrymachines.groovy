
// Auto generated groovyscript example file
// MODS_LOADED: gadgetrymachines

log.info 'mod \'gadgetrymachines\' detected, running script'

// Distiller:
// Converts an input fluidstack into an output fluidstack, with the ability to also require an input item and/or output an
// itemstack.

mods.gadgetrymachines.distiller.removeByInput(item('minecraft:wheat'))
// mods.gadgetrymachines.distiller.removeByInput(fluid('water'))
mods.gadgetrymachines.distiller.removeByOutput(fluid('fuel'))
// mods.gadgetrymachines.distiller.removeByOutput(item('gadgetrycore:biomass'))
// mods.gadgetrymachines.distiller.removeAll()

mods.gadgetrymachines.distiller.recipeBuilder()
    .fluidInput(fluid('lava') * 500)
    .fluidOutput(fluid('water'))
    .register()

mods.gadgetrymachines.distiller.recipeBuilder()
    .fluidInput(fluid('water') * 50)
    .fluidOutput(fluid('lava') * 10)
    .input(item('minecraft:diamond'))
    .output(item('minecraft:clay'))
    .register()

mods.gadgetrymachines.distiller.recipeBuilder()
    .fluidInput(fluid('fuel') * 50)
    .fluidOutput(fluid('lava') * 10)
    .output(item('minecraft:clay'))
    .register()

mods.gadgetrymachines.distiller.recipeBuilder()
    .fluidInput(fluid('ethanol') * 50)
    .fluidOutput(fluid('fuel') * 1000)
    .input(item('minecraft:clay'))
    .register()


// Grinder:
// Converts an input itemstack into an output itemstack.

mods.gadgetrymachines.grinder.removeByInput(item('minecraft:coal_ore'))
mods.gadgetrymachines.grinder.removeByOutput(item('minecraft:sand'))
// mods.gadgetrymachines.grinder.removeAll()

mods.gadgetrymachines.grinder.recipeBuilder()
    .input(item('minecraft:diamond') * 5)
    .output(item('minecraft:clay'))
    .register()

mods.gadgetrymachines.grinder.recipeBuilder()
    .input(item('minecraft:gold_ingot'))
    .output(item('minecraft:diamond') * 3)
    .register()


