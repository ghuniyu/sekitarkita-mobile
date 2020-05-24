package com.linkensky.ornet.presentation.selfcheck.questions

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentSelfcheck5Binding
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.selfcheck.SelfcheckViewModel
import kotlinx.android.synthetic.main.fragment_selfcheck_5.*

class Selfcheck5 : BaseFragment<FragmentSelfcheck5Binding>() {

    private val viewModel: SelfcheckViewModel by activityViewModel()

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
    }

    private fun calculate() = withState(viewModel) { s ->
        var status = "Sehat"
        if ((s.hasFever && (s.hasCough || s.hasSoreThroat || s.hasFlu) && s.hasBreathProblem && (s.inInfectedCountry || s.inInfectedCity)) ||
            ((s.hasFever && (s.hasCough || s.hasSoreThroat || s.hasFlu) && s.directContact))
        ) {
            status = "PDP"
        } else if (((!s.hasFever && !s.hasCough && !s.hasSoreThroat && !s.hasFlu && !(s.inInfectedCountry || s.inInfectedCity)) && s.directContact)) {
            status = "OTG"
        } else if (((s.hasFever || (s.hasCough || s.hasSoreThroat || s.hasFlu)) && ((s.inInfectedCountry || s.inInfectedCity)) || s.directContact)) {
            status = "ODP"
        } else if ((s.inInfectedCountry || s.inInfectedCity)) {
            status = "Traveler"
        }
        context?.let {
            AlertDialog.Builder(it)
                .setTitle("Hasil Pemeriksaan Mandiri")
                .setMessage("Dari Jawaban anda, maka Calculator menyimpulkan bahwa Anda $status ...")
                .setPositiveButton("Tutup", null)
                .show()
        }
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
    }
}