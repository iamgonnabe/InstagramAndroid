package com.example.androidproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.androidproject.databinding.ActivitySingUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SingUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySingUpBinding
    private var auth : FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySingUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.signUnBtn.setOnClickListener {
            createAccount(binding.idEt.text.toString(), binding.passwordEt.text.toString())
        }
    }
    private fun createAccount(email: String, password: String){
        if(email.isNotEmpty() && password.isNotEmpty()){
            auth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener(this){
                task->
                if(task.isSuccessful){
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}