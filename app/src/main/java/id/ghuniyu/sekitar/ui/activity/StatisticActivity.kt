package id.ghuniyu.sekitar.ui.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import id.ghuniyu.sekitar.BuildConfig
import id.ghuniyu.sekitar.R
import id.ghuniyu.sekitar.data.callback.CountryStatsCallback
import id.ghuniyu.sekitar.data.callback.ProvinceStatsCallback
import id.ghuniyu.sekitar.data.model.Country
import id.ghuniyu.sekitar.data.model.Province
import id.ghuniyu.sekitar.data.remote.PublicClient
import id.ghuniyu.sekitar.ui.adapter.ProvinceAdapter
import id.ghuniyu.sekitar.utils.Formatter
import kotlinx.android.synthetic.main.activity_statistic.*
import retrofit2.Response

class StatisticActivity : BaseActivity() {
    private val apiURL = Formatter.redacted(BuildConfig.APP_KAWALCRN_API)

    override fun getLayout() = R.layout.activity_statistic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        source_link.text = apiURL
        fetchCountry()
        fetchProvinces()
    }

    private fun fetchCountry() {
        PublicClient(apiURL)
            .service
            .getIndonesia()
            .enqueue(object : CountryStatsCallback(this) {
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
            .enqueue(object : ProvinceStatsCallback(this) {
                override fun onSuccess(response: Response<List<Province>>) {
                    super.onSuccess(response)
                    populateProvince(response.body())
                }
            })
    }

    private fun populateProvince(data: List<Province>?) {
        if (!data.isNullOrEmpty()) {
            val adapter = ProvinceAdapter(R.layout.item_province_detail, data)
            province_recycler.layoutManager = LinearLayoutManager(this)
            province_recycler.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        fetchCountry()
        fetchProvinces()
    }
}