package com.example.androidproject.navigaiton

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidproject.R
import com.example.androidproject.databinding.SearchDetailBinding
import com.example.androidproject.model.Content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
        recyclerView.addItemDecoration(GridSpacingItemDecoration(3, 0))
        return view
    }
    inner class GridSpacingItemDecoration(private val spanCount: Int, private val spacing : Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val collumn = position % spanCount + 1
            outRect.left = spacing
            outRect.right = spacing
            outRect.top = spacing
            outRect.bottom = spacing
        }
    }
    inner class SearchViewRecyclerViewAdapter : RecyclerView.Adapter<SearchViewRecyclerViewAdapter.CustomViewHolder>() {
        inner class CustomViewHolder(val itemBinding: SearchDetailBinding) :
            RecyclerView.ViewHolder(itemBinding.root)

        var contents: ArrayList<Content> = arrayListOf()

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

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {
            var view = SearchDetailBinding.inflate(LayoutInflater.from(p0.context), p0, false)
            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(p0: CustomViewHolder, p1: Int) {
            var width = resources.displayMetrics.widthPixels / 3
            Glide.with(p0.itemView.context).load(contents[p1].imageUrl).override(width, width).into(p0.itemBinding.gridIv)
            p0.itemBinding.gridIv.setOnClickListener {
                var detailFragment = DetailFragment()
                var bundle = Bundle()
                var data = contents[p1]
                bundle.putParcelable("data", data)
                detailFragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_container, detailFragment)?.commit()
            }
        }
        override fun getItemCount(): Int {
            return contents.size
        }

    }
}