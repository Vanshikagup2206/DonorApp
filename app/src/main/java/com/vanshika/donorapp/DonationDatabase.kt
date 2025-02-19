package com.vanshika.donorapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vanshika.donorapp.donate.DonorsDataClass
import com.vanshika.donorapp.home.SOSRequestsDataClass
import com.vanshika.donorapp.profile.DonationsDataClass
import com.vanshika.donorapp.profile.HealthRecordsDataClass
import com.vanshika.donorapp.requests.HospitalsDataClass
import com.vanshika.donorapp.requests.RecipientsDataClass

@Database(
    entities = [DonorsDataClass::class, RecipientsDataClass::class, HospitalsDataClass::class, DonationsDataClass::class, HealthRecordsDataClass::class, SOSRequestsDataClass::class],
    version = 1,
    exportSchema = true
)
abstract class DonationDatabase : RoomDatabase() {
    abstract fun DonationDao(): DonationDao

    companion object{
        private var donationDatabase: DonationDatabase ?= null
        fun getInstance(context: Context): DonationDatabase {
            if (donationDatabase == null) {
                donationDatabase = Room.databaseBuilder(context, DonationDatabase::class.java, "DonationDatabase")
                    .allowMainThreadQueries().build()
            }
            return donationDatabase!!
        }
    }
}