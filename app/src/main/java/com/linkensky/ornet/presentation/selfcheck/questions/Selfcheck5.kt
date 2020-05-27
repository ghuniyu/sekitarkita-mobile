package com.linkensky.ornet.presentation.selfcheck.questions

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.airbnb.mvrx.*
import com.linkensky.ornet.Const
import com.linkensky.ornet.R
import com.linkensky.ornet.data.model.ReportDataRequest
import com.linkensky.ornet.data.model.enums.Status
import com.linkensky.ornet.databinding.FragmentSelfcheck5Binding
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.selfcheck.SelfcheckState
import com.linkensky.ornet.presentation.selfcheck.SelfcheckViewModel
import com.linkensky.ornet.utils.Formatter
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.fragment_selfcheck_5.*

class Selfcheck5 : BaseFragment<FragmentSelfcheck5Binding>() {

    private val viewModel: SelfcheckViewModel by existingViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            page = "5 dari 5"
            lifecycleOwner = viewLifecycleOwner
            setOnNext { calculate() }
            setOnBack { viewModel.prevPage() }

            setOnYes { viewModel.directContact(true) }
            setOnNo { viewModel.directContact(false) }
        }

        viewModel.asyncSubscribe(
            subscriptionLifecycleOwner,
            SelfcheckState::responseStoreTest,
            onSuccess = {
                binding.apply {
                    isLoading = false
                }
                Hawk.put(Const.SELF_TEST_COMPLETED, true)
                viewModel.nextPage()
            },
            onFail = {
                binding.apply {
                    isLoading = false
                }
                AlertDialog.Builder(requireContext())
                    .setTitle("Oops!")
                    .setMessage("Terjadi kesalahan, coba ulangi lagi.")
                    .setPositiveButton(
                        "Ulangi"
                    ) { _, _ -> calculate() }
                    .show()
            }
        )
    }

    private fun calculate() = withState(viewModel) { s ->
        var status = Status.HEALTHY
        if ((s.hasFever && (s.hasCough || s.hasSoreThroat || s.hasFlu) && s.hasBreathProblem && (s.inInfectedCountry || s.inInfectedCity)) ||
            ((s.hasFever && (s.hasCough || s.hasSoreThroat || s.hasFlu) && s.directContact))
        ) {
            status = Status.PDP
        } else if (((!s.hasFever && !s.hasCough && !s.hasSoreThroat && !s.hasFlu && !(s.inInfectedCountry || s.inInfectedCity)) && s.directContact)) {
            status = Status.OTG
        } else if (((s.hasFever || (s.hasCough || s.hasSoreThroat || s.hasFlu)) && ((s.inInfectedCountry || s.inInfectedCity)) || s.directContact)) {
            status = Status.ODP
        } else if ((s.inInfectedCountry || s.inInfectedCity)) {
            status = Status.TRAVELER
        } else if (s.hasFever && (s.hasCough || s.hasSoreThroat || s.hasFlu) && s.hasBreathProblem && (s.inInfectedCountry || s.inInfectedCity) && s.directContact) {
            status = Status.POSITIVE
        }

        Hawk.put(Const.STORAGE_STATUS, status)
        Hawk.put(Const.NAME, s.name)
        Hawk.put(Const.PHONE, s.phone)

        viewModel.setStatus(status)
        viewModel.storeReportTest(ReportDataRequest(
            device_id = Hawk.get(Const.DEVICE_ID),
            has_fever = s.hasFever,
            has_cough = s.hasCough,
            has_breath_problem = s.hasBreathProblem,
            has_direct_contact = s.directContact,
            has_flu = s.hasFlu,
            has_in_infected_city = s.inInfectedCity,
            has_in_infected_country = s.inInfectedCountry,
            has_sore_throat = s.hasSoreThroat,
            result = status.getValue(),
            name = s.name!!,
            phone = s.phone!!
        ))
    }

    override fun getLayoutRes() = R.layout.fragment_selfcheck_5

    override fun invalidate() = withState(viewModel) {
        if (it.directContact) {
            yes.setIconResource(R.drawable.ic_check_circle)
            no.setIconResource(0)
        } else {
            yes.setIconResource(0)
            no.setIconResource(R.drawable.ic_check_circle)
        }

        if(it.responseStoreTest is Loading) {
            binding.apply {
                isLoading = true
            }
        }
    }
}