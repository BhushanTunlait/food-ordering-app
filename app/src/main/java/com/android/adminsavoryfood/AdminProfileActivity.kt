package com.android.adminsavoryfood

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.adminsavoryfood.databinding.ActivityAdminProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminProfileActivity : AppCompatActivity() {
    private val binding: ActivityAdminProfileBinding by lazy {
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }

    private var isPasswordVisible = false

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var adminReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        adminReference = database.reference.child("owner-user")

        binding.backbtn.setOnClickListener {
            finish()
        }
        binding.saveinfobtn.setOnClickListener {
            updateuserdata()
        }

        setFieldsEnabled(false)
        binding.saveinfobtn.isEnabled = false

        binding.Editbtn.setOnClickListener {
            toggleEditMode()
        }

        binding.showHidePasswordBtn.setOnClickListener {
            togglePasswordVisibility()
        }

        retriveuserdata()
    }


    private fun setFieldsEnabled(isEnabled: Boolean) {
        binding.name.isEnabled = isEnabled
        binding.Address.isEnabled = isEnabled
        binding.Email.isEnabled = isEnabled
        binding.Phone.isEnabled = isEnabled
        binding.Password.isEnabled = isEnabled
    }

    private fun toggleEditMode() {
        val isEnabled = !binding.name.isEnabled
        setFieldsEnabled(isEnabled)
        if (isEnabled) {
            binding.name.requestFocus()
        }
        binding.saveinfobtn.isEnabled = isEnabled
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        if (isPasswordVisible) {
            binding.Password.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.showHidePasswordBtn.setImageResource(R.drawable.eye_hide)
        } else {
            binding.Password.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.showHidePasswordBtn.setImageResource(R.drawable.eye)
        }
        binding.Password.setSelection(binding.Password.text.length)
    }

    private fun retriveuserdata() {
        val currentuserid = auth.currentUser?.uid
        if (currentuserid != null) {
            val userReference = adminReference.child(currentuserid)
            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val ownername = snapshot.child("name").getValue<String>()
                        val email = snapshot.child("email").getValue<String>()
                        val password = snapshot.child("password").getValue<String>()
                        val address = snapshot.child("location").getValue<String>()
                        val phone = snapshot.child("phone").getValue<String>()
                        setDataToTextView(ownername, email, password, address, phone)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AdminProfileActivity, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setDataToTextView(
        ownername: String?,
        email: String?,
        password: String?,
        address: String?,
        phone: String?
    ) {
        binding.name.setText(ownername ?: "")
        binding.Email.setText(email ?: "")
        binding.Password.setText(password ?: "")
        binding.Phone.setText(phone ?: "")
        binding.Address.setText(address ?: "")
    }


    private fun updateuserdata() {
        val updatename = binding.name.text.toString()
        val updateemail = binding.Email.text.toString()
        val updatepassword = binding.Password.text.toString()
        val updatephone = binding.Phone.text.toString()
        val updateaddress = binding.Address.text.toString()
        val currentuserid = auth.currentUser?.uid
        if (currentuserid != null) {
            val userReference = adminReference.child(currentuserid)
            val updates = mapOf<String, Any>(
                "name" to updatename,
                "email" to updateemail,
                "password" to updatepassword,
                "phone" to updatephone,
                "address" to updateaddress
            )
            userReference.updateChildren(updates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Profile Update Successful", Toast.LENGTH_SHORT).show()
                    auth.currentUser?.updateEmail(updateemail)
                    auth.currentUser?.updatePassword(updatepassword)
                } else {
                    Toast.makeText(this, "Profile Update Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
