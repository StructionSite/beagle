package com.pandulapeter.beagle.core.list.delegates

import android.os.Build
import com.pandulapeter.beagle.BeagleCore
import com.pandulapeter.beagle.common.contracts.module.Cell
import com.pandulapeter.beagle.common.contracts.module.Module
import com.pandulapeter.beagle.core.util.createTextModuleFromType
import com.pandulapeter.beagle.modules.ScreenRecordingButtonModule

internal class ScreenRecordingButtonDelegate : Module.Delegate<ScreenRecordingButtonModule> {

    override fun createCells(module: ScreenRecordingButtonModule): List<Cell<*>> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listOf(
                createTextModuleFromType(
                    type = module.type,
                    id = module.id,
                    text = module.text,
                    isEnabled = module.isEnabled,
                    icon = module.icon,
                    onItemSelected = {
                        module.onButtonPressed()
                        hideDebugMenuAndRecordScreen()
                    }
                )
            )
        } else {
            emptyList()
        }

    companion object {
        fun hideDebugMenuAndRecordScreen() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BeagleCore.implementation.performOnHide(BeagleCore.implementation::recordScreen)
            }
        }
    }
}