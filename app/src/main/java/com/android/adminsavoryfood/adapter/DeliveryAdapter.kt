package com.android.adminsavoryfood.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.adminsavoryfood.databinding.DeliveryItemsBinding

class DeliveryAdapter( private val customername:MutableList<String>,private val moneystatus:MutableList<Boolean>):RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):
            DeliveryViewHolder {
      val binding = DeliveryItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DeliveryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customername.size

    inner class DeliveryViewHolder(private val binding: DeliveryItemsBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.apply {
                if (moneystatus[position] == true){
                    moneysstatus.text = "Received"
                }
                else{
                    moneysstatus.text = "NotReceived"
                }

                val colorMap = mapOf(
                    true to Color.GREEN,false to "notReceived" to Color.RED
                )
                moneysstatus.setTextColor(colorMap[moneystatus[position]]?:Color.BLACK)
                deliverystatus.backgroundTintList = ColorStateList.valueOf(colorMap[moneystatus[position]]?:Color.BLACK)

            }
        }

    }

}