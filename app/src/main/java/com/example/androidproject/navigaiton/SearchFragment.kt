package com.example.androidproject.navigaiton

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.androidproject.R
import com.example.androidproject.databinding.HomeDetailBinding
import com.example.androidproject.databinding.SearchDetailBinding
import com.example.androidproject.model.Content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SearchFragment : Fragment() {
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_search, container, false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        var recyclerView = view.findViewById<RecyclerView>(R.id.searchfragment_recyclerview)
        recyclerView.adapter = SearchViewRecyclerViewAdapter()
        recyclerView.layoutManager = GridLayoutManager(activity, 3)
        return view
    }
    inner class SearchViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        inner class CustomViewHolder(val imageview : ImageView) :
            RecyclerView.ViewHolder(imageview)

        var contents: ArrayList<Content> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("images")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                //Sometimes, This code return null of querySnapshot when it signout
                if(querySnapshot == null) return@addSnapshotListener

                //Get data
                for(snapshot in querySnapshot.documents){
                    contents.add(snapshot.toObject(Content::class.java)!!)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3
            var imageview = ImageView(p0.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return CustomViewHolder(imageview)
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var imageview = (p0 as CustomViewHolder).imageview
            Glide.with(p0.itemView.context).load(contents[p1].imageUrl).apply(RequestOptions().centerCrop()).into(imageview)
        }

        override fun getItemCount(): Int {
            return contents.size
        }

    }
}