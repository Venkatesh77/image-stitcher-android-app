package com.example.image

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.image.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import org.opencv.android.OpenCVLoader


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var tag = "main"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(OpenCVLoader.initDebug()){
            Log.d(tag,"OpenCv configured successfully");
        } else{
            Log.d(tag,"OpenCv configured failed");
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}