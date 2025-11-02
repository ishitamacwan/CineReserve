package com.example.cinereserve.ui.adapter

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cinereserve.data.network.model.CartItem
import coil.load
import com.example.cinereserve.R
import com.example.cinereserve.data.network.model.BookedMovie
import com.example.cinereserve.databinding.DialogConfirmBookingBinding
import com.example.cinereserve.databinding.ItemSavedPlanBinding
import com.example.cinereserve.ui.activity.MainActivity
import com.example.cinereserve.ui.viewmodels.CartViewModel
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class CartAdapter(
    var context: Activity,
    private var items: List<CartItem>,
    private val viewModel: CartViewModel,
    private val onDelete: (CartItem) -> Unit,
    private val bookMovie: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    val movieSlots = listOf("10:00 AM", "1:00 PM", "4:00 PM", "7:00 PM", "10:00 PM")

    inner class CartViewHolder(val binding: ItemSavedPlanBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding =
            ItemSavedPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            tvMovieTitle.text = item.title
            tvGenre.text = item.genre
            tvDuration.text = item.duration
            tvRating.text = String.format("%.1f", item.rating)

            tvDate.text = item.date
            tvPersonCount.text = item.personCount.toString()
            imgPoster.load(item.posterUrl)
            val seatPrice = 100
            tvPrice.text = "₹${item.personCount * seatPrice}"


            btnPlus.setOnClickListener {
                if (item.personCount < 10) {
                    val count = item.personCount + 1
                    item.personCount = count
                    viewModel.updateCartItem(item)
                    tvPersonCount.text = count.toString()
                    tvPrice.text = "₹${count * seatPrice}"
                } else {
                    Toast.makeText(context, "Maximum 10 seats allowed", Toast.LENGTH_SHORT).show()
                }
            }



            btnMinus.setOnClickListener {
                if (item.personCount > 1) {
                    val count = item.personCount - 1
                    item.personCount = count
                    viewModel.updateCartItem(item)
                    tvPersonCount.text = count.toString()
                    tvPrice.text = "₹${count * seatPrice}"
                }
            }

            btnDelete.setOnClickListener {
                onDelete(item)
            }
            tvDateSlot.text = item.date
            tvDateSlot.setOnClickListener {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    context,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        val formattedDate = "${selectedDay.toString().padStart(2, '0')}/" +
                                "${(selectedMonth + 1).toString().padStart(2, '0')}/$selectedYear"
                        tvDateSlot.text = formattedDate
                        item.date = formattedDate
                        viewModel.updateCartItem(item)
                    }, year, month, day
                )
                datePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                datePickerDialog.datePicker.minDate = calendar.timeInMillis
                datePickerDialog.show()
            }

            holder.binding.chipGroupTime.removeAllViews()
            movieSlots.forEach { slot ->
                val chip = Chip(context)
                chip.text = slot
                chip.isCheckable = true
                chip.isChecked = item.selectedSlot == slot
                chip.setChipBackgroundColorResource(
                    if (chip.isChecked) R.color.colorPrimary else R.color.white
                )
                if (item.selectedSlot == slot) {
                    chip.setChipBackgroundColorResource(R.color.colorPrimary)
                    chip.setTextColor(context.resources.getColor(R.color.white))
                } else {
                    chip.setChipBackgroundColorResource(R.color.white)
                    chip.setTextColor(context.resources.getColor(R.color.black))
                }

                chip.setOnClickListener {
                    item.selectedSlot = slot
                    viewModel.updateCartItem(item)
                    notifyItemChanged(position)
                }
                chipGroupTime.addView(chip)
            }

            btnCheckout.setOnClickListener {
                if (tvDateSlot.text.isEmpty() || item.selectedSlot.isNullOrEmpty()) {
                    Toast.makeText(context, "Please select date and time slot", Toast.LENGTH_SHORT).show()
                } else {
                    val dialogBinding = DialogConfirmBookingBinding.inflate(LayoutInflater.from(context))

                    val dialog = AlertDialog.Builder(context)
                        .setView(dialogBinding.root)
                        .create()

                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                    dialogBinding.tvMessage.text = "Book tickets for ${item.title}?"

                    dialogBinding.btnYes.setOnClickListener {
                        bookMovie(item)
                        dialog.dismiss()
                    }

                    dialogBinding.btnCancel.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()

                }
            }


        }
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
