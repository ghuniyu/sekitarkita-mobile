package com.linkensky.ornet.presentation.information.sikm

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import com.airbnb.mvrx.activityViewModel
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.BaseEpoxyBindingFragment
import com.linkensky.ornet.presentation.base.buildController
import com.linkensky.ornet.presentation.base.item.Frame
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.component.InputTextMaterial
import com.linkensky.ornet.presentation.base.item.component.ListTileView
import com.linkensky.ornet.presentation.base.item.component.MaterialButtonView
import com.linkensky.ornet.presentation.base.item.component.ViewText
import com.linkensky.ornet.presentation.base.item.keyValue
import com.linkensky.ornet.presentation.base.item.layout
import com.linkensky.ornet.presentation.information.sikm.component.CategoryChipView
import com.linkensky.ornet.utils.*
import java.util.*

class CreateSikmFragment : BaseEpoxyBindingFragment() {

    override val toolbarTitle: String = "Buat SKIM"

    private val viewModel: CreateSikmViewModel by activityViewModel()

    override fun epoxyController() = buildController(viewModel) { state ->
        addModel(
            "label", ViewText.Model(
                text = "Form Surat Ijin Masuk Provinsi Gorontalo",
                gravity = Gravity.CENTER,
                isBold = true,
                textSize = 16f.sp,
                layout = layout {
                    margin = Frame(
                        top = 0.dp,
                        left = 8.dp,
                        right = 8.dp,
                        bottom = 8.dp
                    )
                }
            )
        )
        addModel(
            "noKtpModel", InputTextMaterial(
                setHint = "No KTP (NIK)",
                setInputText = EditorInfo.TYPE_CLASS_NUMBER,
                setText = keyValue(state.data.nik),
                textChangeListner = keyValue { string ->
                    viewModel.setRequestDataSIKM(state.data.copy(nik = string))
                }
            )
        )
        addModel(
            "nameModel", InputTextMaterial(
                setInputText = EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME,
                setHint = "Nama",
                setText = keyValue(state.data.name),
                textChangeListner = keyValue { string ->
                    viewModel.setRequestDataSIKM(state.data.copy(name = string))
                }
            )
        )
        addModel(
            "phoneModel", InputTextMaterial(
                setHint = "No HP",
                setInputText = EditorInfo.TYPE_CLASS_NUMBER,
                setText = keyValue(state.data.phone),
                textChangeListner = keyValue { string ->
                    viewModel.setRequestDataSIKM(state.data.copy(phone = string))
                }
            )
        )
        addModel(
            "originAddress", ListTileView(
                title = getString(R.string.originArea),
                subtitle = setSubtitleArea(state.originText),
                onClick = keyValue {
                    viewModel.setBottomSheet(1)
                    AreaBottomSheet().show(childFragmentManager, "OriginBottomSheet")
                },
                layout = layout {
                    padding = Frame(8.dp, 8.dp)
                }
            )
        )
        addModel(
            "destAddress", ListTileView(
                title = getString(R.string.destArea),
                subtitle = setSubtitleArea(state.destinationText),
                onClick = keyValue {
                    viewModel.setBottomSheet(2)
                    AreaBottomSheet().show(childFragmentManager, "DestinationBottomSheet")
                },
                layout = layout {
                    padding = Frame(8.dp, 8.dp)
                }
            )
        )
        addModel(
            "categoryLabel", ViewText.Model(
                text = "Tujuan Kategori",
                textSize = 14f.sp,
                isBold = true,
                layout = layout {
                    margin = Frame(
                        top = 8.dp,
                        left = 16.dp,
                        right = 16.dp,
                        bottom = 0.dp
                    )
                }
            )
        )
        addModel(
            "categoryModel", CategoryChipView.Model(
                leftChip = {
                    it.text = oneWay
                    it.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    it.setTextColor(android.R.color.white.resColor())
                    it.chipBackgroundColor = isSelectedChipColor(state.data.category == oneWay)
                    it.setOnClickListener {
                        viewModel.setRequestDataSIKM(state.data.copy(category = oneWay))
                    }
                },
                rightChip = {
                    it.text = twoWay
                    it.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    it.setTextColor(android.R.color.white.resColor())
                    it.chipBackgroundColor = isSelectedChipColor(state.data.category == twoWay)
                    it.setOnClickListener {
                        viewModel.setRequestDataSIKM(state.data.copy(category = twoWay))
                    }
                },
                layout = LayoutOption(margin = Frame(16.dp, 8.dp))
            )
        )
        addModel(
            "labelTerm", ViewText.Model(
                text = "Syarat Pelaku Perjalanan untuk ditunjukan ke petugas perbatasan atau pelabuhan",
                gravity = Gravity.CENTER,
                textSize = 16f.sp,
                layout = layout {
                    margin = Frame(
                        top = 8.dp,
                        left = 8.dp,
                        right = 8.dp,
                        bottom = 8.dp
                    )
                }
            )
        )
        addModel(
            "ktpFileLabel", ViewText.Model(
                text = "1. Identitas Diri",
                textSize = 14f.sp,
                isBold = true,
                layout = layout {
                    margin = Frame(
                        top = 8.dp,
                        left = 16.dp,
                        right = 16.dp,
                        bottom = 0.dp
                    )
                }
            )
        )
        addModel(
            "ButtonAddKTP", MaterialButtonView.Model(
                text = "Upload KTP",
                allCaps = false,
                icon = R.drawable.ic_image_gray.resDrawable(),
                background = R.color.colorGreyWhitest,
                textColor = R.color.colorBlack.resColor(),
                layout = layout {
                    margin = Frame(8.dp, 4.dp)
                },
                clickListener = keyValue { _ ->
//                    getImagePicker()
                }
            )
        )

        addModel(
            "testFileLabel", ViewText.Model(
                text = "2. Membawa Hasil Rapid / Swab yang Negative",
                textSize = 14f.sp,
                isBold = true,
                layout = layout {
                    margin = Frame(
                        top = 8.dp,
                        left = 16.dp,
                        right = 16.dp,
                        bottom = 0.dp
                    )
                }
            )
        )
        addModel(
            "ButtonAddTes", MaterialButtonView.Model(
                text = "Upload Hasil Tes",
                allCaps = false,
                icon = R.drawable.ic_image_gray.resDrawable(),
                background = R.color.colorGreyWhitest,
                textColor = R.color.colorBlack.resColor(),
                layout = layout {
                    margin = Frame(8.dp, 4.dp)
                },
                clickListener = keyValue { _ ->
//                    getImagePicker()
                }
            )
        )
        addModel(
            "startDate", ViewText.Model(
                text = "3. Tanggal Mulai Berlaku",
                textSize = 14f.sp,
                isBold = true,
                layout = layout {
                    margin = Frame(
                        top = 8.dp,
                        left = 16.dp,
                        right = 16.dp,
                        bottom = 0.dp
                    )
                }
            )
        )
        addModel(
            "chooseDate", ListTileView(
                title = "Pilih Tanggal",
                subtitle = setSubtitleArea(state.data.medical_issued),
                onClick = keyValue {
                    showDatePicker()
                },
                layout = layout {
                    padding = Frame(8.dp, 8.dp)
                }
            )
        )
        addModel(
            "buttonNext",
            MaterialButtonView.Model(
                text = "Lanjutkan",
                clickListener = keyValue(null),
                allCaps = false,
                background = R.color.colorPrimary,
                layout = LayoutOption(margin = Frame(8.dp, 8.dp, 8.dp, 8.dp))
            )
        )
    }

    private fun setSubtitleArea(string: String): String {
        return if (string.isNotEmpty()) string else emptyArea
    }

    private fun isSelectedChipColor(isSelected: Boolean): ColorStateList? {
        return if (isSelected) chipActiveColor else chipNotActiveColor
    }
    

    private fun showDatePicker()
    {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            // Display Selected date in textbox
//            lblDate.setText("" + dayOfMonth + " " + MONTHS[monthOfYear] + ", " + year)

        }, year, month, day)
        
        dpd.show()
    }

    companion object {
        val chipActiveColor = R.color.colorPrimary.resColorTint()
        val chipNotActiveColor = R.color.searchBarHint.resColorTint()
        const val oneWay = "Sekali Jalan"
        const val twoWay = "Bolak Balik"
        const val emptyArea = "Belum Dipilih"
    }
}
