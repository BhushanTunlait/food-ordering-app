package com.android.adminsavoryfood

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.adminsavoryfood.databinding.ActivitySignUpBinding
import com.android.adminsavoryfood.model.usermodell
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private var isPasswordVisible = false

    private lateinit var username: String
    private lateinit var restaurantName: String
    private lateinit var contactInfo: String
    private lateinit var password: String
    private lateinit var location: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth
        // Initialize Firebase Database
        database = Firebase.database.reference

        val locationList = arrayOf("Buldhana", "Pune", "Mumbai", "Nagpur", "Chikhli", "Akola")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList)
        val autoCompleteTextView = binding.listoflocation
        autoCompleteTextView.setAdapter(adapter)

               binding.newacntbtn.setOnClickListener {
            // Get text from EditText
            username = binding.OwnerName.text.toString().trim()
            restaurantName = binding.NameOfRestaurant.text.toString().trim()
            contactInfo = binding.Contactinfo.text.toString().trim()
            password = binding.NewPassword.text.toString().trim()
            location = binding.listoflocation.text.toString().trim()

            if (username.isBlank() || restaurantName.isBlank() || contactInfo.isBlank() || password.isBlank() || location.isBlank()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            } else {
                // Check if contactInfo is an email
                if (Patterns.EMAIL_ADDRESS.matcher(contactInfo).matches()) {
                    createAccountWithEmail(contactInfo, password)
                } else {
                    Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.alreadyhaveacnt.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val passwordEditText = binding.NewPassword
        val showHidePasswordBtn = binding.showHidePasswordBtn

        showHidePasswordBtn.setOnClickListener {
            // Toggle the password visibility
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showHidePasswordBtn.setImageResource(R.drawable.eye_hide)
            } else {
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showHidePasswordBtn.setImageResource(R.drawable.eye)
            }

            // Move the cursor to the end of the text
            passwordEditText.setSelection(passwordEditText.text.length)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }


    private fun createAccountWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                saveUserData(email = email)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val exception = task.exception
                when {
                    exception is FirebaseAuthWeakPasswordException -> {
                        Toast.makeText(this, "Password is too weak.", Toast.LENGTH_SHORT).show()
                    }
                    exception is FirebaseAuthInvalidCredentialsException -> {
                        Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show()
                    }
                    exception is FirebaseAuthUserCollisionException -> {
                        Toast.makeText(this, "This email is already in use.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(this, "Account Creation Failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                Log.e(TAG, "createAccountWithEmail: Failure", exception)
            }
        }

    }

    private fun saveUserData(email: String? = null, phone: String? = null, displayName: String? = null) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.e(TAG, "User not logged in. Cannot save user data.")
            return
        }

        val userId = user.uid
        username = displayName ?: binding.OwnerName.text.toString().trim()
        restaurantName = binding.NameOfRestaurant.text.toString().trim()
        password = binding.NewPassword.text.toString().trim()
        location = binding.listoflocation.text.toString().trim()

        val userData = usermodell(username, restaurantName, email, password, phone, location)
        database.child("owner-user").child(userId).setValue(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "User data saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to save user data: ${exception.message}")
                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
            }
    }


    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Handle the case where the user is null (optional)
        }
    }

    companion object {
        private const val TAG = "SignUpActivity"
    }
}
