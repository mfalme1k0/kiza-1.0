package com.example.kizakuu.data

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.google.firebase.database.FirebaseDatabase
import model.Product
import navigation.ROUTE_DASHBOARD

class ProductViewModel(var navController: NavHostController, var context: Context) {

    fun addProduct(name: String, price: String, description: String) {
        if (name.isBlank() || price.isBlank() || description.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val id = System.currentTimeMillis().toString()
        val productData = Product(name, price, description, id)
        val productRef = FirebaseDatabase.getInstance().getReference().child("Products/$id")

        productRef.setValue(productData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Product added successfully", Toast.LENGTH_SHORT).show()
                navController.navigate(ROUTE_DASHBOARD)
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
