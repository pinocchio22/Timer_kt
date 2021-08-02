package com.example.timer_kt

import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val seekBar: SeekBar by lazy { findViewById(R.id.seekBar) }
    private val remainMinutesTextView: TextView by lazy { findViewById(R.id.remainMinutesTextView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun bindViews() {
        seekBar.setOnClickListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            //프로그레스바를 조정하고 있으면 초를 -으로 맞춰주기 위해 추가 (텍스트뷰 갱신)
                            updateRemainTimes(proress * 60 * 1000L)
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

    private fun updateRemainTimes(remainMills: Long) {
        val remainSeconds = remainMills / 1000

        remainMinutesTextView.text = "%02d".format(remainSeconds / 60)
        remainMinutesTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun startCountDown() {
        // 사용자가 바에서 손을 떼는 순간 새로운 타이머 생성
        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
        currentCountDownTimer?.start()

        // 소리 재생
        // 디바이스 자체에 요청이기 때문에 화면 종료시에도 계속 재생
        // 생명주기에 따라 처리 필요
        tickingSoundld?.let { soundld ->
            soundPool.play(soundld, 1F, 1F, 0, -1, 1F)
        }
    }

    private fun createCountDownTimer(initialMillis: Long): CountDownTimer =
            object : CountDownTimer(initialMillis, 1000L) {
                override fun onTick(millisUntilFinished: Long) {
                    updateRemainTimes(millisUntilFinished)
                    updateSeekBar(millisUntilFinished)
                }

                override fun onFinish() {
                    completeCountDown()
                }
            }

    private fun updateSeekBar(remainMills: Long) {
        seekBar.progress = (remainMills / 1000 / 60).toInt() //분
    }

    private fun completeCountDown() {
        updateRemainTimes(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSound?.let { soundld ->
            soundPool.play(soundld, 1F, 1F, 0, 0, 1F)
        }
    }

    private fun stopCountDown() {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPoll.autoPause()
    }

    private val soundPool = SoundPool.Builder().build()

    private fun initSounds() {
        // sound 로드
        tickingSoundld = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundld = soundPool.load(this, R.raw.timer_bell, 1)

        tickingSoundld?.let { soundld ->
            soundPool.play(soundld, 1F, 1F, 0, -1, 1F)
        }

        soundPool.autoPause()
    }

    override fun onResume() {
        super.onResume()
        // 앱이 다시 시작되는 경우
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        // 앱이 화면에 보이지 않을 경우
        // soundPool.pause() // 특정 스트림 아이디로 정지
        soundPool.autoPause() // 모든 활성 스트림 정지
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release() // 더이상 필요 없으면 사운드풀 메모리에서 해제
    }
}