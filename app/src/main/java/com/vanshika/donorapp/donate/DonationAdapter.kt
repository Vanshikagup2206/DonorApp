package com.vanshika.donorapp.donate

import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vanshika.donorapp.R
import com.vanshika.donorapp.requests.EmergencyRequestAdapter
import com.vanshika.donorapp.requests.EmergencyRequestAdapter.ViewHolder
import java.text.SimpleDateFormat

class DonationAdapter(
    var donation: ArrayList<DonorsDataClass>,
    var donationInterface: DonationInterfae,

    ) :
    RecyclerView.Adapter<DonationAdapter.ViewHolder>() {
    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var donationType: TextView = view.findViewById(R.id.textDonationtype)
        var date: TextView = view.findViewById(R.id.textDate)
        var name: TextView = view.findViewById(R.id.textDonorName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(
            parent
                .context
        ).inflate(R.layout.item_donation, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return donation.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.donationType.setText(donation[position].donationType)
        holder.date.setText(donation[position].createdDate)
        holder.name.setText(donation[position].donorName)
        holder.itemView.setOnClickListener {
            donationInterface.clickInterface(position)
        }
//        var calendar = Calendar.getInstance().also {
//            it.time = donation[position].createddate
//        }
////        holder.date.setText(donation[position].createddate.toString())
//        holder.date.setText(SimpleDateFormat("dd/MM/yyyy").format(calendar.time))
//    }
    }
}
