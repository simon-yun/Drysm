package com.example.recoder

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageButton

class RecordButton (
    context: Context,
    attrs: AttributeSet
): AppCompatImageButton(context, attrs) {

    //shape drawble 재생산성을 높이기 위해서 init 안에 넣는다
    init {
        setBackgroundResource(R.drawable.shape_oval_button)
    }

fun updateIconWithState(state: State) {
    when(state) {
        State.BEFORE_RECODING ->{
            setImageResource(R.drawable.icl_record_24)
        }
        State.ON_RECORDING -> {
            setImageResource(R.drawable.ic_stop_24)
        }
        State.AFTER_RECORDING -> {
            setImageResource(R.drawable.ic_play_arrow_24)
        }
        State.ON_PLAYING -> {
            setImageResource(R.drawable.ic_play_arrow_24)
        }
    }
}

}