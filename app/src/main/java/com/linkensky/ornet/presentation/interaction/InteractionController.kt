package com.linkensky.ornet.presentation.interaction

import com.linkensky.ornet.itemInteraction
import com.linkensky.ornet.presentation.base.BaseController

class InteractionController : BaseController() {
    override fun buildModels() {
        1.rangeTo(100).forEach {i ->
            itemInteraction {
                id("mac-$i")
                text("6e:c7:6d:94:00:c2")
            }
        }
    }

}