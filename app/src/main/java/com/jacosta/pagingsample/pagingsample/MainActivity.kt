package com.jacosta.pagingsample.pagingsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val epoxyController = DataEpoxyController()
    private val dataManager = DataManager()
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeRecyclerView()

        //listen for data
        val disposable = dataManager.sampleData.subscribe {
            updateDataView(it)
        }
        compositeDisposable.add(disposable)

        //load the sample data initially
        dataManager.getLatestData()

    }

    private fun initializeRecyclerView() {
        val recyclerView = recycler_view
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        recyclerView.setItemSpacingDp(3)
        recyclerView.setController(epoxyController)
    }

    private fun updateDataView(data: PagedList<Data>) {
        epoxyController.submitList(data)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
