package com.jacosta.pagingsample.pagingsample

class PageKeyedSampleDataSource(
    private val initialDataSet: ArrayList<Data> = ArrayList(),
    private val updateBackingDataCallback: ((ArrayList<Data>, String?) -> Unit)?,
    private val initialBeforeKey: String?
) {

    private fun hasDatLoadedBefore() = !initialDataSet.isEmpty()
}