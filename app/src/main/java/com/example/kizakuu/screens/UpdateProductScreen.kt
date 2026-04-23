package com.example.kizakuu.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.kizakuu.data.ProductViewModel
import com.example.kizakuu.utils.CloudinaryHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import model.Product
import navigation.ROUTE_VIEW_PRODUCTS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProductScreen(navController: NavHostController, productId: String) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var currentImageUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val productViewModel = remember { ProductViewModel(navController, context) }

    // Fetch current product details
    LaunchedEffect(productId) {
        val ref = FirebaseDatabase.getInstance().getReference("Products/$productId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val product = snapshot.getValue(Product::class.java)
                if (product != null) {
                    name = product.name
                    price = product.price
                    description = product.description
                    currentImageUrl = product.imageUrl
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Update Product", color = Color.Red, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Black)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image Preview
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                val imagePainter = if (selectedImageUri != null) {
                    rememberAsyncImagePainter(selectedImageUri)
                } else {
                    rememberAsyncImagePainter(currentImageUrl)
                }
                Image(
                    painter = imagePainter,
                    contentDescription = "Product Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Button(
                onClick = { launcher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Change Image", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Product Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Product Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isUploading) {
                CircularProgressIndicator(color = Color.Red)
            } else {
                Button(
                    onClick = {
                        if (name.isBlank() || price.isBlank() || description.isBlank()) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isUploading = true
                        if (selectedImageUri != null) {
                            // Upload new image first
                            CloudinaryHelper.uploadImage(
                                selectedImageUri!!,
                                onSuccess = { newUrl ->
                                    updateFirebase(productId, name, price, description, newUrl, productViewModel)
                                },
                                onError = { error ->
                                    isUploading = false
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            // Update with existing image URL
                            updateFirebase(productId, name, price, description, currentImageUrl, productViewModel)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Update Product", color = Color.White)
                }
            }
        }
    }
}

private fun updateFirebase(
    id: String,
    name: String,
    price: String,
    description: String,
    imageUrl: String,
    viewModel: ProductViewModel
) {
    val ref = FirebaseDatabase.getInstance().getReference("Products/$id")
    val product = Product(name, price, description, imageUrl, id)
    ref.setValue(product).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Toast.makeText(viewModel.context, "Updated successfully", Toast.LENGTH_SHORT).show()
            viewModel.navController.navigate(ROUTE_VIEW_PRODUCTS)
        } else {
            Toast.makeText(viewModel.context, task.exception?.message, Toast.LENGTH_SHORT).show()
        }
    }
}
