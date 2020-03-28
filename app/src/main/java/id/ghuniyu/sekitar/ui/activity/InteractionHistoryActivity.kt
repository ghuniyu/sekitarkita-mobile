package id.ghuniyu.sekitar.ui.activity

import Client
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.orhanobut.hawk.Hawk
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.data.callback.InteractionHistoryCallback
import id.ghuniyu.sekitar.data.model.Interaction
import id.ghuniyu.sekitar.data.request.InteractionHistoryRequest
import id.ghuniyu.sekitar.data.response.InteractionHistoryResponse
import id.ghuniyu.sekitar.ui.adapter.InteractionAdapter
import id.ghuniyu.sekitar.utils.Constant
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