package com.linkensky.ornet.presentation.information

import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.BaseEpoxyBindingFragment
import com.linkensky.ornet.presentation.base.buildController
import com.linkensky.ornet.presentation.base.item.Frame
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.component.CardInfoView
import com.linkensky.ornet.presentation.base.item.component.MaterialButtonView
import com.linkensky.ornet.presentation.base.item.keyValue
import com.linkensky.ornet.presentation.information.sikm.InputBottomSheet
import com.linkensky.ornet.utils.addModel
import com.linkensky.ornet.utils.dp
import com.linkensky.ornet.utils.resString

class InformationOnBoardFragment : BaseEpoxyBindingFragment() {
    override val toolbarTitle: String = "Informasi"

    override fun epoxyController() = buildController {
        addModel(
            "info-sikm",
            CardInfoView(
                titleText = R.string.info_sikm.resString(),
                contentText = R.string.info_sikm_content.resString()
            )
        )
        addModel(
            "show-sikm",
            MaterialButtonView.Model(
                text = "Saya Ingin Melihat SIKM Saya",
                clickListener = keyValue { _ ->
                    InputBottomSheet().show(
                        parentFragmentManager,
                        "inputBottomSheet"
                    )
                },
                allCaps = false,
                background = R.color.colorNavy,
                layout = LayoutOption(margin = Frame(8.dp, 8.dp, 8.dp, 8.dp))
            )
        )
        addModel(
            "create-sikm",
            MaterialButtonView.Model(
                text = "Saya Ingin Membuat SIKM",
                clickListener = keyValue { _ -> navigateTo(R.id.action_informationOnBoardFragment_to_createSikmFragment) },
                allCaps = false,
                background = R.color.colorRed,
                layout = LayoutOption(margin = Frame(8.dp, 8.dp, 8.dp, 8.dp))
            )
        )
        addModel(
            "other-information",
            MaterialButtonView.Model(
                text = "Informasi Lainnya",
                clickListener = keyValue { _ -> navigateTo(R.id.statisticFragment) },
                background = R.color.blue,
                allCaps = false,
                layout = LayoutOption(margin = Frame(8.dp, 8.dp, 8.dp, 8.dp))
            )
        )
    }
}