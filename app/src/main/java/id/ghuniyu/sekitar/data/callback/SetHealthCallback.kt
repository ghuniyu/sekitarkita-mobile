package id.ghuniyu.sekitar.data.callback

import android.content.Context
import android.util.Log
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import id.ghuniyu.sekitar.data.response.SetHealthResponse
import id.ghuniyu.sekitar.utils.Constant
import retrofit2.Response

open class SetHealthCallback(private val context: Context) :
    DefaultCallback<SetHealthResponse>(context) {
    override fun onSuccess(response: Response<SetHealthResponse>) {
        super.onSuccess(response)

        response.body()?.let {
            it.success?.let { success ->
                if (success){
                    Hawk.put(Constant.STORAGE_STATUS, it.device.health_condition)
                    Toasty.success(context, "Laporan Berhasil diKirim").show()
                }else
                    Toasty.error(context, "Laporan Gagal diKirim").show()
            }
        }
    }
}