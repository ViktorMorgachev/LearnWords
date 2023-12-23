package com.learn.worlds.utils

import android.content.Context
import android.media.MediaPlayer
import androidx.core.net.toUri
import com.learn.worlds.di.MainDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(DelicateCoroutinesApi::class)
@Singleton
class AudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
    @MainDispatcher private val dispatcher: CoroutineDispatcher,){

    private val scope = CoroutineScope(GlobalScope.coroutineContext + dispatcher)

    private var player: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    fun play(file: File){
        try {
            if (player == null) {
                player = MediaPlayer.create(context, file.toUri()).apply {
                    start()
                }
            } else player?.start()
            player?.setOnCompletionListener {
                scope.launch {
                    _isPlaying.emit(false)
                }
            }
            player?.setOnErrorListener { mp, what, extra ->
                Timber.e("what: $what extra: $extra")
                scope.launch {
                    _isPlaying.emit(false)
                }
                true
            }
            scope.launch {
                _isPlaying.emit(true)
            }
        } catch (t: Throwable){
            Timber.e(t)
            scope.launch {
                _isPlaying.emit(false)
            }
        }

    }

    fun pause(){
        if (player?.isPlaying == true) {
            player?.pause()
            scope.launch {
                _isPlaying.emit(false)
            }
        }
    }
    fun release(){
        if (player != null) {
            player!!.release()
            player = null
        }
        scope.launch {
            _isPlaying.emit(false)
        }
    }

    // When leave current screen
    fun stopSound(){
        if (player != null) {
            player!!.stop()
            player!!.release()
            player = null
        }
        scope.launch {
            _isPlaying.emit(false)
        }
    }
}