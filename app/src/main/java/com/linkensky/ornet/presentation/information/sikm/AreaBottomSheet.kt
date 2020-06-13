package com.linkensky.ornet.presentation.information.sikm

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.existingViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.linkensky.ornet.R
import com.linkensky.ornet.data.model.Area
import com.linkensky.ornet.presentation.base.BaseEpoxySheetFragment
import com.linkensky.ornet.presentation.base.buildController
import com.linkensky.ornet.presentation.base.item.Frame
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.component.*
import com.linkensky.ornet.presentation.base.item.keyValue
import com.linkensky.ornet.presentation.base.item.layout
import com.linkensky.ornet.utils.addModel
import com.linkensky.ornet.utils.dp
import com.linkensky.ornet.utils.resColor
import kotlinx.android.synthetic.main.bottom_sheet_with_recycler.*
import java.math.BigInteger


class AreaBottomSheet : BaseEpoxySheetFragment() {
    private val viewModel: CreateSikmViewModel by existingViewModel()

    private lateinit var searchBar: InputText
    private var isOrigin: Boolean = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
            bottomSheet.setBackgroundResource(android.R.color.white)
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehavior.isFitToContents = false
            bottomSheetBehavior.skipCollapsed = true
        }

        searchBar = InputText(requireContext())

        withState(viewModel) { state ->
            isOrigin = state.sheetId == 1
            text_title.text = if (isOrigin) getString(R.string.originArea)
            else getString(R.string.destArea)
        }

        header.addView(searchBar)
        searchBar.apply {
            bind(InputText.Model(
                hint = "Cari...",
                layout = LayoutOption(margin = Frame(16.dp, 8.dp)),
                itemLayout = LayoutOption(padding = Frame(16.dp, 8.dp)),
                imeOption = EditorInfo.IME_ACTION_DONE,
                drawableStart = R.drawable.ic_google,
                textChangeListner = keyValue { text ->
                    if (isOrigin) {
                        viewModel.setOriginFilter(text)
                    } else {
                        viewModel.setDestinationFilter(text)
                    }
                },
                onDoneAction = keyValue {
                    hideSoftKey(requireContext(), requireView())
                }
            ))

        }
    }


    override fun invalidate() {
        super.invalidate()
    }

    override fun epoxyController() = buildController(viewModel) { state ->
        if (state.sheetId == 1) {
            when (val it = state.originCities) {
                is Success -> {
                    val filter = state.originFilter
                    val data = it().toMutableList()
                    data.toMutableList().filter {
                        it.name.toLowerCase().contains(filter) || filter.isEmpty()
                    }.mapIndexed { index, area ->
                        renderData(area, index, ORIGIN)
                    }.ifEmpty {
                        renderEmpty(ORIGIN)
                    }
                }
                is Loading -> {
                    addModel(
                        "loading-origin",
                        LottieLoading(
                            layout = LayoutOption(
                                margin = Frame(
                                    right = 8.dp,
                                    left = 8.dp,
                                    top = 80.dp,
                                    bottom = 0.dp
                                )
                            )
                        )
                    )
                }
            }
        } else {
            when (val it = state.gorontaloAreas) {
                is Success -> {
                    val filter = state.destinationFilter
                    val data = it().toMutableList()
                    data.toMutableList().filter {
                        it.name.toLowerCase().contains(filter) || filter.isEmpty()
                    }.mapIndexed { index, area ->
                        renderData(area, index, DESTINATION)
                    }.ifEmpty {
                        renderEmpty(DESTINATION)
                    }
                }
                is Loading -> {
                    addModel(
                        "loading-provinces",
                        LottieLoading(
                            layout = LayoutOption(
                                margin = Frame(
                                    right = 8.dp,
                                    left = 8.dp,
                                    top = 80.dp,
                                    bottom = 0.dp
                                )
                            )
                        )
                    )
                }
            }
        }

    }

    private fun EpoxyController.renderData(item: Area, index: Int, name: String) =
        withState(viewModel) { state ->
            addModel(
                "$name${item.name}$index", ListTileView(
                    icon = if(name == ORIGIN) setIcon(item.id, state.originable_id) else setIcon(item.id, state.destinable_id),
                    title = item.name,
                    onClick = keyValue { _ ->
                        if (name == ORIGIN) {
                            viewModel.setOriginId(item.id)
                            viewModel.setOriginText(item.name)
                        } else {
                            viewModel.setDestinationId(item.id)
                            viewModel.setDestinationText(item.name)
                        }
                        dismiss()

                    },
                    layout = layout {
                        padding = Frame(8.dp, 8.dp, 8.dp, 16.dp)
                    }
                )
            )
            addModel(
                "$name divider $index", DividerView.Model(
                    height = 1.dp,
                    color = R.color.colorWhiteGray.resColor()
                )
            )
        }

    private fun EpoxyController.renderEmpty(name: String) {
        addModel(
            "empty-$name",
            LottieEmptyState(
                layout = LayoutOption(
                    margin = Frame(
                        left = 8.dp,
                        right = 8.dp,
                        top = 40.dp,
                        bottom = 80.dp
                    )
                )
            )
        )
    }

    private fun setIcon(field: BigInteger, state: BigInteger?): Int {
        return when {
            state != null && field == state -> R.drawable.ic_check_green
            else -> R.drawable.ic_keyboard_arrow_right
        }
    }

    companion object {
        const val DESTINATION = "destination"
        const val ORIGIN = "origin"
    }
}