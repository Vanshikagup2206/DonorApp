package com.vanshika.donorapp.requests

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vanshika.donorapp.R

class EmergencyRequestAdapter(var emergencyRequestList:ArrayList<RecipientsDataClass>):
    RecyclerView.Adapter<EmergencyRequestAdapter.ViewHolder>() {
    class ViewHolder(var view:View): RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent
            .context).inflate(R.layout.item_emergency_request,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return emergencyRequestList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }
}