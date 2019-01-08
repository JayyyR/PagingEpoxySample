package com.jacosta.pagingsample.pagingsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val epoxyController = DataEpoxyController()
    private val compositeDisposable = CompositeDisposable()
    private val dataManager = DataManager(compositeDisposable)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeRecyclerView()
        setUpAddButton()

        //listen for data
        val disposable = dataManager.sampleData.subscribe {
            updateDataView(it)
        }
        compositeDisposable.add(disposable)

        //load the sample data initially
        dataManager.getLatestData()



    }

    private fun setUpAddButton() {
        val addButton = add_data_button

        addButton.setOnClickListener {
            dataManager.addData()

            //wait until our new data is added to our recyclerview, then scroll to the bottom of our list
            epoxyController.listenForNextModelBuild {
                val newItemCount = recycler_view.adapter?.itemCount
                newItemCount?.let {
                    recycler_view.scrollToPosition(newItemCount - 1)
                }
            }
        }
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
