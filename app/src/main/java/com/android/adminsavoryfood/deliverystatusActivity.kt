package com.android.adminsavoryfood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.adminsavoryfood.adapter.DeliveryAdapter
import com.android.adminsavoryfood.databinding.ActivityDeliverystatusBinding
import com.android.adminsavoryfood.databinding.DeliveryItemsBinding
import com.android.adminsavoryfood.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class deliverystatusActivity : AppCompatActivity() {
    private val binding : ActivityDeliverystatusBinding by lazy {
        ActivityDeliverystatusBinding.inflate(layoutInflater)
    }
    private lateinit var  database: FirebaseDatabase
    private var listOfCompleteOrderList: ArrayList<OrderDetails> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backbtn.setOnClickListener {
            finish()
        }
        retrieveCompleteOrderDetails()

    }

    private fun retrieveCompleteOrderDetails() {
        database = FirebaseDatabase.getInstance()
        val completeorderreference = database.reference.child("CompletedOrder")
            .orderByChild("currenttime")
        completeorderreference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfCompleteOrderList.clear()
                for (orderSnapshot in snapshot.children){
                    val completeOrder = orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.let {
                        listOfCompleteOrderList.add(it)
                    }
                }
                listOfCompleteOrderList.reverse()
                setdataintorecyclerview()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setdataintorecyclerview() {

        val customerName = mutableListOf<String>()
        val moneystatus = mutableListOf<Boolean>()
        for (order in listOfCompleteOrderList){
            order.userName?.let {
                customerName.add(it)
            }
            moneystatus.add(order.paymentReceived)
        }
        val adapter = DeliveryAdapter(customerName,moneystatus)
        binding.deliverystatusrv.adapter = adapter
        binding.deliverystatusrv.layoutManager = LinearLayoutManager(this)
    }
}