package com.linkensky.ornet.data.callback

import android.content.Context
import com.orhanobut.hawk.Hawk
import es.dmoral.toasty.Toasty
import com.linkensky.ornet.data.response.SetHealthResponse
import com.linkensky.ornet.utils.Constant
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