package com.example.androidproject

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.androidproject.databinding.ActivityUploadBinding
import com.example.androidproject.model.Content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*

class UploadActivity : AppCompatActivity() {
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    private var storageRef = Firebase.storage.reference
    private lateinit var binding: ActivityUploadBinding
    private val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        results.entries.forEach{
            val permissionName = it.key
            val isGranted = it.value
            if(isGranted){
                Toast.makeText(this, "confirmed", Toast.LENGTH_SHORT).show()
                var photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                openGalleryLauncher.launch(photoPickerIntent)
            } else{
                Toast.makeText(this, "denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private val openGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == RESULT_OK && result.data!=null){
            val img = binding.addphotoImage
            img.setImageURI(result.data?.data)
            photoUri = result.data?.data
            binding.uploadBtn.isEnabled = true
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUploadBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.uploadTb)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "새 게시물"
        binding.uploadTb.setNavigationOnClickListener {
            finish()
        }
        binding.uploadBtn.isEnabled = false
        binding.selectBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                        this,
                        READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                        this,
                        READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                var photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                openGalleryLauncher.launch(photoPickerIntent)
            }
            else{
                requsetStorage()
            }
        }
        binding.uploadBtn.setOnClickListener {
            contentUpload(photoUri)
        }
    }
    private fun requsetStorage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        } else {
            requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun contentUpload(imageUri : Uri?){
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"
        val imagesRef = storageRef.child("images").child(imageFileName)
        val uploadTask = imagesRef.putFile(imageUri!!)
        uploadTask.addOnSuccessListener {
            var contentDTO = Content()

            //Insert downloadUrl of image
            contentDTO.imageUrl = imageUri.toString()

            //Insert uid of user
            contentDTO.uid = auth?.currentUser?.uid

            //Insert userId
            contentDTO.userId = auth?.currentUser?.email

            //Insert explain of content
            contentDTO.explain = binding.descriptionEt.text.toString()

            //Insert timestamp
            contentDTO.timestamp = System.currentTimeMillis()

            firestore?.collection("images")?.document()?.set(contentDTO)
            setResult(Activity.RESULT_OK)
            finish()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

}