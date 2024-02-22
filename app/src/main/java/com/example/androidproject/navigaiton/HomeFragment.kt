package com.example.androidproject.navigaiton

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidproject.R
import com.example.androidproject.databinding.HomeDetailBinding
import com.example.androidproject.model.Content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_home, container, false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        var recyclerView = view.findViewById<RecyclerView>(R.id.homefragment_recyclerview)
        recyclerView.adapter = HomeViewRecyclerViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(activity)

        return view
    }
    inner class HomeViewRecyclerViewAdapter : RecyclerView.Adapter<HomeViewRecyclerViewAdapter.CustomViewHolder>() {
        inner class CustomViewHolder(val itemBinding: HomeDetailBinding) :
            RecyclerView.ViewHolder(itemBinding.root)

        var contents: ArrayList<Content> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("images")?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contents.clear()
                    contentUidList.clear()
                    if (querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot.documents) {
                        var item = snapshot.toObject(Content::class.java)
                        contents.add(item!!)
                        contentUidList.add(snapshot.id)
                    }
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {
            var view = HomeDetailBinding.inflate(LayoutInflater.from(p0.context), p0, false)
            return CustomViewHolder(view)
        }

        override fun getItemCount(): Int {
            return contents.size
        }

        override fun onBindViewHolder(p0: CustomViewHolder, p1: Int) {
            val currentItem = contents[p1]


            p0.itemBinding.userNameTv.text = currentItem.userId
            Glide.with(p0.itemView.context).load(currentItem.imageUrl).into(p0.itemBinding.postIv)
            p0.itemBinding.smallUserNameTv.text = currentItem.userId
            p0.itemBinding.contentDescriptionTv.text = currentItem.explain
            val likesCount = currentItem.favoriteCount
            val likesText = getString(R.string.like, likesCount)
            p0.itemBinding.likesTv.text = likesText
            val time = formatTimeAgo(currentItem.timestamp!!)
            p0.itemBinding.timepassedTv.text = time

            p0.itemBinding.likeIb.setOnClickListener {
                favoriteEvent(p1)
            }

            if (currentItem.favorites.containsKey(uid)) {
                p0.itemBinding.likeIb.setImageResource(R.drawable.heart_rounded)
                val likedColor = ContextCompat.getColor(p0.itemView.context, R.color.red)
                p0.itemBinding.likeIb.setColorFilter(likedColor)
            } else {
                p0.itemBinding.likeIb.setImageResource(R.drawable.heart_outlined)
                val notLikedColor = ContextCompat.getColor(p0.itemView.context, R.color.white)
                p0.itemBinding.likeIb.setColorFilter(notLikedColor)
            }

        }

        private fun formatTimeAgo(timestamp: Long): String {
            val now = System.currentTimeMillis()
            return DateUtils.getRelativeTimeSpanString(timestamp, now, DateUtils.MINUTE_IN_MILLIS)
                .toString()
        }

        private fun favoriteEvent(position: Int) {
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction { transaction ->


                var content = transaction.get(tsDoc!!).toObject(Content::class.java)

                if (content!!.favorites.containsKey(uid)) {
                    //When the button is clicked
                    content.favoriteCount = content.favoriteCount - 1
                    content.favorites.remove(uid)
                } else {
                    //When the button is not clicked
                    content.favoriteCount = content.favoriteCount + 1
                    content.favorites[uid!!] = true

                }
                transaction.set(tsDoc, content)
            }
        }
    }
}