package com.example.panicalarm.fragments

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.panicalarm.R
import com.example.panicalarm.adapter.ContactAdapter
import com.example.panicalarm.adapter.OnClick
import com.example.panicalarm.database.Contact
import com.example.panicalarm.database.ContactDatabase
import com.example.panicalarm.database.DatabaseObject
import com.example.panicalarm.databinding.FragmentContactDetailsBinding
import com.example.panicalarm.model.ContactModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ContactDetailsFragment : Fragment(), OnClick {

    lateinit var contactList: List<ContactModel>
    lateinit var updadeList: List<Contact>
    lateinit var binding: FragmentContactDetailsBinding
    lateinit var deleteList: List<Contact>
    lateinit var contactListToShow: List<Contact>
    lateinit var adapter: ContactAdapter
    lateinit var database: ContactDatabase
    var contactLimit = true;
    var count = 0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentContactDetailsBinding.inflate(layoutInflater)

        contactList = ArrayList()
        updadeList = ArrayList()


        database = DatabaseObject.getInstance(requireContext())
        database.contactDao().getContact().observe(viewLifecycleOwner, Observer {
            contactListToShow = it
            adapter = ContactAdapter(contactListToShow, this)
            binding.recyclerView.adapter = adapter
            deleteList = ArrayList()
        })
        //Log.d("contactList", "$contactListToShow")
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this@ContactDetailsFragment.requireContext())


        binding.btnAddContact.setOnClickListener()
        {
            if (count > 5) {
                contactLimit = false;
            }
            if (contactLimit && validation()) {
                val name = binding.etName.text.toString()
                val number = binding.etNumber.text.toString()
                binding.etName.setText("")
                binding.etNumber.setText("")
                GlobalScope.launch {
                    database.contactDao().insertContact(Contact(0, name, number))

                }
                /* contactList.add(ContactModel(name, number))
                 adapter = ContactAdapter(contactList)
                 binding.recyclerView.adapter = adapter*/
                count++
            } else {
                Toast.makeText(
                    this@ContactDetailsFragment.requireContext(),
                    "Can only add 5 contacts",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        return binding.root
    }

    private fun validation(): Boolean {

        if (binding.etName.text.toString().isEmpty()) {
            binding.etName.setError("Empty")
            return false
        }
        if (binding.etNumber.text.toString().isEmpty()) {
            binding.etNumber.setError("Empty")
            return false
        }

        if (binding.etNumber.text.toString().length < 10) {
            Toast.makeText(
                this@ContactDetailsFragment.requireContext(),
                "Invalid number",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (binding.etNumber.text.toString().length > 10) {
            Toast.makeText(
                this@ContactDetailsFragment.requireContext(),
                "Invalid number",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (!Patterns.PHONE.matcher(binding.etNumber.text.toString()).matches()) {
            return false;
        }
        return true

    }

    override fun deleteContact(name: String, number: String) {

        val builder = AlertDialog.Builder(this@ContactDetailsFragment.requireContext())
        builder.setTitle("Confirm Delete")
        builder.setCancelable(false)
        builder.setMessage("Are you sure you want to delete this item?")
        builder.setPositiveButton("Delete") { dialog, which ->
            // Delete logic goes here
            database.contactDao().getContact().observe(viewLifecycleOwner, Observer {
                deleteList = it
            })
            GlobalScope.launch {
                for (item in deleteList) {
                    if (item.name == name) {
                        Log.d("name", "${item.name}")
                        database.contactDao().deleteContact(Contact(item.id, name, number))
                    }
                }


            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            // Cancel logic goes here
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun updateContact(name: String, phone: String) {

        val dailog = AlertDialog.Builder(this@ContactDetailsFragment.requireContext())
        val dailogView = LayoutInflater.from(context).inflate(R.layout.custom_dailog_contact_update, null)
        val view = dailog.setView(dailogView).create()
        dailog.setCancelable(false)
        val updateName = dailogView.findViewById<EditText>(R.id.et_name)
        val updateNumber = dailogView.findViewById<EditText>(R.id.et_number)
        val updateButton = dailogView.findViewById<Button>(R.id.btn_update)
        val cancelButton = dailogView.findViewById<Button>(R.id.btn_cancel)
        updateName?.setText(name.toString())
        updateNumber?.setText(phone.toString())
        val show =  dailog.show()
        updateButton?.setOnClickListener()
        {
            val newName = updateName.text.toString()
            val newNumber = updateNumber.text.toString()
            database.contactDao().getContact().observe(viewLifecycleOwner, Observer {
                updadeList = it
            })
            Toast.makeText(context, "$updadeList", Toast.LENGTH_SHORT).show()

            GlobalScope.launch {
                for (item in updadeList) {
                    if (item.name == name) {

                        database.contactDao().updateContact(Contact(item.id, newName, newNumber))
                    }
                }
            }
            show.dismiss()
        }
        cancelButton.setOnClickListener()
        {
            show.dismiss()
        }

    }

}