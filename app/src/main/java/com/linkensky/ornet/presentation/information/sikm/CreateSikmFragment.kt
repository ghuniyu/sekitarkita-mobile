package com.linkensky.ornet.presentation.information.sikm

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.linkensky.ornet.Const
import com.linkensky.ornet.R
import com.linkensky.ornet.data.model.RequestDataSIKM
import com.linkensky.ornet.presentation.WebviewActivity
import com.linkensky.ornet.presentation.base.BaseEpoxyBindingFragment
import com.linkensky.ornet.presentation.base.buildController
import com.linkensky.ornet.presentation.base.item.Frame
import com.linkensky.ornet.presentation.base.item.LayoutOption
import com.linkensky.ornet.presentation.base.item.component.*
import com.linkensky.ornet.presentation.base.item.keyValue
import com.linkensky.ornet.presentation.base.item.layout
import com.linkensky.ornet.presentation.information.sikm.component.CategoryChipView
import com.linkensky.ornet.utils.*
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import id.zelory.compressor.Compressor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.*

class CreateSikmFragment : BaseEpoxyBindingFragment() {

    private lateinit var datePickerDialog: DatePickerDialog
    override val toolbarTitle: String = "Buat SKIM"
    var passed = true

    private val viewModel: CreateSikmViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDatePicker()
    }

    private fun setInvalidForm(isInValid: Boolean) {
        passed = !isInValid
    }

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
                setText = keyValue(state.nik),
                textChangeListner = keyValue { string ->
                    viewModel.setNik(string)
                },
                validator = { layout, editText ->
                    editText.doOnTextChanged { text, _, _, _ ->
                        if (text.toString().length != 16) {
                            layout.error = "NIK Harus 16 Digit"
                        } else {
                            layout.isErrorEnabled = false
                        }
                    }
                }
            )
        )
        addModel(
            "nameModel", InputTextMaterial(
                setInputText = EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME,
                setHint = "Nama",
                setText = keyValue(state.name),
                textChangeListner = keyValue { string ->
                    viewModel.setName(string)
                },
                validator = { layout, editText ->
                    editText.doOnTextChanged { text, _, _, _ ->
                        if (text.isNullOrEmpty()) {
                            layout.error = "Nama Wajib Diisi"
                        } else {
                            layout.isErrorEnabled = false
                        }
                    }

                }
            )
        )
        addModel(
            "phoneModel", InputTextMaterial(
                setHint = "No HP",
                setInputText = EditorInfo.TYPE_CLASS_NUMBER,
                imeOption = EditorInfo.IME_ACTION_DONE,
                setText = keyValue(state.phone),
                textChangeListner = keyValue { string ->
                    viewModel.setPhone(string)
                },
                onDoneAction = keyValue {
                    hideSoftKey(requireContext(), requireView())
                },
                validator = { layout, editText ->
                    editText.doOnTextChanged { text, _, _, _ ->
                        if (!text.toString().validPhone()) {
                            layout.error = "No HP Tidak Valid"
                        } else {
                            layout.isErrorEnabled = false
                        }
                    }

                }
            )
        )
        addModel(
            "originAddress", ListTileView(
                title = getString(R.string.originArea),
                subtitle = setSubtitleArea(state.originText),
                onClick = keyValue { _ ->
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
                onClick = keyValue { _ ->
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
                    it.text = "Sekali Jalan"
                    it.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    it.setTextColor(android.R.color.white.resColor())
                    it.chipBackgroundColor = isSelectedChipColor(state.category == oneWay)
                    it.setOnClickListener {
                        viewModel.setCategory(oneWay)
                    }
                },
                rightChip = {
                    it.text = "Bolak Balik"
                    it.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    it.setTextColor(android.R.color.white.resColor())
                    it.chipBackgroundColor = isSelectedChipColor(state.category == twoWay)
                    it.setOnClickListener {
                        viewModel.setCategory(twoWay)
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
                    imagePicker().start(RC_KTP)
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
                    imagePicker().start(RC_TES_RESULT)
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
                subtitle = setSubtitleArea(state.medical_issued),
                onClick = keyValue { _ ->
                    datePickerDialog.show()
                },
                layout = layout {
                    padding = Frame(8.dp, 8.dp)
                }
            )
        )
        when (val it = state.response) {
            is Loading -> {
                addModel(
                    "loading-submit",
                    LottieLoading(
                        layout = LayoutOption(
                            margin = Frame(
                                right = 8.dp,
                                left = 8.dp,
                                top = 8.dp,
                                bottom = 0.dp
                            )
                        )
                    )
                )
            }
            is Success -> {
                val intent = Intent(requireContext(), WebviewActivity::class.java)
                intent.putExtra("url", "https://sekitarkita.id/sikm/${it().data.id}")
                startActivity(intent)
            }
            else -> {
                addModel(
                    "buttonNext",
                    MaterialButtonView.Model(
                        text = "Lanjutkan",
                        clickListener = keyValue { _ ->
                            next()
                        },
                        allCaps = false,
                        background = R.color.colorPrimary,
                        layout = LayoutOption(margin = Frame(8.dp, 8.dp, 8.dp, 8.dp))
                    )
                )
            }
        }
    }

    private fun setSubtitleArea(string: String): String {
        return if (string.isNotEmpty()) string else emptyArea
    }

    private fun isSelectedChipColor(isSelected: Boolean): ColorStateList? {
        return if (isSelected) chipActiveColor else chipNotActiveColor
    }

    private fun imagePicker(): ImagePicker {
        return ImagePicker.create(this)
            .toolbarImageTitle("Tap to select")
            .toolbarDoneButtonText("Selesai")
            .single()
            .showCamera(true)
            .imageDirectory("camera")
            .imageFullDirectory(Environment.getExternalStorageDirectory().path)
    }

    private fun setupDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, y, monthOfYear, dayOfMonth ->
                viewModel.setDate("$dayOfMonth-${monthOfYear}-$y")
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.maxDate = c.time.time
    }

    private fun next() = withState(viewModel) {
        val data = RequestDataSIKM(
            device_id = Hawk.get(Const.DEVICE_ID),
            name = it.name,
            phone = it.phone,
            nik = it.nik,
            category = it.category,
            medical_issued = it.medical_issued,
            originable_id = it.originable_id,
            destinable_id = it.destinable_id
        )

        val validForm = data.category.isNotEmpty() && data.destinable_id != null
                && data.originable_id != null && data.name.isNotEmpty()
                && data.nik.isNotEmpty() && data.phone.isNotEmpty() && data.medical_issued.isNotEmpty()
                && it.ktp_file != null && it.medical_file != null
        if (validForm && passed) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("PERNYATAAN")
                .setMessage(
                    """
                    Dengan ini saya menyatakan bahwa semua data dan dokumen yang saya sampaikan adalah benar dan apabila dikemudian hari ternyata dokumen tersebut palsu atau tidak benar maka saya siap menerima sanksi hukum sesuai peraturan perundang-undangan yang berlaku.

                    Apakah anda setuju? 
                """.trimIndent()
                )
                .setPositiveButton("Setuju") { _, _ ->
                    viewModel.storeData(
                        data = data,
                        medical = it.medical_file!!,
                        ktp = it.ktp_file!!
                    )
                }
                .setNegativeButton("Kembali") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .setIcon(R.mipmap.ic_launcher)
                .show()
        } else {
            Toasty.warning(requireContext(), "Isi Form Dengan Benar").show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == RC_KTP && data != null) {
            Log.d("HELLO", "KTPP")
            setKtpFile(ImagePicker.getFirstImageOrNull(data))
        } else if (resultCode == Activity.RESULT_OK && requestCode == RC_TES_RESULT && data != null) {
            Log.d("HELLO", "RESULT")
            setMedicalFile(ImagePicker.getFirstImageOrNull(data))
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setKtpFile(image: Image?) {
        if (image != null) {
            val file = File(image.path)
            try {
                val compressedFile = Compressor(context).compressToFile(file)

                val requestFile =
                    compressedFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val img = MultipartBody.Part.createFormData("ktp_file", file.name, requestFile)

                viewModel.setKtpFile(img)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun setMedicalFile(image: Image?) {
        if (image != null) {
            val file = File(image.path)
            try {
                val compressedFile = Compressor(context).compressToFile(file)

                val requestFile =
                    compressedFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val img = MultipartBody.Part.createFormData("medical_file", file.name, requestFile)

                viewModel.setMedicalFile(img)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    companion object {
        val chipActiveColor = R.color.colorPrimary.resColorTint()
        val chipNotActiveColor = R.color.searchBarHint.resColorTint()
        const val oneWay = "one_way"
        const val twoWay = "return"
        const val emptyArea = "Belum Dipilih"
        const val RC_KTP = 101
        const val RC_TES_RESULT = 102
    }
}
