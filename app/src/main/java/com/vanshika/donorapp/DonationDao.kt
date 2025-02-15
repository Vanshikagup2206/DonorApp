package com.vanshika.donorapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vanshika.donorapp.requests.RecipientsDataClass

@Dao
interface DonationDao {
    @Insert
    fun insertEmergencyRequest(recipientsDataClass: RecipientsDataClass)

    @Query("SELECT * FROM RecipientsDataClass")
    fun getEmergencyRequestList(): List<RecipientsDataClass>
}