package com.linkensky.ornet.presentation.selfcheck.questions

import com.airbnb.mvrx.withState
import com.linkensky.ornet.R
import com.linkensky.ornet.presentation.base.MvRxEpoxyController
import com.linkensky.ornet.presentation.selfcheck.SelfcheckViewModel
import com.linkensky.ornet.testSummary
import com.linkensky.ornet.utils.resString

class SelfcheckResultController(private val viewModel: SelfcheckViewModel) : MvRxEpoxyController() {
    override fun buildModels() = withState(viewModel) {
        testSummary {
            id("question-1")
            question(R.string.quest_1.resString())
            answer(if (it.hasFever) "Ya" else "Tidak")
        }

        testSummary {
            id("question-2")
            question(R.string.quest_2.resString())
            val ans = arrayListOf<String>()
            if (it.hasCough) ans.add("Batuk")
            if (it.hasFlu) ans.add("Pilek")
            if (it.hasBreathProblem) ans.add("Sesak Nafas")
            if (it.hasSoreThroat) ans.add("Sakit Tenggorokan")
            answer(if (ans.isNotEmpty()) ans.joinToString(separator = ",") else "Tidak Ada")
        }

        testSummary {
            id("question-3")
            question(R.string.quest_3.resString())
            answer(if (it.hasFever) "Ya" else "Tidak")
        }

        testSummary {
            id("question-4")
            question(R.string.quest_4.resString())
            answer(if (it.hasFever) "Ya" else "Tidak")
        }

        testSummary {
            id("question-5")
            question(R.string.quest_5.resString())
            answer(if (it.hasFever) "Ya" else "Tidak")
        }

        testSummary {
            id("question-result")
            question(R.string.result.resString())
            answer(it.status.toString())
        }
    }
}