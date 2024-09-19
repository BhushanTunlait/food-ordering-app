package com.android.adminsavoryfood

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.adminsavoryfood.databinding.ActivityAddItemBinding
import com.android.adminsavoryfood.model.Allmenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class AddItemActivity : AppCompatActivity() {
    private lateinit var foodname: String
    private lateinit var foodprice: String
    private lateinit var fooddescription: String
    private lateinit var foodingredient: String
    private var foodimgs: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private val binding: ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //initialize firebase auth
        auth = FirebaseAuth.getInstance()
        // initialize firebase database
        database = FirebaseDatabase.getInstance()

        binding.additembtn.setOnClickListener {
            // get data from textfield
            foodname = binding.Foodname.text.toString().trim()
            foodprice = binding.Foodprice.text.toString().trim()
            fooddescription = binding.Description.text.toString().trim()
            foodingredient = binding.ingradients.text.toString().trim()

            if (!(foodname.isBlank() || foodprice.isBlank() || fooddescription.isBlank() || foodingredient.isBlank())) {
                uploadData()
                if (foodimgs == null){
                    return@setOnClickListener
                }else{
                    Toast.makeText(this, "Item Added Successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }

            } else {
                Toast.makeText(this, "Failed To Add, Fill All The Details", Toast.LENGTH_SHORT).show()
            }
        }

        binding.selectfoodimage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.backbtn.setOnClickListener {
            finish()
        }
    }

    private fun uploadData() {
        // get a reference to the "menu" node in the database
        val MenuRef: DatabaseReference = database.getReference("menu")
        // Generate a unique key for the new menu item
        val newItemkey: String? = MenuRef.push().key

        if (foodimgs != null) {
            val storageRef: StorageReference = FirebaseStorage.getInstance().reference
            val imageRef: StorageReference = storageRef.child("menu_images/${newItemkey}.jpg")
            val uploadTask: UploadTask = imageRef.putFile(foodimgs!!)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // create a new menu item
                    val newItem = Allmenu(
                        newItemkey,
                        foodnames = foodname,
                        foodprice = foodprice,
                        foodDescriptions = fooddescription,
                        foodIngredients = foodingredient,
                        foodimgs = downloadUrl.toString()
                    )

                    newItemkey?.let { key ->
                        MenuRef.child(key).setValue(newItem).addOnSuccessListener {
                            // Log success
                            Log.d("UploadData", "Data Uploaded Successfully")
                            Toast.makeText(this, "Data Uploaded Successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }.addOnFailureListener {
                            // Log failure
                            Log.e("UploadData", "Failed To Upload Data", it)
                            Toast.makeText(this, "Failed To Upload Data", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.addOnFailureListener {
                // Log failure in uploading image
                Log.e("UploadData", "Image Upload Failed", it)
                Toast.makeText(this, "Image Upload Failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Log if no image is selected
            Log.d("UploadData", "Please Select An Image")
            Toast.makeText(this, "Please Select An Image", Toast.LENGTH_SHORT).show()
        }
    }


    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            binding.foodimage.setImageURI(uri)
            foodimgs = uri
        }
    }
}
