package com.example.androidproject.navigaiton

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.androidproject.MainActivity
import com.example.androidproject.R
import com.example.androidproject.databinding.HomeDetailBinding
import com.example.androidproject.model.Content

class DetailFragment : Fragment() {
    private lateinit var binding: HomeDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeDetailBinding.inflate(inflater)
        val data = arguments?.getParcelable("data") as? Content
        if (data != null){
            binding.userNameTv.text = data.userId
            binding.smallUserNameTv.text = data.userId
            binding.contentDescriptionTv.text = data.explain
            val likesCount = data.favoriteCount
            val likesText = getString(R.string.like, likesCount)
            binding.likesTv.text = likesText
            Glide.with(this).load(data.imageUrl).into(binding.postIv)
            val time = formatTimeAgo(data.timestamp!!)
            binding.timepassedTv.text = time
        }
        return binding.root
    }
    private fun formatTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        return DateUtils.getRelativeTimeSpanString(timestamp, now, DateUtils.MINUTE_IN_MILLIS)
            .toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var toolbar = (requireActivity() as AppCompatActivity).supportActionBar
        toolbar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.title = "탐색 탭"
    }

    override fun onDestroy() {
        super.onDestroy()
        var toolbar = (requireActivity() as AppCompatActivity).supportActionBar
        toolbar?.title = "instagram"
        toolbar?.setDisplayHomeAsUpEnabled(false)
    }
}