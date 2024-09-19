package com.android.adminsavoryfood.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.adminsavoryfood.databinding.PendingordersBinding
import com.bumptech.glide.Glide

class pendingorderadapter(
    private val context: Context,
    private val customerName: MutableList<String>,
    private val quantity: MutableList<String>,
    private val pofoodimg: MutableList<String>,
    private val itemClicked: OnItemClicked,
) : RecyclerView.Adapter<pendingorderadapter.pendingorderViewHolder>() {

    interface OnItemClicked{
        fun onItemClickListener(position: Int)
        fun onItemacceptClickListener(position: Int)
        fun onItemdispatchClickListener(position: Int)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): pendingorderViewHolder {
        val binding =
            PendingordersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return pendingorderViewHolder(binding)
    }


    override fun onBindViewHolder(holder: pendingorderViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerName.size

    inner class pendingorderViewHolder(private val binding: PendingordersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isAccepted = false
        fun bind(position: Int) {
            binding.apply {
                customername.text = customerName[position]
                pofoodqty.text = quantity[position]
                var usiString = pofoodimg[position]
                var uri = Uri.parse(usiString)
                Glide.with(context).load(uri).into(orderfoodimg)
                orderacceptbtn.apply {

                    if (!isAccepted) {
                        text = "Accept"
                    } else {
                        text = "Dispatch"
                    }
                    setOnClickListener {
                        if (!isAccepted) {
                            text = "Dispatch"
                            isAccepted = true
                            showToast("Order Is Accepted")
                            itemClicked.onItemacceptClickListener(position)

                        } else {
                            customerName.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            showToast("Order Is Dispatch")
                            itemClicked.onItemdispatchClickListener(position)


                        }
                    }
                }
                itemView.setOnClickListener{
                    itemClicked.onItemClickListener(position)
                }

            }


        }

        private  fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

}