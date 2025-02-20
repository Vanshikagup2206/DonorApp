package com.vanshika.donorapp.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vanshika.donorapp.R
import com.vanshika.donorapp.requests.RecipientsDataClass

class HighEmergencyRequestAdapter(
    var emergencyRequestList: ArrayList<RecipientsDataClass>
) :
        RecyclerView.Adapter<HighEmergencyRequestAdapter.ViewHolder>(){
            class ViewHolder(var view: View) : RecyclerView.ViewHolder(view){
                var tvRecipientName: TextView = view.findViewById(R.id.tvRecipientName)
                var tvRequirement: TextView = view.findViewById(R.id.tvRequirement)
                var tvHospitalLocation: TextView = view.findViewById(R.id.tvHospitalLocation)
                var tvContactNumber: TextView = view.findViewById(R.id.tvContactNumber)
                var tvUrgency: TextView = view.findViewById(R.id.tvUrgency)
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_high_emegency, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return emergencyRequestList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipient = emergencyRequestList[position]
        holder.tvRecipientName.setText(emergencyRequestList[position].recipientName)
        holder.tvRequirement.text = "${recipient.requestedItem}: ${recipient.bloodOrganRequirement}"
        holder.tvHospitalLocation.setText(emergencyRequestList[position].location)
        holder.tvContactNumber.setText(emergencyRequestList[position].contact)
        holder.tvUrgency.text = when (emergencyRequestList[position].urgencyLevel) {
            1 -> "Low"
            2 -> "Medium"
            else -> "High"
        }
    }
}