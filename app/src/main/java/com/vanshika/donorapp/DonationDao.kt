package com.vanshika.donorapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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
    //
    @Update
    fun updateEmergencyRequest(recipient: RecipientsDataClass)

    @Delete
    fun deleteEmergencyRequest(recipient: RecipientsDataClass)

    @Query("SELECT * FROM RecipientsDataClass WHERE requestedItem =:typeOfRequirement")
    fun getRecipientListAccToReq(typeOfRequirement : String): List<RecipientsDataClass>

    @Query("SELECT * FROM RecipientsDataClass WHERE urgencyLevel =:urgencyLevel")
    fun getHighEmergencyList(urgencyLevel : Int) : List<RecipientsDataClass>
}

