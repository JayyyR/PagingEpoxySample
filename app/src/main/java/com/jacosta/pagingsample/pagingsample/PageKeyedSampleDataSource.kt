package com.jacosta.pagingsample.pagingsample

import androidx.paging.PageKeyedDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PageKeyedSampleDataSource(
    private val initialDataSet: ArrayList<Data> = ArrayList(),
    private val updateBackingDataCallback: ((List<Data>, Int?) -> Unit)?,
    private val initialBeforeKey: Int?,
    private val compositeDisposable: CompositeDisposable
    ) : PageKeyedDataSource<Int, Data>(){

    private val fakeAPI = FakeDataBuilder()

    private fun hasDataLoadedBefore() = !initialDataSet.isEmpty()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Data>) {
        if (hasDataLoadedBefore()) {
            callback.onResult(initialDataSet, initialBeforeKey, null)
        } else {
            val count = params.requestedLoadSize
            loadInitialFromFakeAPI(count, callback)
        }
    }


    private fun loadInitialFromFakeAPI(count: Int, callback: LoadInitialCallback<Int, Data>) {
        val disposable = fakeAPI.loadInitialData(count)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {response ->
                val dataList = response.fakeData ?: arrayListOf()
                val beforeKey = response.fakePageKey

                //update backing data
                updateBackingDataCallback?.invoke(dataList, beforeKey)

                callback.onResult(dataList, beforeKey, null)
            }

        compositeDisposable.add(disposable)

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Data>) {
        //not used
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Data>) {
        val before = params.key
        val count = params.requestedLoadSize

        val disposable = fakeAPI.loadBeforeData(count, before)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { response ->
                val dataList = response.fakeData ?: arrayListOf()
                val beforeKey = response.fakePageKey

                //update backing data
                updateBackingDataCallback?.invoke(dataList, beforeKey)

                callback.onResult(dataList, beforeKey)
            }

        compositeDisposable.add(disposable)


    }


}