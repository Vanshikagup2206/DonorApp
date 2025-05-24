package com.vanshika.donorapp.requests

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vanshika.donorapp.R

class MyRequestAdapter(
    private var myRequestList: ArrayList<RecipientsDataClass>,
    private val requestInterface: RequestInterface
) : RecyclerView.Adapter<MyRequestAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRecipientName: TextView = view.findViewById(R.id.tvRecipientName)
        val tvRequirement: TextView = view.findViewById(R.id.tvRequirement)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
        val tvHospitalLocation: TextView = view.findViewById(R.id.tvHospitalLocation)
        val tvContactNumber: TextView = view.findViewById(R.id.tvContactNumber)
        val tvUrgency: TextView = view.findViewById(R.id.tvUrgency)
        val btnEdit: TextView = view.findViewById(R.id.btnEdit)
        val btnDelete: TextView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_emergency_request, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = myRequestList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipient = myRequestList[position]

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

        holder.btnEdit.setOnClickListener {
            requestInterface.editRequest(position)
        }
        holder.btnDelete.setOnClickListener {
            requestInterface.deleteRequest(position)
        }
    }

    fun updateList(newList: List<RecipientsDataClass>) {
        myRequestList.clear()
        myRequestList.addAll(newList)
        notifyDataSetChanged()
    }
}
