package com.android.adminsavoryfood

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.adminsavoryfood.adapter.MenuItemAdapter
import com.android.adminsavoryfood.databinding.ActivityAllItemsBinding
import com.android.adminsavoryfood.model.Allmenu
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllItemsActivity : AppCompatActivity() {

    private var menuItems: ArrayList<Allmenu> = ArrayList()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private val binding: ActivityAllItemsBinding by lazy {
        ActivityAllItemsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        databaseReference = FirebaseDatabase.getInstance().reference
        retrivemenuitem()

        binding.backbtn.setOnClickListener {
            finish()
        }


    }

    private fun retrivemenuitem() {
        database = FirebaseDatabase.getInstance()
        val foodRef:DatabaseReference = database.reference.child("menu")

        //fetch data from data base
        foodRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                menuItems.clear()

                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(Allmenu::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError","Error: ${error.message}")
            }


        })


    }
    private fun setAdapter(){
        val adapter =
            MenuItemAdapter(this@AllItemsActivity, menuItems,databaseReference){position ->
                deleteMenuitems(position)
            }
        binding.itemsmenu.layoutManager = LinearLayoutManager(this)
        binding.itemsmenu.adapter = adapter

    }

    private fun deleteMenuitems(position: Int) {
        val menuItemToDelete = menuItems[position]
        val menuitemkey = menuItemToDelete.key
        val foodmanureference = database.reference.child("menu").child(menuitemkey!!)
        foodmanureference.removeValue().addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                menuItems.removeAt(position)
                binding.itemsmenu.adapter?.notifyItemRemoved(position)
            }
            else {
                Toast.makeText(this, "Item not Deleted",Toast.LENGTH_SHORT).show()
            }
        }
    }
}