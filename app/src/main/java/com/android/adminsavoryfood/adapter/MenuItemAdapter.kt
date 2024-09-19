package com.android.adminsavoryfood.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.adminsavoryfood.databinding.ItemItemBinding
import com.android.adminsavoryfood.model.Allmenu
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference

class MenuItemAdapter(
    private val context: Context,
    private val MenuList: ArrayList<Allmenu>,
    databaseReference: DatabaseReference,
    private val onDeleteClickListener:(position : Int) ->Unit
    ) : RecyclerView.Adapter<MenuItemAdapter.AddItemViewHolder>() {

    private val itemqty = IntArray(MenuList.size) { 1 }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuItemAdapter.AddItemViewHolder {
        val binding = ItemItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddItemViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MenuItemAdapter.AddItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = MenuList.size
    inner class AddItemViewHolder(private val binding: ItemItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val menuItem = MenuList[position]
                val quantity = itemqty[position]
                val uriString = menuItem.foodimgs
                val uri = Uri.parse(uriString)
                hisfoodname.text = menuItem.foodnames
                hisfoodprice.text = menuItem.foodprice
                Glide.with(context).load(uri).into(hisimg)
                foodqty.text = quantity.toString()
                minusbtn.setOnClickListener {
                    decreaseqty(position)
                }
                plusbtn.setOnClickListener {
                    increaseqty(position)
                }
                trashbtn.setOnClickListener {
                    onDeleteClickListener(position)
                }
            }
        }

        private fun deleteitem(position: Int) {
            MenuList.removeAt(position)
            MenuList.removeAt(position)
            MenuList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, MenuList.size)

        }

        private fun increaseqty(position: Int) {
            if (itemqty[position] < 10) {
                itemqty[position]++
                binding.foodqty.text = itemqty[position].toString()
            }
        }

        private fun decreaseqty(position: Int) {
            if (itemqty[position] > 1) {
                itemqty[position]--
                binding.foodqty.text = itemqty[position].toString()
            }
        }

    }
}
