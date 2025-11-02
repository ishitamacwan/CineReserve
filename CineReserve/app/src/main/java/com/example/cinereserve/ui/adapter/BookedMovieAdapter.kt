package com.example.cinereserve.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cinereserve.data.network.model.BookedMovie
import com.example.cinereserve.databinding.ItemMovieTicketBinding

class BookedMovieAdapter(
    private var items: List<BookedMovie>
) : RecyclerView.Adapter<BookedMovieAdapter.BookedMovieViewHolder>() {

    inner class BookedMovieViewHolder(val binding: ItemMovieTicketBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookedMovieViewHolder {
        val binding = ItemMovieTicketBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookedMovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookedMovieViewHolder, position: Int) {
        val item = items[position]

        Log.e("TAG", "onBindViewHolder: size of booked ${items.size}", )
        holder.binding.apply {
            textTitle.text = item.title
            textDateTime.text = "${item.date}, ${item.slot ?: ""}"
            textCinema.text = item.genre
            imagePoster.load(item.posterUrl)
            tvTicket.text = item.personCount.toString() + " Seat(s)"
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<BookedMovie>) {
        items = newItems
        notifyDataSetChanged()
    }
}
