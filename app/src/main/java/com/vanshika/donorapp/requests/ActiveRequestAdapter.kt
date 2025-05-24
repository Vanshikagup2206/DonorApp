package com.vanshika.donorapp.requests

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vanshika.donorapp.R

class ActiveRequestAdapter(
    private var activeRequestList: ArrayList<RecipientsDataClass>
) : RecyclerView.Adapter<ActiveRequestAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRecipientName: TextView = view.findViewById(R.id.tvRecipientName)
        val tvRequirement: TextView = view.findViewById(R.id.tvRequirement)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
        val tvHospitalLocation: TextView = view.findViewById(R.id.tvLocation)
        val tvContactNumber: TextView = view.findViewById(R.id.tvContact)
        val tvUrgency: TextView = view.findViewById(R.id.tvUrgency)
        val btnEdit: TextView = view.findViewById(R.id.btnEdit)
        val btnDelete: TextView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_active_request, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = activeRequestList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipient = activeRequestList[position]

        holder.tvRecipientName.text = recipient.name
        holder.tvHospitalLocation.text = recipient.location
        holder.tvContactNumber.text = recipient.contactNo
        holder.tvUrgency.text = when (recipient.urgencyLevel) {
            1 -> "Low"
            2 -> "Medium"
            else -> "High"
        }
        holder.tvRequirement.text = "Requirement: ${recipient.requirement}"
        holder.tvDetails.text = when (recipient.requirement) {
            "Blood", "Organ" -> "Type: ${recipient.bloodOrganRequirement}"
            "Medicine", "Money" -> "Details: ${recipient.medicineMoneyDetails}"
            else -> ""
        }
        holder.btnEdit.visibility = View.GONE
        holder.btnDelete.visibility = View.GONE
    }

    fun updateList(newList: List<RecipientsDataClass>) {
        activeRequestList.clear()
        activeRequestList.addAll(newList)
        notifyDataSetChanged()
    }
}
