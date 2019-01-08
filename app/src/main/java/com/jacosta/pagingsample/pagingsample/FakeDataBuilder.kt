package com.jacosta.pagingsample.pagingsample

import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

class FakeDataBuilder {

    private val fakeData = ArrayList<Data>()

    init {
        //create fake data set
        for (i in 0..10000) {
            fakeData.add(Data(i.toString()))
        }
    }

    fun loadInitialData(count: Int) : Flowable<FakeDataResponse> {

        val fakeResponse = FakeDataResponse(
            fakeData = fakeData.subList(0, count),
            fakePageKey = count
        )

        return Flowable.just(fakeResponse)
            .delay(300, TimeUnit.MILLISECONDS)
    }

    fun loadBeforeData(count: Int, beforePageKey: Int) : Flowable<FakeDataResponse> {

        var indexToLoadTo = beforePageKey + count
        var fakePageKey: Int? = indexToLoadTo

        //if we're at the end of the list
        if (indexToLoadTo > fakeData.size) {
            indexToLoadTo = fakeData.size
            fakePageKey = null
        }

        val fakeResponse = FakeDataResponse(
            fakeData = fakeData.subList(beforePageKey, indexToLoadTo),
            fakePageKey = fakePageKey
        )

        return Flowable.just(fakeResponse)
            .delay(300, TimeUnit.MILLISECONDS)
    }
}

data class FakeDataResponse(
    val fakeData: List<Data>? = null,

    val fakePageKey: Int? = null
)