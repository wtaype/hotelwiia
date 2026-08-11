// Hardware.kt — Utilidades para Hardware Nativo (Audio & Monitor de Red)
package com.hotelwii.core.kidev

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File

/**
 * 🎙️ WiAudioRecorder — Grabador y reproductor de voz comprimido AAC (.m4a)
 */
class WiAudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    var outputFile: File? = null

    fun startRecording(): File {
        val file = File(context.cacheDir, "audio_note_${System.currentTimeMillis()}.m4a")
        outputFile = file

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        return file
    }

    fun stopRecording() {
        try {
            recorder?.stop()
            recorder?.release()
        } catch (e: Exception) {
        } finally {
            recorder = null
        }
    }

    fun playAudio(onComplete: () -> Unit) {
        outputFile?.let { file ->
            if (!file.exists()) return
            player?.release()
            player = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                setOnCompletionListener { onComplete() }
                start()
            }
        }
    }

    fun stopAudio() {
        try {
            player?.stop()
            player?.release()
        } catch (e: Exception) {
        } finally {
            player = null
        }
    }
}

/**
 * 🌐 Monitor en tiempo real de estado de Internet
 */
@Composable
fun rememberNetworkStatus(): State<Boolean> {
    val context = LocalContext.current
    val isConnected = remember { mutableStateOf(true) }

    DisposableEffect(context) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { isConnected.value = true }
            override fun onLost(network: Network) { isConnected.value = false }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm.registerNetworkCallback(request, callback)

        onDispose { cm.unregisterNetworkCallback(callback) }
    }

    return isConnected
}
