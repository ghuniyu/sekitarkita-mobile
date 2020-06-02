package com.linkensky.ornet.presentation.mac

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.linkensky.ornet.Const
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentMacAddressBinding
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.utils.resString
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty


class MacAddressFragment : BaseFragment<FragmentMacAddressBinding>() {
    override fun getLayoutRes() = R.layout.fragment_mac_address

    private val controller by lazy {
        MacAddressController()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(Hawk.contains(Const.DEVICE_ID) && Hawk.contains(Const.SELF_TEST_COMPLETED)) {
            view.findNavController().popBackStack()
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            tutorialRecycler.setController(controller)
            tutorialRecycler.layoutManager = GridLayoutManager(context, 1)
            text = "Bluetooth Perangkat"
            subTitle = "Lihat penduan pengguna dibawah"

            registerAfterMacTextChangedCallback(macEditText)

            controller.requestModelBuild()
            setOnShowMac {
                startActivity(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS))
            }

            setOnSave {
                val macAddress = macEditText.text.toString()
                if (macAddress.length == 17)
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.are_you_sure))
                        .setMessage(getString(R.string.mac_address_confirm))
                        .setPositiveButton(getString(R.string.correct)) { _, _ ->
                            Hawk.put(Const.DEVICE_ID, macAddress)
                            Toasty.success(requireContext(), R.string.bluetooth_saved.resString())
                                .show()
                            hideSoftKey(context, view)
                            view.findNavController()
                                .navigate(R.id.action_macAddressFragment_to_selfcheckFragment)
                        }
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show()
                else
                    macEditText.error = R.string.incorect_mac_address.resString()
            }
        }
    }

    override fun invalidate() {}

    private fun registerAfterMacTextChangedCallback(mMacEdit: EditText) {
        mMacEdit.addTextChangedListener(object : TextWatcher {
            var mPreviousMac: String? = null

            override fun afterTextChanged(arg0: Editable) {}
            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

            @SuppressLint("DefaultLocale")
            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                val enteredMac: String = s.toString().toUpperCase()
                val cleanMac = clearNonMacCharacters(enteredMac)
                var formattedMac = formatMacAddress(cleanMac)
                val selectionStart: Int = mMacEdit.selectionStart
                formattedMac = handleColonDeletion(enteredMac, formattedMac, selectionStart)
                val lengthDiff = formattedMac.length - enteredMac.length
                setMacEdit(cleanMac, formattedMac, selectionStart, lengthDiff)
            }

            /**
             * Strips all characters from a string except A-F and 0-9.
             * @param mac       User input string.
             * @return          String containing MAC-allowed characters.
             */
            private fun clearNonMacCharacters(mac: String): String {
                return mac.replace("[^A-Fa-f0-9]".toRegex(), "")
            }

            /**
             * Adds a colon character to an unformatted MAC address after
             * every second character (strips full MAC trailing colon)
             * @param cleanMac      Unformatted MAC address.
             * @return              Properly formatted MAC address.
             */
            private fun formatMacAddress(cleanMac: String): String {
                var grouppedCharacters = 0
                var formattedMac = ""
                for (element in cleanMac) {
                    formattedMac += element
                    ++grouppedCharacters
                    if (grouppedCharacters == 2) {
                        formattedMac += ":"
                        grouppedCharacters = 0
                    }
                }

                // Removes trailing colon for complete MAC address
                if (cleanMac.length == 12) formattedMac =
                    formattedMac.substring(0, formattedMac.length - 1)


                return formattedMac
            }

            /**
             * Upon users colon deletion, deletes MAC character preceding deleted colon as well.
             * @param enteredMac            User input MAC.
             * @param formattedMac          Formatted MAC address.
             * @param selectionStart        MAC EditText field cursor position.
             * @return                      Formatted MAC address.
             */
            private fun handleColonDeletion(
                enteredMac: String,
                formattedMac: String,
                selectionStart: Int
            ): String {
                var formattedMac = formattedMac
                mPreviousMac?.let {
                    if (it.length > 1) {
                        val previousColonCount = colonCount(it)
                        val currentColonCount = colonCount(enteredMac)
                        if (currentColonCount < previousColonCount && selectionStart > 0) {
                            formattedMac =
                                formattedMac.substring(
                                    0,
                                    selectionStart - 1
                                ) + formattedMac.substring(
                                    selectionStart
                                )
                            val cleanMac = clearNonMacCharacters(formattedMac)
                            formattedMac = formatMacAddress(cleanMac)
                        } else {
                            val cleanMac = clearNonMacCharacters(formattedMac)
                            formattedMac = formatMacAddress(cleanMac)
                        }
                    }
                }

                return formattedMac
            }

            /**
             * Gets MAC address current colon count.
             * @param formattedMac      Formatted MAC address.
             * @return                  Current number of colons in MAC address.
             */
            private fun colonCount(formattedMac: String): Int {
                return formattedMac.replace("[^:]".toRegex(), "").length
            }

            /**
             * Removes TextChange listener, sets MAC EditText field value,
             * sets new cursor position and re-initiates the listener.
             * @param cleanMac          Clean MAC address.
             * @param formattedMac      Formatted MAC address.
             * @param selectionStart    MAC EditText field cursor position.
             * @param lengthDiff        Formatted/Entered MAC number of characters difference.
             */
            private fun setMacEdit(
                cleanMac: String,
                formattedMac: String,
                selectionStart: Int,
                lengthDiff: Int
            ) {
                mMacEdit.removeTextChangedListener(this)
                if (cleanMac.length <= 12) {
                    mMacEdit.setText(formattedMac)
                    if (lengthDiff == -1 && selectionStart == 0
                        || lengthDiff == 1 && selectionStart == 1)
                        mMacEdit.setSelection(selectionStart)
                    else
                        mMacEdit.setSelection(selectionStart + lengthDiff)
                    mPreviousMac = formattedMac
                } else {
                    mMacEdit.setText(mPreviousMac ?: "")
                    mMacEdit.setSelection(mPreviousMac?.length ?: 0)
                }
                mMacEdit.addTextChangedListener(this)
            }
        })
    }

}