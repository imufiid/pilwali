package com.mufiid.pilwali2020.presenters

import com.mufiid.pilwali2020.api.ApiClient
import com.mufiid.pilwali2020.views.IPilwaliView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class PilwaliPresenter(private val pilwaliView: IPilwaliView) {
    fun getVerification(id_tps: String?) {
        pilwaliView.isLoadingPilwali()
        ApiClient.instance().getVerifikasi(id_tps)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when(it.status) {
                    200 -> {
                        pilwaliView.success(it.message, it.data)
                    }
                    else -> {
                        pilwaliView.failed(it.message)
                    }
                }
                pilwaliView.hideLoadingPilwali()
            }, {
                pilwaliView.failed(it.message)
                pilwaliView.hideLoadingPilwali()
            })
    }

}