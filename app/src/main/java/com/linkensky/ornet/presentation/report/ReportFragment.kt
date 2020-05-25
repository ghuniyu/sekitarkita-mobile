package com.linkensky.ornet.presentation.report

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentReportBinding
import com.linkensky.ornet.presentation.base.BaseFragment

class ReportFragment : BaseFragment<FragmentReportBinding>() {
    private val viewModel: ReportViewModel by activityViewModel()

    override fun getLayoutRes() = R.layout.fragment_report
    override fun invalidate() {}

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
        }
    }

    private fun reportDialog(status: Status) = withState(viewModel) { state ->
        viewModel.setStatus(status)
        context?.let { ctx ->
            val d = Dialog(ctx, R.style.ReportDialog)
            d.window?.setBackgroundDrawableResource(android.R.color.transparent)
            d.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
            d.setContentView(R.layout.dialog_report)
            d.setCancelable(false)

            val message = d.findViewById<TextView>(R.id.message)
            val phone = d.findViewById<EditText>(R.id.phone)
            val travelHistory = d.findViewById<EditText>(R.id.travel_history)
            val cancel = d.findViewById<Button>(R.id.cancel)
            val confirm = d.findViewById<Button>(R.id.confirm)

            message.text = getString(R.string.report_confirmation, status.toString())
            phone.setText(state.phone)
            travelHistory.setText(state.travelHistory)

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
                viewModel.postReport()
                d.dismiss()
            }
            d.show()
        }
    }
}