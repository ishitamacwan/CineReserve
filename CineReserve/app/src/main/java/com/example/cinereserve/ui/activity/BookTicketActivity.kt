package com.example.cinereserve.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinereserve.R
import com.example.cinereserve.data.network.model.BookedMovie
import com.example.cinereserve.databinding.ActivityBookTicketBinding
import com.example.cinereserve.ui.adapter.CartAdapter
import com.example.cinereserve.ui.viewmodels.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookTicketActivity : AppCompatActivity() {

    lateinit var binding: ActivityBookTicketBinding

    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBookTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        setupRecyclerView()
        observeCart()
        cartViewModel.loadCart()
    }
    private fun setupRecyclerView() {

        adapter = CartAdapter(
            this,
            emptyList(),
            cartViewModel,
            onDelete = { item -> cartViewModel.deleteCartItem(item) },
            bookMovie = { item ->
                cartViewModel.bookMovie(item) {
                    Toast.makeText(this, "Tickets booked successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        )
        binding.rvCart.layoutManager = LinearLayoutManager(this)
        binding.rvCart.adapter = adapter
    }
    private fun observeCart() {
        lifecycleScope.launch {
            cartViewModel.cartItems.collectLatest { list ->
                adapter.updateList(list)
                if (list.isEmpty()) {
                    binding.rvCart.visibility = View.GONE
                    binding.noDataText.visibility = View.VISIBLE
                } else {
                    binding.rvCart.visibility = View.VISIBLE
                    binding.noDataText.visibility = View.GONE
                }
            }
        }
    }

}