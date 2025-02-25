package com.example.voicerecorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.voicerecorder.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var fileName: String = ""
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        fileName = "${externalCacheDir?.absolutePath}/audiorecord.3gp"

        val logo: ImageView = binding.logo
        val record: FloatingActionButton = binding.record
        val stop: FloatingActionButton = binding.stop
        val play: FloatingActionButton = binding.play

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (!isGranted) {
                    Toast.makeText(this, "Permission needed", Toast.LENGTH_LONG).show()
                    finish()
                }
            }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        record.setOnClickListener {
            logo.setImageResource(R.drawable.record)
            if (mediaRecorder == null) {
                mediaRecorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setOutputFile(fileName)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    try {
                        prepare()
                        start()
                    } catch (e: IOException) {
                        Log.e("RECORDING", "No es pot iniciar la gravació", e)
                    }
                }
            }
        }

        stop.setOnClickListener {
            logo.setImageResource(R.drawable.speak)
            mediaRecorder?.apply {
                stop()
                reset()
                release()
                mediaRecorder = null
            }
            mediaPlayer?.apply {
                stop()
                release()
                mediaPlayer = null
            }
        }

        play.setOnClickListener {
            logo.setImageResource(R.drawable.play)
            if (mediaRecorder == null && mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    try {
                        setDataSource(fileName)
                        prepare()
                        start()
                        setOnCompletionListener {
                            stop.callOnClick()
                        }
                    } catch (e: IOException) {
                        Log.e("RECORDING", "No es pot iniciar la reproducció", e)
                    }
                }
            }
        }
    }
}
