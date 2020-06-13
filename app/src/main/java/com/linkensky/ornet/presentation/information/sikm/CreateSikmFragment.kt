package com.linkensky.ornet.presentation.information.sikm

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.*
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.esafirm.imagepicker.model.Image
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.linkensky.ornet.BuildConfig
import com.linkensky.ornet.Const
import com.linkensky.ornet.R
import com.linkensky.ornet.data.model.RequestDataSIKM
import com.linkensky.ornet.inputText
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
import org.koin.android.ext.android.bind
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
        viewModel.asyncSubscribe(
            subscriptionLifecycleOwner,
            CreateSikmState::response,
            UniqueOnly("responseSikm${Random().nextInt()}"),
            onSuccess = {
                val intent = Intent(requireContext(), WebviewActivity::class.java)
                intent.putExtra("url", "${BuildConfig.APP_WEB_URL}sikm/${it.data.id}")
                startActivityForResult(intent, RC_WEBVIEW)
            },
            onFail = {
                Toasty.error(requireContext(), "Terjadi Kesalahan, Coba Lagi").show()
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.clearState()
        viewModel.getGorontaloArea()
        viewModel.getOriginCities()
    }

    override fun invalidate() {
        super.invalidate()
        recyclerView.requestModelBuild()
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
                    if (string.length != 16) {
                        viewModel.setErrorNik("NIK Harus 16 Digit")
                    } else {
                        viewModel.setErrorNik("")
                    }
                    viewModel.setNik(string)
                },
                setError = state.nikError
            )
        )
        addModel(
            "nameModel", InputTextMaterial(
                setInputText = EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME,
                setHint = "Nama",
                setText = keyValue(state.name),
                textChangeListner = keyValue { string ->
                    if (string.isEmpty()) {
                        viewModel.setErrorName("Nama Wajib Diisi")
                    } else {
                        viewModel.setErrorName("")
                    }

                    viewModel.setName(string)
                },
                setError = state.nameError
            )
        )
        addModel(
            "phoneModel", InputTextMaterial(
                setHint = "No HP",
                setInputText = EditorInfo.TYPE_CLASS_NUMBER,
                imeOption = EditorInfo.IME_ACTION_DONE,
                setText = keyValue(state.phone),
                textChangeListner = keyValue { string ->
                    if (!string.validPhone()) {
                        viewModel.setErrorPhone("No HP Tidak Valid")
                    } else {
                        viewModel.setErrorPhone("")
                    }
                    viewModel.setPhone(string)
                },
                onDoneAction = keyValue {
                    hideSoftKey(requireContext(), requireView())
                },
                setError = state.phoneError
            )
        )
        addModel(
            "originAddress", ListTileView(
                title = getString(R.string.originArea),
                subtitle = setSubtitleArea(state.originText),
                onClick = keyValue { _ ->
                    viewModel.setBottomSheet(1)
                    AreaBottomSheet().show(parentFragmentManager, "OriginBottomSheet")
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
                    AreaBottomSheet().show(parentFragmentManager, "DestinationBottomSheet")
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
        if (state.ktp_file != null)
            addModel(
                "ktpFileExist", ViewText.Model(
                    text = "File KTP sudah dipilih",
                    textSize = 12f.sp,
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

        if (state.medical_file != null)
            addModel(
                "medicalFileExist", ViewText.Model(
                    text = "File Swab / Rapid sudah dipilih",
                    textSize = 12f.sp,
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
                text = "3. Tanggal Mulai Berlaku (Dokumen Rapid /Swab)",
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
            .theme(R.style.ImagePickerTheme)
            .returnMode(ReturnMode.ALL)
            .folderMode(true)
            .toolbarImageTitle("Tap to select")
            .toolbarDoneButtonText("Selesai")
            .single()
            .showCamera(true)
            .imageFullDirectory(context?.getExternalFilesDir(null)?.absolutePath)
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
            name = it.name ?: "",
            phone = it.phone ?: "",
            nik = it.nik ?: "",
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
        try {

            if (resultCode == Activity.RESULT_OK && requestCode == RC_KTP && data != null) {
                setKtpFile(ImagePicker.getFirstImageOrNull(data))
            } else if (resultCode == Activity.RESULT_OK && requestCode == RC_TES_RESULT && data != null) {
                setMedicalFile(ImagePicker.getFirstImageOrNull(data))
            }

            if (resultCode == Activity.RESULT_OK && requestCode == RC_WEBVIEW) {
                findNavController().popBackStack()
            }
        } catch (e: Exception) {
            Toasty.error(requireContext(), "Gagal Mengambil File, File tidak ditemukan").show()
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
        const val RC_WEBVIEW = 100
    }
}
