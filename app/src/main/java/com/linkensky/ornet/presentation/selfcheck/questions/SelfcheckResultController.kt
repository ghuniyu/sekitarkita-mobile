package com.linkensky.ornet.presentation.selfcheck.questions

import com.airbnb.mvrx.withState
import com.linkensky.ornet.App
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.MvRxEpoxyController
import com.linkensky.ornet.presentation.selfcheck.SelfcheckViewModel
import com.linkensky.ornet.testSummary

class SelfcheckResultController(private val viewModel: SelfcheckViewModel) : MvRxEpoxyController() {
    override fun buildModels() = withState(viewModel) {

        testSummary {
            id("question-1")
            question(App.getContext().getString(R.string.quest_1))
            answer(if (it.hasFever) "Ya" else "Tidak")
        }

        testSummary {
            id("question-2")
            question(App.getContext().getString(R.string.quest_2))
            val ans = arrayListOf<String>()
            if (it.hasCough) ans.add("Batuk")
            if (it.hasFlu) ans.add("Pilek")
            if (it.hasBreathProblem) ans.add("Sesak Nafas")
            if (it.hasSoreThroat) ans.add("Sakit Tenggorokan")
            answer(if (ans.isNotEmpty()) ans.joinToString(separator = ",") else "Tidak Ada")
        }

        testSummary {
            id("question-3")
            question(App.getContext().getString(R.string.quest_3))
            answer(if (it.hasFever) "Ya" else "Tidak")
        }

        testSummary {
            id("question-4")
            question(App.getContext().getString(R.string.quest_4))
            answer(if (it.hasFever) "Ya" else "Tidak")
        }

        testSummary {
            id("question-5")
            question(App.getContext().getString(R.string.quest_5))
            answer(if (it.hasFever) "Ya" else "Tidak")
        }

        testSummary {
            id("question-result")
            question(App.getContext().getString(R.string.result))
            answer(it.status.toString())
        }
    }
}