package com.mufiid.pilwali2020.presenters

import com.mufiid.pilwali2020.api.ApiClient
import com.mufiid.pilwali2020.views.IConfigView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class ConfigPresenter(private val configView: IConfigView) {
    fun config() {
        configView.isLoadingConfig()
        ApiClient.instance().getConfig()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when(it.status) {
                    200 -> {
                        configView.getSuccessConfig(it.message, it.data!!)
                    }
                    else -> {
                        configView.getFailedConfig(it.message)
                    }
                }
                configView.hideLoadingConfig()
            }, {
                configView.getFailedConfig(it.message)
                configView.hideLoadingConfig()
            })
    }
}