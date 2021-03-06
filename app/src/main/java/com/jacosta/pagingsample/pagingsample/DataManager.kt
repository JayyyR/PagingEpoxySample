package com.jacosta.pagingsample.pagingsample

import android.os.Handler
import android.os.Looper
import androidx.paging.PagedList
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DataManager(private val compositeDisposable: CompositeDisposable) {

    private lateinit var pageKeyedDataSource: PageKeyedSampleDataSource
    private val dataSourceSubscriptions = CompositeDisposable()
    private val networkExecutor = Executors.newFixedThreadPool(5)
    val sampleData: BehaviorSubject<PagedList<Data>> = BehaviorSubject.create()


    //backing data set that we keep so we can add and manipulate our pagedList dataset.
    //we cannot directly edit a paged list so this becomes necessary. When we need to make changes to our data
    //we will edit our backing data set and create a new PageKeyedSampleDataSource
    private var backingDataSet = ArrayList<Data>()
    private var latestBeforeKey: Int? = null

    //this is a callback that will get triggered when our data set gets updated via the fake API
    //This is so we can keep our backing data in sync with the data that comes from our API
    private val updateBackingData: (List<Data>, Int?) -> Unit = {dataToAdd, beforeKey ->
        backingDataSet.addAll(0, dataToAdd)
        latestBeforeKey = beforeKey
    }

    //just to add data in order
    var addDataCount = -1

    init {
        createNewMessageAPIDataSource()
    }

    /**
     * Create a new [PageKeyedSampleDataSource]. This should get called any time we make a local change
     * to our backing data set. This will pass in our updated backing data to our new [PageKeyedSampleDataSource]
     * so that is used instead of a brand new call to the API. Keeping [latestBeforeKey] up to date will ensure
     * the API will pull from the correct API source as the user scrolls up, even if we make local changes.
     */
    private fun createNewMessageAPIDataSource() {

        //clear subscriptions to old data source (if it existed)
        dataSourceSubscriptions.clear()

        //create new data source with updated backing data (if it exists)
        pageKeyedDataSource = PageKeyedSampleDataSource(
            initialDataSet = backingDataSet.map { it }, //make sure to pass a copy of the list not the list directly (that creates odd behavior)
            initialBeforeKey = latestBeforeKey,
            compositeDisposable = dataSourceSubscriptions,
            updateBackingDataCallback = updateBackingData
        )
    }

    /**
     * Gets and posts the latest data we have. This creates a PagedList from our data source and
     * feeds it to interested listeners
     */
    fun getLatestData() {

        //create pagedList config
        val pagedListConfig = PagedList.Config.Builder()
            .setPageSize(30)
            .setInitialLoadSizeHint(60)
            .setEnablePlaceholders(false)
            .build()

        //create paged list, trigger a fetch of our initial data load
        val messagePagedList = PagedList.Builder(pageKeyedDataSource, pagedListConfig)
            .setFetchExecutor(networkExecutor)
            .setNotifyExecutor(MainThreadExecutor())
            .build()

        //feed paged list to our subject
        sampleData.onNext(messagePagedList)
    }

    fun addData() {

        //create and add new data
        val newData = Data(
            dataString = addDataCount.toString(),
            brandNew = true
        )
        addDataCount--

        //update data set
        backingDataSet.add(newData)
        updateDataSource()

        //simulate API call that manipulates data you just added
        val disposable = Observable.just(true)
            .delay(3100, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                markDataAsNotNew(newData.dataString)
                updateDataSource()
            }

        compositeDisposable.add(disposable)


    }

    private fun markDataAsNotNew(id: String?) {
        val updatedDataSet = backingDataSet.map { data ->
            val brandNew = if (data.dataString == id) false else data.brandNew
            data.copy(
                brandNew = brandNew
            )
        }

        backingDataSet = ArrayList(updatedDataSet)
    }



    /**
     * Creates new Data source with updated backing data and updates
     * our pagedlist so interested listeners can update appropriately to our new data
     */
    private fun updateDataSource() {
        createNewMessageAPIDataSource()
        getLatestData()
    }
}

class MainThreadExecutor : Executor {
    private val handler = Handler(Looper.getMainLooper())

    override fun execute(r: Runnable) {
        handler.post(r)
    }
}