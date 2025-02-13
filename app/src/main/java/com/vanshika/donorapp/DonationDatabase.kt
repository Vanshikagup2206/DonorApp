package com.vanshika.donorapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vanshika.donorapp.donate.Donors
import com.vanshika.donorapp.home.SOSRequests
import com.vanshika.donorapp.profile.Donations
import com.vanshika.donorapp.profile.HealthRecords
import com.vanshika.donorapp.requests.Hospitals
import com.vanshika.donorapp.requests.Recipients

@Database(
    entities = [Donors::class, Recipients::class, Hospitals::class, Donations::class, HealthRecords::class, SOSRequests::class],
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