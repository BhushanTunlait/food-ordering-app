package com.android.adminsavoryfood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.adminsavoryfood.databinding.ActivityCreateUserBinding
import com.android.adminsavoryfood.model.usermodell
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class CreateUserActivity : AppCompatActivity() {
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private val binding: ActivityCreateUserBinding by lazy{
        ActivityCreateUserBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.backbtn.setOnClickListener {
            finish()
        }
        binding.CreatAccountbtn.setOnClickListener {
            username = binding.editTextName.text.toString().trim()
            email = binding.editTxtEmailAddress.text.toString().trim()
            password = binding.editTxtPassword.text.toString().trim()
            if (email.isBlank() || password.isBlank() || username.isBlank()) {
                Toast.makeText(this, "Please Fill All The Details", Toast.LENGTH_SHORT).show()
            } else {
                creatAccount(email, password)

            }
        }
    }
    private fun creatAccount(email: String, password: String) {
        if (!isEmailValid(email)) {
            Toast.makeText(this, "Invalid Email Format", Toast.LENGTH_SHORT).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                saveUserData()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed To Creat Account ", Toast.LENGTH_SHORT).show()
                Log.d("Account", "creatAccount: Failuer", task.exception)
            }
        }

    }
    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    private fun saveUserData() {
        username = binding.editTextName.text.toString()
        password = binding.editTxtPassword.text.toString().trim()
        email = binding.editTxtEmailAddress.text.toString().trim()

        val user = usermodell(username, email, password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        //save data in firebase
        database.child("owner-user").child(userId).setValue(user)
    }
}
