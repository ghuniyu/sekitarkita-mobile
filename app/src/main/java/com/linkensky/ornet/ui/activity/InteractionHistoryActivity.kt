package com.linkensky.ornet.ui.activity

import com.linkensky.ornet.data.remote.Client
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.orhanobut.hawk.Hawk
import com.linkensky.ornet.R
import com.linkensky.ornet.data.callback.InteractionHistoryCallback
import com.linkensky.ornet.data.model.Interaction
import com.linkensky.ornet.data.request.InteractionHistoryRequest
import com.linkensky.ornet.data.response.InteractionHistoryResponse
import com.linkensky.ornet.ui.adapter.InteractionAdapter
import com.linkensky.ornet.utils.Constant
import kotlinx.android.synthetic.main.activity_history.*
import retrofit2.Response

class InteractionHistoryActivity : BaseActivity() {
    override fun getLayout() = R.layout.activity_history

    companion object {
        const val TAG = "InteractionHistoryTag"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchTangentHistory()
    }

    private fun fetchTangentHistory() {
        Client.service.postInteractionHistory(
            InteractionHistoryRequest(
                Hawk.get(Constant.STORAGE_MAC_ADDRESS)
            )
        ).enqueue(object : InteractionHistoryCallback(this) {
            override fun onSuccess(response: Response<InteractionHistoryResponse>) {
                super.onSuccess(response)
                populateTangentHistory(response.body()?.nearbies)
            }
        })
    }

    private fun populateTangentHistory(interactions: List<Interaction>?) {
        if (!interactions.isNullOrEmpty()) {
            tangent_recycler.visibility = View.VISIBLE
            empty_device.visibility = View.GONE

            val mAdapter = InteractionAdapter(R.layout.item_device, interactions)
            mAdapter.setOnItemClickListener { _, _, i ->
                val item = interactions[i]
                if (item.latitude != null && item.longitude != null) {
                    val gmmIntentUri: Uri = Uri
                        .parse("geo:${item.latitude},${item.longitude}?q=${item.latitude},${item.longitude}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                }
            }

            tangent_recycler.layoutManager = LinearLayoutManager(this)
            tangent_recycler.adapter = mAdapter
        }
    }
}