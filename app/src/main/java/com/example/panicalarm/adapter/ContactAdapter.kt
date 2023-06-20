package com.example.panicalarm.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.panicalarm.R
import com.example.panicalarm.database.Contact

class ContactAdapter(val contactList: List<Contact>, private val listner:OnClick):
    RecyclerView.Adapter<ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.saved_contact_item_resource, parent, false)
        return ContactViewHolder(view)
    }


    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentData = contactList[position]
        holder.name.text = currentData.name
        holder.number.text = currentData.phone.toString()
        holder.deleteContact.setOnClickListener()
        {
            listner.deleteContact(currentData.name,currentData.phone)
        }
        holder.updateContact.setOnClickListener()
        {
            listner.updateContact(currentData.name,currentData.phone)
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }
}

class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name = itemView.findViewById<TextView>(R.id.tv_contact_name)
    val number = itemView.findViewById<TextView>(R.id.tv_contact_number)
    val deleteContact = itemView.findViewById<ImageView>(R.id.img_delete)
    val updateContact = itemView.findViewById<ImageView>(R.id.img_update)
}
interface OnClick
{
    fun deleteContact(name: String, number: String)
    fun updateContact(name: String, phone: String)
}