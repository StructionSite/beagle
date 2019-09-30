package com.pandulapeter.beagleCore.configuration.modules

import com.pandulapeter.beagleCore.contracts.BeagleModuleContract
import java.util.UUID

/**
 * Displays a button with configurable text and action.
 * This module can be added multiple times as long as the ID is unique.
 *
 * @param id - A unique ID for the module. If you don't intend to dynamically remove / modify the module, a suitable default value is auto-generated.
 * @param text - The text that should be displayed on the button.
 * @param onButtonPressed - The callback that gets invoked when the user presses the button.
 */
//TODO: The Buttons don't look great if the app uses Material theme.
data class ButtonModule(
    override val id: String = UUID.randomUUID().toString(),
    val text: CharSequence,
    val onButtonPressed: () -> Unit
) : BeagleModuleContract