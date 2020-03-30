package id.ghuniyu.sekitar.ui.fragment

import id.ghuniyu.sekitar.data.remote.Client
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.data.callback.CollectionCallback
import id.ghuniyu.sekitar.data.model.CallCenter
import id.ghuniyu.sekitar.data.response.BaseCollectionResponse
import id.ghuniyu.sekitar.ui.adapter.CallCenterAdapter
import kotlinx.android.synthetic.main.fragment_search_recycle_view.*
import retrofit2.Response

class CallCenterFragment : BaseFragment() {

    override fun getLayout(): Int = R.layout.fragment_search_recycle_view

    private var callcenter: List<CallCenter> = emptyList()
    lateinit var adapter: CallCenterAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchCallCenter()
        textSearch.hint = "Provinsi / Kota / Kecamatan"
        textSearch.doOnTextChanged { text, _, _, _ ->
            filterCallCenter(text.toString().toLowerCase())
        }
    }

    private fun fetchCallCenter() {
        Client.service.getCallCenter().enqueue(object :
            CollectionCallback<List<CallCenter>>(requireContext()) {
            override fun onSuccess(response: Response<BaseCollectionResponse<List<CallCenter>>>) {
                super.onSuccess(response)
                response.body()?.data?.let {
                    callcenter = it
                    populateHospital(it.toMutableList())
                }
            }
        })
    }

    private fun filterCallCenter(text: String) {
        if (::adapter.isInitialized) {
            adapter.replaceData(callcenter.filter {
                it.area_detail.toLowerCase().contains(text) || text.isEmpty()
            })
            adapter.notifyDataSetChanged()
        }

    }

    private fun populateHospital(data: List<CallCenter>) {
        if (data.isNotEmpty()) {
            adapter = CallCenterAdapter(R.layout.item_card, data)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter

            adapter.setOnItemChildClickListener { _, view, i ->
                val item = data[i]
                when (view.id) {
                    R.id.phone -> {
                        item.phone.let {
                            val callIntetUri: Uri = Uri
                                .parse("tel:${it}")
                            val telIntent = Intent(Intent.ACTION_DIAL, callIntetUri)
                            startActivity(telIntent)
                        }
                    }
                    R.id.area -> {
                        item.website.let {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(it)
                            )
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}