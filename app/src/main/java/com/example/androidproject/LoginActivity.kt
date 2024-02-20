package com.example.androidproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.androidproject.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var auth : FirebaseAuth?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        binding.loginBtn.setOnClickListener {
            login(binding.idEt.text.toString(), binding.passwordEt.text.toString())
        }
        binding.signInBtn.setOnClickListener {
            val intent = Intent(this, SingUpActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
    }
    private fun login(email: String, password: String){
        if(email.isNotEmpty() && password.isNotEmpty()){
            auth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(this){
                task ->
                if(task.isSuccessful){
                    moveMainPage(auth?.currentUser)
                }else{
                    Toast.makeText(
                        baseContext, "로그인 실패", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun moveMainPage(user: FirebaseUser?){
        if(user!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }

}