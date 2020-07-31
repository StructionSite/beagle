package com.pandulapeter.beagle.core.list.delegates

import android.os.Build
import com.pandulapeter.beagle.BeagleCore
import com.pandulapeter.beagle.common.contracts.module.Cell
import com.pandulapeter.beagle.core.list.cells.TextCell
import com.pandulapeter.beagle.core.list.delegates.shared.ExpandableModuleDelegate
import com.pandulapeter.beagle.core.util.extension.append
import com.pandulapeter.beagle.modules.ScreenCaptureToolboxModule

internal class ScreenCaptureToolboxDelegate : ExpandableModuleDelegate<ScreenCaptureToolboxModule> {

    override fun canExpand(module: ScreenCaptureToolboxModule) = true

    override fun MutableList<Cell<*>>.addItems(module: ScreenCaptureToolboxModule) {
        module.imageText?.let { imageText ->
            add(
                TextCell(
                    id = "${module.id}_image",
                    text = "• ".append(imageText),
                    onItemSelected = ScreenshotButtonDelegate.Companion::hideDebugMenuAndTakeScreenshot
                )
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            module.videoText?.let { videoText ->
                add(
                    TextCell(
                        id = "${module.id}_video",
                        text = "• ".append(videoText),
                        onItemSelected = ScreenRecordingButtonDelegate.Companion::hideDebugMenuAndRecordScreen
                    )
                )
            }
        }
        add(
            TextCell(
                id = "${module.id}_gallery",
                text = "• ".append(module.galleryText),
                onItemSelected = BeagleCore.implementation::openGallery
            )
        )
    }
}