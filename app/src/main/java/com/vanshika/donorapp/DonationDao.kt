package com.vanshika.donorapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vanshika.donorapp.donate.DonorsDataClass
import com.vanshika.donorapp.requests.RecipientsDataClass

@Dao
interface DonationDao {
    @Insert
    fun insertDonor(donor: DonorsDataClass)

    // Get donor by ID
    @Query("SELECT * FROM DonorsDataClass WHERE donorId = :id")
    fun getDonorById(id: Int): DonorsDataClass

    @Insert
    fun insertEmergencyRequest(recipientsDataClass: RecipientsDataClass)

    @Query("SELECT * FROM RecipientsDataClass")
    fun getEmergencyRequestList(): List<RecipientsDataClass>
}