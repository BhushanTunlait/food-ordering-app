package com.android.adminsavoryfood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.adminsavoryfood.adapter.OrderDetailsAdapter
import com.android.adminsavoryfood.databinding.ActivityOrderDetailsBinding
import com.android.adminsavoryfood.model.OrderDetails

class OrderDetailsActivity : AppCompatActivity() {
    private val binding : ActivityOrderDetailsBinding by lazy {
        ActivityOrderDetailsBinding.inflate(layoutInflater)
    }
    private var username : String? = null
    private var userAddress : String? = null
    private var userphonenumber : String? = null
    private var totalfoodprice : String? = null
    private var foodName : ArrayList<String> = arrayListOf()
    private var foodimages : ArrayList<String> = arrayListOf()
    private var foodQuantity : ArrayList<Int> = arrayListOf()
    private var foodPrice: ArrayList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backbtn.setOnClickListener {
            finish()
        }

        getdatafromintent()

    }

    private fun getdatafromintent() {
        val recivedOrderDetails = intent.getSerializableExtra("UserOrderDetails") as OrderDetails
        recivedOrderDetails?.let { orderDetails ->
                username = recivedOrderDetails.userName
                foodName = recivedOrderDetails.foodNames as ArrayList<String>
                foodimages = recivedOrderDetails.foodImages as ArrayList<String>
                foodQuantity = recivedOrderDetails.foodQuantities as ArrayList<Int>
                userAddress = recivedOrderDetails.address
                userphonenumber = recivedOrderDetails.phonenumber
                foodPrice = recivedOrderDetails.foodPrices as ArrayList<String>
                totalfoodprice = recivedOrderDetails.totalprice

                setUserDetail()
                setAdapter()

        }

    }

    private fun setAdapter() {
        binding.orderdetailsrecycler.layoutManager = LinearLayoutManager(this)
        val adapter = OrderDetailsAdapter(this, foodName, foodimages, foodQuantity, foodPrice)
        binding.orderdetailsrecycler.adapter = adapter
    }

    private fun setUserDetail() {
        binding.name.text = username
        binding.address.text = userAddress
        binding.phone.text = userphonenumber
        binding.TotalPrice.text = totalfoodprice

    }

}