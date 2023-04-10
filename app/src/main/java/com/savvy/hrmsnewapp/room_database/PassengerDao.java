package com.savvy.hrmsnewapp.room_database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.savvy.hrmsnewapp.retrofitModel.MenuModule;

import java.util.List;

@Dao
public interface PassengerDao {

    @Insert
    long insertPassengerData(PassengerModel passengerModel);

    @Query("SELECT * FROM passengermodel")
    List<PassengerModel> getPassengerData();

    @Query("DELETE FROM passengermodel WHERE id= :id")
    void removePassengerDetail(int id);

    @Query("DELETE FROM passengermodel")
    void removeAllPassengerDetail();


    @Insert
    long insertIteneraryData(IteneraryModel iteneraryModel);

    @Query("SELECT * FROM itenerarymodel")
    List<IteneraryModel> getIteneraryData();

    @Query("DELETE FROM itenerarymodel WHERE id= :id")
    void removeIteneraryDetail(int id);

    @Query("DELETE FROM itenerarymodel")
    void removeAllItineraryData();


    @Insert
    long insertAccommodationData(AccommodationModel iteneraryModel);

    @Query("SELECT * FROM accommodationmodel")
    List<AccommodationModel> getaccommodationData();

    @Query("DELETE FROM accommodationmodel WHERE id= :id")
    void removeAccommodtionDetail(int id);

    @Query("DELETE FROM accommodationmodel ")
    void removeAllAccommodtionData();


    @Insert
    long insertAdvanceData(AdvanceModel advanceModel);

    @Query("SELECT * FROM advancemodel")
    List<AdvanceModel> getAdvanceData();

    @Query("DELETE FROM AdvanceModel WHERE id=:id")
    void removeAdvanceData(int id);

    @Query("DELETE FROM AdvanceModel")
    void removeAllAdvanceData();


    @Insert
    long insertCarDetails(CarDetailsModel carDetailsModel);


    @Query("SELECT * FROM cardetailsmodel")
    List<CarDetailsModel> getCarDetails();

    @Query("DELETE FROM cardetailsmodel WHERE id=:id")
    void removeCarDetail(int id);

    @Query("DELETE FROM cardetailsmodel")
    void removeAllCarDetail();

    @Insert
    long insertManagerMMTData(ManagerDashboardMMTModel mmtModel);

    @Query("SELECT * FROM managerdashboardmmtmodel ORDER BY id ASC LIMIT 20 OFFSET 0")
    List<ManagerDashboardMMTModel> getMDshboardMMTData();


    @Query("DELETE FROM managerdashboardmmtmodel")
    void removeDashboardData();

    @Query("SELECT * FROM managerdashboardmmtmodel ORDER BY id ASC LIMIT :limit OFFSET :lastId")
    List<ManagerDashboardMMTModel> getData(int limit, int lastId);

    @Query("SELECT * FROM managerdashboardmmtmodel")
    List<ManagerDashboardMMTModel> getAllDashboardData();

    @Query("DELETE  FROM managerdashboardmmtmodel WHERE id IN(:userIds)")
    void deleteDashBoardData(int[] userIds);

    @Insert
    long insertTrackMeDetails(TrackMeModel trackMeModel);

    @Query("Select * from TRACKMEMODEL")
    List<TrackMeModel> getTrackMeDetails();

    @Query("DELETE FROM TRACKMEMODEL")
    void deleteAllTrackMeData();


    // Offline User Related

    @Insert
    void insertOfflineUser(OfflineCredentialModel... users);

    @Query("SELECT * FROM OfflineCredentialModel")
    List<OfflineCredentialModel> getAllOfflineUser();

    @Insert
    void insertOfflinePunchInData(OfflinePunchInModel... models);

    @Query("SELECT * FROM OfflinePunchInModel")
    List<OfflinePunchInModel> getAllOfflinePunch();

    @Delete
    void deleteOfflinePunchData(OfflinePunchInModel... models);
}
