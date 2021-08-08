package com.example.recoder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.jar.Manifest
import android.*
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private val visualizedView: SoundVisualizedView by lazy {
        findViewById(R.id.soundvisuallizer)
    }
    private val resetButton: Button by lazy {
        findViewById(R.id.resetbutton)
    }

    private val recordButton: RecordButton by lazy {
        findViewById(R.id.recordbutton)
    }


    private val requeriedPermisions = arrayOf(android.Manifest.permission.RECORD_AUDIO)

    private val recordingFilePath: String by lazy {
        // 코틀린 스타일이 아님 externalCacheDir?.absolutePath + "/recording.3gp"
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }

    private  val recordTimeTextView: CountUpView by lazy {
        findViewById(R.id.recordTimeTextview)
    }
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var state = State.BEFORE_RECODING
        set(value) {
            field = value
            resetButton.isEnabled = (value == State.AFTER_RECORDING) ||
                    (value == State.ON_PLAYING)
            recordButton.updateIconWithState(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestAudioPermission()
        initViews()
        bindViews()
        initVariables()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted = requestCode == REQUSET_RECORD_AUDIO_PERMISSION &&
                grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if (!audioRecordPermissionGranted) {
            finish()
        }

    }

    private fun requestAudioPermission() {

        requestPermissions(requeriedPermisions, REQUSET_RECORD_AUDIO_PERMISSION)
    }

    fun initViews() {
        recordButton.updateIconWithState(state)
    }

    //앱에 스테이트에 따른 유아이 구성 완료 --> 동작 구성 -->onclick listener을 받아야한다..
    private fun bindViews() {
        visualizedView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?: 0
        }
        resetButton.setOnClickListener {
            stopPlaying()
            visualizedView.clearVisualization()
            recordTimeTextView.clearCountUp()
            state = State.BEFORE_RECODING

        }

        recordButton.setOnClickListener {
            when (state) {
                State.BEFORE_RECODING -> {
                    startRecording()
                }
                State.ON_RECORDING -> {
                    stopRecording()
                }
                State.AFTER_RECORDING -> {
                    startPlaying()
                }
                State.ON_PLAYING -> {
                    stopPlaying()
                }
            }
        }
    }


    private fun initVariables() {
        state = State.BEFORE_RECODING
    }
    private fun startRecording() {

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            //따로 뭐 확인 안하겟지만 캐쉬 디렉토리...를 쓴데..
            setOutputFile(recordingFilePath)
            prepare()
        }
        recorder?.start()
        state = State.ON_RECORDING
        recordTimeTextView.startCountUp()
        visualizedView.startVisualizing(false)
    }

    private fun stopRecording() {
        recorder?.run {

            stop()
            release()
        }
        recorder = null
        visualizedView.stopVisualizing()
        recordTimeTextView.stopCountUp()
        state = State.AFTER_RECORDING

    }

    private fun startPlaying() {
        player = MediaPlayer()
            .apply {
                setDataSource(recordingFilePath)
                prepare()  //ui 어씽크 ..prepare a singc
            }
        player?.setOnCompletionListener {
            stopPlaying()
            state = State.AFTER_RECORDING
        }
        player?.start()
        visualizedView.startVisualizing(true)
        recordTimeTextView.startCountUp()
        state = State.ON_RECORDING
    }

    private fun stopPlaying() {
        player?.release()
        player = null
        visualizedView.stopVisualizing()
        state = State.AFTER_RECORDING
        recordTimeTextView.stopCountUp()
    }

    companion object {
        private const val REQUSET_RECORD_AUDIO_PERMISSION = 222
    }
}