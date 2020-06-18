package com.pandulapeter.beagle.core.list.moduleDelegates

import com.pandulapeter.beagle.common.contracts.module.Cell
import com.pandulapeter.beagle.core.list.cells.TextCell
import com.pandulapeter.beagle.core.list.moduleDelegates.shared.ExpandableModuleDelegate
import com.pandulapeter.beagle.modules.LongTextModule

internal class LongTextDelegate : ExpandableModuleDelegate<LongTextModule> {

    override fun MutableList<Cell<*>>.addItems(module: LongTextModule) {
        add(
            TextCell(
                id = "text_${module.id}",
                text = "• ${module.text}",
                onItemSelected = null
            )
        )
    }
}