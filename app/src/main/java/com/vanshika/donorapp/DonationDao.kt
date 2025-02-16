package com.vanshika.donorapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vanshika.donorapp.donate.DonorsDataClass

@Dao
interface DonationDao {
    @Insert
    fun insertDonor(donor: DonorsDataClass)

    // Get donor by ID
    @Query("SELECT * FROM DonorsDataClass WHERE donorId = :id")
    fun getDonorById(id: Int): DonorsDataClass
}