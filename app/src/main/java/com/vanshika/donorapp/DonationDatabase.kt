package com.vanshika.donorapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vanshika.donorapp.donate.`Donors DataClass`
import com.vanshika.donorapp.home.`SOSRequests DataClass`
import com.vanshika.donorapp.profile.`Donations DataClass`
import com.vanshika.donorapp.profile.`HealthRecords DataClass`
import com.vanshika.donorapp.requests.`Hospitals DataClass`
import com.vanshika.donorapp.recipient.`Recipients DataClass`

@Database(
    entities = [`Donors DataClass`::class, `Recipients DataClass`::class, `Hospitals DataClass`::class, `Donations DataClass`::class, `HealthRecords DataClass`::class, `SOSRequests DataClass`::class],
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