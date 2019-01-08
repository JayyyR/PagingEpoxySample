package com.jacosta.pagingsample.pagingsample

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView

@ModelView(defaultLayout = R.layout.sample_data_view_in_rv)
class SampleDataView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    TextView(context, attrs, defStyleAttr) {

    @ModelProp
    fun bindData(data: Data?) {
        text = data?.dataString
    }

}
