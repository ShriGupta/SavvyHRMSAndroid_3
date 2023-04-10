package com.savvy.hrmsnewapp.room_database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {PassengerModel.class, IteneraryModel.class,AdvanceModel.class,CarDetailsModel.class, AccommodationModel.class,ManagerDashboardMMTModel.class,TrackMeModel.class,OfflineCredentialModel.class,OfflinePunchInModel.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PassengerDao passengerDao();
}
