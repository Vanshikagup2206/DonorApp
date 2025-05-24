package com.vanshika.donorapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vanshika.donorapp.donate.DonorsDataClass
import com.vanshika.donorapp.notification.UsersDataClass
import com.vanshika.donorapp.profile.HealthRecordsDataClass
import com.vanshika.donorapp.requests.RecipientsDataClass

@Dao
interface DonationDao {
    @Insert
    fun insertDonor(donor: DonorsDataClass)

    // Get donor by ID
    @Query("SELECT * FROM DonorsDataClass WHERE donorId = :id")
    fun getDonorById(id: Int): DonorsDataClass

    @Query("SELECT * FROM DonorsDataClass")
    fun getDonationList(): List<DonorsDataClass>

    @Insert
    fun insertRecipient(recipientsDataClass: RecipientsDataClass)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRequest(request: RecipientsDataClass)

    @Query("SELECT * FROM RecipientsDataClass")
    fun getEmergencyRequestList(): List<RecipientsDataClass>

    @Query("SELECT * FROM RecipientsDataClass WHERE userId =:userId")
    fun getEmergencyRequestAccToId(userId : Int) : RecipientsDataClass

    //
    @Update
    fun updateEmergencyRequest(recipient: RecipientsDataClass)

    @Delete
    fun deleteEmergencyRequest(recipient: RecipientsDataClass)

    @Query("SELECT * FROM RecipientsDataClass WHERE requirement =:typeOfRequirement")
    fun getRecipientListAccToReq(typeOfRequirement : String): List<RecipientsDataClass>

    @Query("SELECT * FROM RecipientsDataClass WHERE urgencyLevel =:urgencyLevel")
    fun getHighEmergencyList(urgencyLevel : Int) : List<RecipientsDataClass>

    @Insert
    fun insertToken(usersDataClass: UsersDataClass)

    @Query("SELECT * FROM users WHERE fcmToken = :token LIMIT 1")
    fun getUserByToken(token: String): UsersDataClass?

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<UsersDataClass>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHealthRecords(healthRecordsDataClass: HealthRecordsDataClass)

}

