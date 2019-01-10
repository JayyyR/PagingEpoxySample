package com.jacosta.pagingsample.pagingsample

import com.airbnb.epoxy.DiffResult
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.OnModelBuildFinishedListener
import com.airbnb.epoxy.paging.PagedListEpoxyController

class DataEpoxyController: PagedListEpoxyController<Data>() {
    override fun buildItemModel(currentPosition: Int, item: Data?): EpoxyModel<*> {
        return SampleDataViewModel_()
            .id(item?.dataString)
            .bindData(item)
    }




}

inline fun EpoxyController.listenForNextModelBuild(crossinline callback: (DiffResult) -> Unit) {
    val modelBuildListener = object : OnModelBuildFinishedListener {
        override fun onModelBuildFinished(result: DiffResult) {
            callback.invoke(result)
            this@listenForNextModelBuild.removeModelBuildListener(this)
        }
    }
    this.addModelBuildListener(modelBuildListener)
}