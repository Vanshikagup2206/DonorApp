package com.vanshika.donorapp.requests

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vanshika.donorapp.R
import org.w3c.dom.Text

class EmergencyRequestAdapter(
    var emergencyRequestList: ArrayList<RecipientsDataClass>,
    var requestInterface: RequestInterface
) :
    RecyclerView.Adapter<EmergencyRequestAdapter.ViewHolder>() {
    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        var tvRecipientName: TextView = view.findViewById(R.id.tvRecipientName)
        var tvRequirement: TextView = view.findViewById(R.id.tvRequirement)
        var tvHospitalLocation: TextView = view.findViewById(R.id.tvHospitalLocation)
        var tvContactNumber: TextView = view.findViewById(R.id.tvContactNumber)
        var tvUrgency: TextView = view.findViewById(R.id.tvUrgency)
        var btnEdit: TextView = view.findViewById(R.id.btnEdit)
        var btnDelete: TextView = view.findViewById(R.id.btnDelete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(
            parent
                .context
        ).inflate(R.layout.item_emergency_request, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return emergencyRequestList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipient = emergencyRequestList[position]
        holder.tvRecipientName.setText(emergencyRequestList[position].recipientName)
//        holder.tvRequirement.setText(emergencyRequestList[position].requestedItem)
        holder.tvRequirement.text = "${recipient.requestedItem}: ${recipient.specificRequirement}}"
        holder.tvHospitalLocation.setText(emergencyRequestList[position].location)
        holder.tvContactNumber.setText(emergencyRequestList[position].contact)
        holder.tvUrgency.text = when (emergencyRequestList[position].urgencyLevel) {
            1 -> "Low"
            2 -> "Medium"
            else -> "High"
        }
        holder.btnEdit.setOnClickListener {
            requestInterface.editRequest(position)
        }
        holder.btnDelete.setOnClickListener {
            requestInterface.deleteRequest(position)
        }

    }
}