package com.example.timer_kt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val seekBar : SeekBar by lazy { findViewById(R.id.seekBar)}
    private val remainMinutesTextView: TextView by lazy { findViewById(R.id.remainMinutesTextView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun bindViews(){
        seekBar.setOnClickListener(
                 object : SeekBar.OnSeekBarChangeListener{
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            //프로그레스바를 조정하고 있으면 초를 -으로 맞춰주기 위해 추가 (텍스트뷰 갱신)
                            updateRemainTimes(proress*60*1000L)
                        }
                    }

                     override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        // 조정하기 시작하면 기존 타이머가 있을 때 cancel 후 null
                         stopCountDown()
                     }

                     override fun onStopTrackingTouch(seekBar: SeekBar?) {
                         seekBar ?: return

                         if (seekBar.progress == 0) {
                             stopCountDown()
                         } else {
                             startCountDown()
                         }
                     }
                 }
        )
    }

    private fun updateRemainTimes(remainMills:Long) {
        val remainSeconds = remainMills / 1000

        remainMinutesTextView.text = "%02d".format(remainSeconds / 60)
        remainMinutesTextView.text = "%02d".format(remainSeconds % 60)
    }
}