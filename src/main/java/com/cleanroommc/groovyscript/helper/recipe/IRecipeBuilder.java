package com.cleanroommc.groovyscript.helper.recipe;

import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A groovy recipe builder template
 *
 * @param <T>
 */
public interface IRecipeBuilder<T> {

    /**
     * Validates the current values. Should be called in {@link #register()}.
     *
     * @return if a valid recipe can be build with the current values
     */
    boolean validate();

    /**
     * This method creates and tries to register recipes using the values of this builder.
     * <ul>
     *     <li>Should call {@link #validate()} and return an empty list if the values are not valid.</li>
     *     <li>Should return all recipes added.</li>
     *     <li>Should never return {@code null}.</li>
     * </ul>
     *
     * @return the built recipes or an empty list if values are invalid
     */
    @RecipeBuilderRegistrationMethod
    @NotNull
    List<T> register();
}
