package com.android.adminsavoryfood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.adminsavoryfood.adapter.DeliveryAdapter
import com.android.adminsavoryfood.adapter.pendingorderadapter
import com.android.adminsavoryfood.databinding.ActivityPendingOrderBinding
import com.android.adminsavoryfood.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingOrderActivity : AppCompatActivity() , pendingorderadapter.OnItemClicked{
    private lateinit var binding: ActivityPendingOrderBinding
    private var listofName : MutableList<String> = mutableListOf()
    private var listOfTotalPrice : MutableList<String> = mutableListOf()
    private var listOfImageFirstFoodOrder : MutableList<String> = mutableListOf()
    private var listOfOrderItem : ArrayList<OrderDetails> = arrayListOf()
    private lateinit var database:FirebaseDatabase
    private lateinit var databaseOrderDetails: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        databaseOrderDetails = database.reference.child("OrderDetails")
        getOrderDetails()

        binding.backbtn.setOnClickListener {
            finish()
        }

    }

    private fun getOrderDetails() {
        //retrive order details from firebase database
        databaseOrderDetails.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children){
                    val orderDetails = orderSnapshot.getValue(OrderDetails::class.java)
                    orderDetails?.let{
                        listOfOrderItem.add(it)
                    }
                }
                addDataToListForRecycleView()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addDataToListForRecycleView() {
        for (orderItem in listOfOrderItem){
            orderItem.userName?.let { listofName.add(it) }
            orderItem.totalprice?.let { listOfTotalPrice.add(it) }
            orderItem.foodImages?.filterNot { it.isEmpty() }?.forEach{
                listOfImageFirstFoodOrder.add(it)
            }
        }
        setAdapter()
    }

    private fun setAdapter() {
        binding.pendingorderryc.layoutManager = LinearLayoutManager(this)
        val adapter = pendingorderadapter(this, listofName,listOfTotalPrice,listOfImageFirstFoodOrder,this)
        binding.pendingorderryc.adapter= adapter
    }

    override  fun onItemClickListener (position: Int){
        val intent = Intent(this, OrderDetailsActivity::class.java)
        val userOrderDetails = listOfOrderItem[position]
        intent.putExtra("UserOrderDetails",userOrderDetails)
        startActivity(intent)
    }

    override fun onItemacceptClickListener(position: Int) {
        val childitempushkey = listOfOrderItem[position].itempushkey
        val clickItemOrderReference = childitempushkey?.let {
            database.reference.child("OrderDetails").child(it)

        }
        clickItemOrderReference?.child("orderaccepted")?.setValue(true)
        updateOrderAcceptStatus(position)
    }



    override fun onItemdispatchClickListener(position: Int) {
        val dispatchitempushkey = listOfOrderItem[position].itempushkey
        val dispatchitemorderreference = database.reference.child("CompletedOrder").child(dispatchitempushkey!!)
        dispatchitemorderreference.setValue(listOfOrderItem[position])
            .addOnSuccessListener {
                deletethisitemformorderdetails(dispatchitempushkey)
            }

    }

    private fun deletethisitemformorderdetails(dispatchitempushkey: String) {
        val orderdetailsitemreference = database.reference.child("OrderDetails").child(dispatchitempushkey)
        orderdetailsitemreference.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this,"Order is Dispatched",Toast.LENGTH_SHORT).show()

            }.addOnFailureListener{
                Toast.makeText(this,"Order is not Dispatched",Toast.LENGTH_SHORT).show()
            }
     }

    private fun updateOrderAcceptStatus(position: Int) {
    val userIdOfClickedItem = listOfOrderItem[position].userUid
        val pushkeyofclickeditem = listOfOrderItem[position].itempushkey
        val buyhistoryReference = database.reference.child("user").child(userIdOfClickedItem!!).child("BuyHistory").child(pushkeyofclickeditem!!)
        buyhistoryReference.child("orderaccepted").setValue(true)
        databaseOrderDetails.child(pushkeyofclickeditem).child("orderaccepted").setValue(true)
    }
}



