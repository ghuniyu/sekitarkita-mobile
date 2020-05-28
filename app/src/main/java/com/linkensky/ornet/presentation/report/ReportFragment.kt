package com.linkensky.ornet.presentation.report

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.airbnb.mvrx.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.linkensky.ornet.Const
import com.linkensky.ornet.R
import com.linkensky.ornet.data.model.ChangeStatusRequest
import com.linkensky.ornet.data.model.enums.Status
import com.linkensky.ornet.databinding.FragmentReportBinding
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.utils.required
import com.linkensky.ornet.utils.resString
import com.orhanobut.hawk.Hawk

class ReportFragment : BaseFragment<FragmentReportBinding>() {
    private val viewModel: ReportViewModel by activityViewModel()
    override fun getLayoutRes() = R.layout.fragment_report
    override fun invalidate() {}
    private val controller: ReportController by lazy {
        ReportController(viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            setOnReport {
                when (it.id) {
                    R.id.card_pdp -> reportDialog(Status.PDP)
                    R.id.card_odp -> reportDialog(Status.ODP)
                    R.id.card_otg -> reportDialog(Status.OTG)
                    R.id.card_positive -> reportDialog(Status.POSITIVE)
                    R.id.card_traveler -> reportDialog(Status.TRAVELER)
                    R.id.card_healthy -> reportDialog(Status.HEALTHY)
                }
            }
            text = "Lapor Infeksi"
            controller.requestModelBuild()
        }

        viewModel.asyncSubscribe(
            subscriptionLifecycleOwner,
            ReportState::responsePostChangeStatus,
            onSuccess = {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.success))
                    .setMessage(it.message)
                    .setPositiveButton(getString(R.string.close)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            },
            onFail = {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.oops))
                    .setMessage(getString(R.string.general_error))
                    .setPositiveButton(getString(R.string.try_again)) { _, _ ->
                        postReport()
                    }
                    .show()
            }
        )
    }


    private fun reportDialog(status: Status) = withState(viewModel) { state ->
        viewModel.setStatus(status)
        context?.let { ctx ->
            val d = Dialog(ctx, R.style.ReportDialog)
            d.window?.setBackgroundDrawableResource(android.R.color.transparent)
            d.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
            d.setContentView(R.layout.dialog_report)
            d.setCancelable(false)

            val name = d.findViewById<EditText>(R.id.name)
            val message = d.findViewById<TextView>(R.id.message)
            val phone = d.findViewById<EditText>(R.id.phone)
            val travelHistory = d.findViewById<EditText>(R.id.travel_history)
            val cancel = d.findViewById<Button>(R.id.cancel)
            val confirm = d.findViewById<Button>(R.id.confirm)

            message.text = getString(R.string.report_confirmation, status.toString())
            name.setText(state.name)
            phone.setText(state.phone)
            travelHistory.setText(state.travelHistory)

            name.doOnTextChanged { text, _, _, _ ->
                viewModel.setName(text.toString())
            }

            phone.doOnTextChanged { text, _, _, _ ->
                viewModel.setPhone(text.toString())
            }

            travelHistory.doOnTextChanged { text, _, _, _ ->
                viewModel.setTravelHistory(text.toString())
            }

            cancel.setOnClickListener {
                d.dismiss()
            }
            confirm.setOnClickListener {
                if (Hawk.contains(Const.DEVICE_ID)) {
                    phone.required("Nomor Telepon Harus diisi") {
                        name.required("Nama harus diisi") {
                            postReport()
                            d.dismiss()
                        }
                    }
                } else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.oops))
                        .setMessage(getString(R.string.no_device_id))
                        .setPositiveButton(getString(R.string.understand)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setIcon(R.mipmap.ic_launcher)
                        .show()
                }
            }
            d.show()
        }
    }

    private fun postReport() = withState(viewModel) { state ->
        state.phone?.let { phone ->
            state.name?.let { name ->
                viewModel.postChangeStatus(
                    ChangeStatusRequest(
                        device_id = Hawk.get(Const.DEVICE_ID),
                        health = state.status.getValue(),
                        phone = phone,
                        travelHistory = state.travelHistory,
                        name = name
                    )
                )
            }
        }
    }
}