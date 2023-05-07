package com.example.image.fragment.stitch

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.image.utils.FileUtil
import com.example.image.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import javax.inject.Inject

@HiltViewModel
class StitchFragmentViewModel @Inject constructor(): ViewModel() {
    val stitchObserver = MutableLiveData<ViewState<Bitmap>>()
    lateinit var files: List<Uri>

    fun stitchImages(uris: List<Uri>, context: Context) {
        stitchObserver.postValue(ViewState.Loading)
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val files = FileUtil(context).urisToFiles(uris)
                val options = BitmapFactory.Options()
                options.inScaled = false // Leaving it to true enlarges the decoded image size.
                val matList = listOf<Mat>().toMutableList()
                files.forEach { file ->
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)
                    val mat = Mat()
                    Utils.bitmapToMat(bitmap, mat)
                    matList.add(mat)
                }
                val outputBitmap: Bitmap? = stitchMatImages(matList)
                outputBitmap?.let {
                    stitchObserver.postValue(ViewState.Success(it))
                }
            } catch (ex: Exception) {
                Log.e("error", ex.message.toString())
                stitchObserver.postValue(ViewState.Failure(ex.message.toString()));
            }
        }
    }

    private fun stitchMatImages(src: List<Mat?>?): Bitmap? {
        val dst = Mat()
        Core.hconcat(src, dst)
        val imgBitmap = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(dst, imgBitmap)
        return imgBitmap
    }

    fun clearTempImages(context: Context){
        FileUtil(context).cleanUpWorkingDirectory()
    }
}
