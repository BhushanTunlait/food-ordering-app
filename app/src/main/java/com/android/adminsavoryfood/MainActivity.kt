package com.android.adminsavoryfood

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.adminsavoryfood.databinding.ActivityMainBinding
import com.android.adminsavoryfood.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var complitedorderreference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        binding.addmenu.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }
        binding.allitemsmenu.setOnClickListener {
            val intent = Intent(this, AllItemsActivity::class.java)
            startActivity(intent)
        }
        binding.PandingOrder.setOnClickListener {
            val intent = Intent(this, deliverystatusActivity::class.java)
            startActivity(intent)
        }
        binding.newuserbtn.setOnClickListener {
            val intent = Intent(this, CreateUserActivity::class.java)
            startActivity(intent)
        }
        binding.PandingOrdertxtbtn.setOnClickListener {
            val intent = Intent(this, PendingOrderActivity::class.java)
            startActivity(intent)
        }

        binding.logoutbtn.setOnClickListener {
            auth.signOut() // Sign out from Firebase Authentication

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close MainActivity
            Toast.makeText(this, "Account LogOut Successful", Toast.LENGTH_SHORT).show()
        }

        binding.adminprofile.setOnClickListener {
            val intent = Intent(this, AdminProfileActivity::class.java)
            startActivity(intent)
        }

        pendingOrders()
        completedOrders()
        wholetimeearning()
    }

    private fun wholetimeearning() {
        var listoftotalpay = mutableListOf<Int>()
        complitedorderreference = FirebaseDatabase.getInstance().reference.child("CompletedOrder")
        complitedorderreference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    var completeorder = orderSnapshot.getValue(OrderDetails::class.java)
                    completeorder?.totalprice?.replace("₹", "")?.toIntOrNull()
                        ?.let { i ->
                            listoftotalpay.add(i)
                        }
                }
                binding.wholetimesearning.text = listoftotalpay.sum().toString() + "₹"

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

    private fun completedOrders() {
        database = FirebaseDatabase.getInstance()
        var completedorderreference = database.reference.child("CompletedOrder")
        var completedorderitemcount = 0
        completedorderreference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                completedorderitemcount = snapshot.childrenCount.toInt()
                binding.Completedordercount.text = completedorderitemcount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

    private fun pendingOrders() {
        database = FirebaseDatabase.getInstance()
        var pendingorderreference = database.reference.child("OrderDetails")
        var pendingorderitemcount = 0
        pendingorderreference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pendingorderitemcount = snapshot.childrenCount.toInt()
                binding.pendingorderscount.text = pendingorderitemcount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }
}
