package com.linkensky.ornet.presentation.selfcheck.questions

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.mvrx.existingViewModel
import com.airbnb.mvrx.withState
import com.linkensky.ornet.R
import com.linkensky.ornet.databinding.FragmentSelfcheckResultBinding
import com.linkensky.ornet.presentation.base.BaseEpoxyFragment
import com.linkensky.ornet.presentation.base.BaseFragment
import com.linkensky.ornet.presentation.base.MvRxEpoxyController
import com.linkensky.ornet.presentation.selfcheck.SelfcheckViewModel

@BindingAdapter("android:src")
fun setImageResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}

class SelfcheckResult : BaseEpoxyFragment<FragmentSelfcheckResultBinding>() {

    override var fragmentLayout = R.layout.fragment_selfcheck_result
    private val viewModel: SelfcheckViewModel by existingViewModel()

    private val controller by lazy {
        SelfcheckResultController(viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            setOnClose {
                view.findNavController().popBackStack(R.id.homeFragment, false)
                viewModel.clearAllState()
            }
            setOnRetake { viewModel.clearAllState() }
        }
    }

    override fun invalidate() {
        recyclerView.requestModelBuild()
        
        withState(viewModel) {
            binding.apply {
                result = it.status.toString()
                illustration = it.status.getDrawable()
            }
        }
    }

    override fun epoxyController() = controller
}