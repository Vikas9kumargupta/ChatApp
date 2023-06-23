package com.example.vikaschatapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.vikaschatapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var btnSignUp : Button
    private lateinit var mDatabaseRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()


        mAuth = FirebaseAuth.getInstance()
        btnSignUp = binding.signUp

        btnSignUp.setOnClickListener {
            val name = binding.name.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            signUp(name,email,password)
        }

        binding.loginText.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun signUp(name: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task->
            if(task.isSuccessful) {
                addUserToDataBase(name,email,mAuth.currentUser?.uid!!)
                val intent = Intent(this, MainActivity::class.java)
                finish()
                startActivity(intent)
            } else {
                Toast.makeText(this,"Some error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addUserToDataBase(name: String, email: String, uid: String?) {
        mDatabaseRef = FirebaseDatabase.getInstance().reference
        mDatabaseRef.child("user").child(uid!!).setValue(User(name,email,uid))
    }
}


















