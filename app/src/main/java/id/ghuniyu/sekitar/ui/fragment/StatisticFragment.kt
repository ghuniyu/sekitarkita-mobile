package id.ghuniyu.sekitar.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.utils.Formatter
import id.ghuniyu.sekitar.BuildConfig
import id.ghuniyu.sekitar.data.callback.CountryStatsCallback
import id.ghuniyu.sekitar.data.callback.ProvinceStatsCallback
import id.ghuniyu.sekitar.data.model.Country
import id.ghuniyu.sekitar.data.model.Province
import id.ghuniyu.sekitar.data.remote.PublicClient
import id.ghuniyu.sekitar.ui.adapter.ProvinceAdapter
import kotlinx.android.synthetic.main.fragment_statistic.*
import retrofit2.Response

class StatisticFragment : BaseFragment() {

    private val apiURL = Formatter.redacted(BuildConfig.APP_KAWALCRN_API)

    override fun getLayout(): Int = R.layout.fragment_statistic

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        source_link.text = apiURL
        fetchCountry()
        fetchProvinces()
    }


    private fun fetchCountry() {
        PublicClient(apiURL)
            .service
            .getIndonesia()
            .enqueue(object : CountryStatsCallback(requireContext()) {
                override fun onSuccess(response: Response<List<Country>>) {
                    super.onSuccess(response)
                    response.body()?.let {
                        if (!it.isNullOrEmpty()) {
                            val indonesia = it.first()
                            positive_number.text = indonesia.positif
                            recover_number.text = indonesia.sembuh
                            death_number.text = indonesia.meninggal
                        }
                    }
                }
            })
    }

    private fun fetchProvinces() {
        PublicClient(apiURL)
            .service
            .getProvinces()
            .enqueue(object : ProvinceStatsCallback(requireContext()) {
                override fun onSuccess(response: Response<List<Province>>) {
                    super.onSuccess(response)
                    populateProvince(response.body())
                }
            })
    }

    private fun populateProvince(data: List<Province>?) {
        if (!data.isNullOrEmpty()) {
            val adapter =
                ProvinceAdapter(R.layout.item_province_detail, data)
            province_recycler.layoutManager = LinearLayoutManager(context)
            province_recycler.adapter = adapter
        }
    }
}