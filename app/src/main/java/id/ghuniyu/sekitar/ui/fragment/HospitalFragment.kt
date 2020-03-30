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
import id.ghuniyu.sekitar.data.model.Hospital
import id.ghuniyu.sekitar.data.response.BaseCollectionResponse
import id.ghuniyu.sekitar.ui.adapter.HospitalAdapter
import kotlinx.android.synthetic.main.fragment_search_recycle_view.*
import retrofit2.Response

class HospitalFragment : BaseFragment() {

    override fun getLayout(): Int = R.layout.fragment_search_recycle_view

    private var hospitals: List<Hospital> = emptyList()
    lateinit var adapter: HospitalAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchHospitals()
        textSearch.hint = "Rumah Sakit / Kota / Provinsi"
        textSearch.doOnTextChanged { text, _, _, _ ->
            filterHospital(text.toString().toLowerCase())
        }
    }

    private fun fetchHospitals() {
        Client.service.getHospital().enqueue(object :
            CollectionCallback<List<Hospital>>(requireContext()) {
            override fun onSuccess(response: Response<BaseCollectionResponse<List<Hospital>>>) {
                super.onSuccess(response)
                response.body()?.data?.let {
                    hospitals = it
                    populateHospital(it.toMutableList())
                }
            }
        })
    }

    private fun filterHospital(text: String) {
        if (::adapter.isInitialized) {
            adapter.replaceData(hospitals.filter {
                it.name.toLowerCase().contains(text) || it.area_detail.toLowerCase().contains(text) || text.isEmpty()
            })
            adapter.notifyDataSetChanged()
        }

    }

    private fun populateHospital(data: List<Hospital>) {
        if (data.isNotEmpty()) {
            adapter = HospitalAdapter(R.layout.item_card, data)
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
                    R.id.imageMap -> {
                        if (item.latitude != null && item.longitude != null) {
                            val gmmIntentUri: Uri = Uri
                                .parse("geo:${item.latitude},${item.longitude}?q=${item.latitude},${item.longitude}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            startActivity(mapIntent)
                        }
                    }
                }
            }
        }
    }
}