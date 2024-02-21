package com.example.androidproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import com.example.androidproject.databinding.ActivityMainBinding
import com.example.androidproject.navigaiton.AccountFragment
import com.example.androidproject.navigaiton.HomeFragment
import com.example.androidproject.navigaiton.SearchFragment
import com.example.androidproject.navigaiton.ShortsFragment
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, HomeFragment()).commit()
                return true
            }

            R.id.action_search -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, SearchFragment()).commit()
                return true
            }

            R.id.action_upload -> {
                uploadLauncher.launch(Intent(this, UploadActivity::class.java))
                return true
            }

            R.id.action_video -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, ShortsFragment()).commit()
                return true
            }

            R.id.action_account -> {
                var accountFragment = AccountFragment()
                var bundle = Bundle()
                var uid = FirebaseAuth.getInstance().currentUser?.uid

                bundle.putString("destinationUid", uid)
                accountFragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, accountFragment).commit()
                return true
            }
        }
        return false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.bottomNavigation.setOnItemSelectedListener(this)
        if(savedInstanceState == null){
            binding.bottomNavigation.selectedItemId = R.id.action_home
        }
        val toolbar = binding.mainTb
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = "Instagram"
    }

    private val uploadLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == RESULT_OK){
            binding.bottomNavigation.selectedItemId = R.id.action_home
        }
    }

}