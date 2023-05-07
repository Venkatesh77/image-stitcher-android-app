package com.example.image.fragment.stitch

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.image.R
//import com.example.image.fragment.stitch.StitchFragmentViewModel
import com.example.image.databinding.FragmentStitchBinding
import com.example.image.utils.ViewState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class StitchFragment : Fragment() {

    private val stitchFragmentViewModel: StitchFragmentViewModel by viewModels()
    private lateinit var binding: FragmentStitchBinding
    companion object {
        private const val EXTRA_ALLOW_MULTIPLE = "android.intent.extra.ALLOW_MULTIPLE"
        private const val INTENT_IMAGE_TYPE = "image/*"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStitchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonStitch.setOnClickListener {
            chooseImages()
        }
        stitchFragmentViewModel.stitchObserver.observe(viewLifecycleOwner){
            render(it)
        }
    }

    private fun chooseImages() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
            .setType(INTENT_IMAGE_TYPE)
            .putExtra(EXTRA_ALLOW_MULTIPLE, true)
        resultLauncher.launch(intent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val clipData = data?.clipData
            val images = if (clipData != null) {
                List(clipData.itemCount) { clipData.getItemAt(it).uri }
            } else {
                listOf(data?.data!!)
            }
            if (images.count() <=1){
                Toast.makeText(requireContext(), getText(R.string.more_than_one), Toast.LENGTH_SHORT).show()
            }else{
                stitchFragmentViewModel.stitchImages(images, requireContext())
            }
        }
    }

    private fun render(state: ViewState<Bitmap>){
        when(state){
            is ViewState.Success -> {
                binding.loader.visibility = View.GONE
                binding.ivOutput.setImageBitmap(state.data)
                Toast.makeText(requireContext(), getText(R.string.success), Toast.LENGTH_SHORT).show()
            }
            is ViewState.Failure -> {
                binding.loader.visibility = View.GONE
                Toast.makeText(requireContext(), getText(R.string.error), Toast.LENGTH_SHORT).show()
            }
            is ViewState.Loading -> {
                binding.loader.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stitchFragmentViewModel.clearTempImages(requireContext())
    }
}