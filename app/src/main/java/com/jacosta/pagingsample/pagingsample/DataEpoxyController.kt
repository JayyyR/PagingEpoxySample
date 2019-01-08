package com.jacosta.pagingsample.pagingsample

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController

class DataEpoxyController: PagedListEpoxyController<Data>() {
    override fun buildItemModel(currentPosition: Int, item: Data?): EpoxyModel<*> {
        return SampleDataViewModel_()
            .id(item?.dataString)
            .bindData(item)
    }


}