package com.savvy.hrmsnewapp.room_database;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class PassengerDao_Impl implements PassengerDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PassengerModel> __insertionAdapterOfPassengerModel;

  private final EntityInsertionAdapter<IteneraryModel> __insertionAdapterOfIteneraryModel;

  private final EntityInsertionAdapter<AccommodationModel> __insertionAdapterOfAccommodationModel;

  private final EntityInsertionAdapter<AdvanceModel> __insertionAdapterOfAdvanceModel;

  private final EntityInsertionAdapter<CarDetailsModel> __insertionAdapterOfCarDetailsModel;

  private final EntityInsertionAdapter<ManagerDashboardMMTModel> __insertionAdapterOfManagerDashboardMMTModel;

  private final EntityInsertionAdapter<TrackMeModel> __insertionAdapterOfTrackMeModel;

  private final EntityInsertionAdapter<OfflineCredentialModel> __insertionAdapterOfOfflineCredentialModel;

  private final EntityInsertionAdapter<OfflinePunchInModel> __insertionAdapterOfOfflinePunchInModel;

  private final EntityDeletionOrUpdateAdapter<OfflinePunchInModel> __deletionAdapterOfOfflinePunchInModel;

  private final SharedSQLiteStatement __preparedStmtOfRemovePassengerDetail;

  private final SharedSQLiteStatement __preparedStmtOfRemoveAllPassengerDetail;

  private final SharedSQLiteStatement __preparedStmtOfRemoveIteneraryDetail;

  private final SharedSQLiteStatement __preparedStmtOfRemoveAllItineraryData;

  private final SharedSQLiteStatement __preparedStmtOfRemoveAccommodtionDetail;

  private final SharedSQLiteStatement __preparedStmtOfRemoveAllAccommodtionData;

  private final SharedSQLiteStatement __preparedStmtOfRemoveAdvanceData;

  private final SharedSQLiteStatement __preparedStmtOfRemoveAllAdvanceData;

  private final SharedSQLiteStatement __preparedStmtOfRemoveCarDetail;

  private final SharedSQLiteStatement __preparedStmtOfRemoveAllCarDetail;

  private final SharedSQLiteStatement __preparedStmtOfRemoveDashboardData;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllTrackMeData;

  public PassengerDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPassengerModel = new EntityInsertionAdapter<PassengerModel>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `PassengerModel` (`id`,`firstname`,`middlename`,`lastname`,`contact`,`address`,`employeetype`,`foodid`,`foodvalue`,`age`,`gender`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, PassengerModel value) {
        stmt.bindLong(1, value.getId());
        if (value.getFirstname() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getFirstname());
        }
        if (value.getMiddlename() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getMiddlename());
        }
        if (value.getLastname() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getLastname());
        }
        if (value.getContact() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getContact());
        }
        if (value.getAddress() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getAddress());
        }
        if (value.getEmployeetype() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getEmployeetype());
        }
        if (value.getFoodId() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getFoodId());
        }
        if (value.getFoodValue() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getFoodValue());
        }
        stmt.bindLong(10, value.getAge());
        if (value.getGender() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.getGender());
        }
      }
    };
    this.__insertionAdapterOfIteneraryModel = new EntityInsertionAdapter<IteneraryModel>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `IteneraryModel` (`id`,`source`,`destination`,`departuredate`,`returndate`,`mode`,`classcode`,`starttime`,`endtime`,`flightdetail`,`travelwaytype`,`sourceid`,`destinationid`,`modeid`,`classid`,`seatprefid`,`seatprefvalue`,`insurancevalue`,`frequentlyfillerno`,`specialrequest`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, IteneraryModel value) {
        stmt.bindLong(1, value.getId());
        if (value.getSource() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getSource());
        }
        if (value.getDestination() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getDestination());
        }
        if (value.getDeparturedate() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getDeparturedate());
        }
        if (value.getReturndate() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getReturndate());
        }
        if (value.getMode() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getMode());
        }
        if (value.getClasscode() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getClasscode());
        }
        if (value.getStarttime() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getStarttime());
        }
        if (value.getEndtime() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getEndtime());
        }
        if (value.getFlightdetail() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.getFlightdetail());
        }
        if (value.getTravelwaytype() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.getTravelwaytype());
        }
        if (value.getSourceid() == null) {
          stmt.bindNull(12);
        } else {
          stmt.bindString(12, value.getSourceid());
        }
        if (value.getDestinationid() == null) {
          stmt.bindNull(13);
        } else {
          stmt.bindString(13, value.getDestinationid());
        }
        if (value.getModeid() == null) {
          stmt.bindNull(14);
        } else {
          stmt.bindString(14, value.getModeid());
        }
        if (value.getClassid() == null) {
          stmt.bindNull(15);
        } else {
          stmt.bindString(15, value.getClassid());
        }
        if (value.getSeatprefid() == null) {
          stmt.bindNull(16);
        } else {
          stmt.bindString(16, value.getSeatprefid());
        }
        if (value.getSeatpreValue() == null) {
          stmt.bindNull(17);
        } else {
          stmt.bindString(17, value.getSeatpreValue());
        }
        if (value.insuranceValue == null) {
          stmt.bindNull(18);
        } else {
          stmt.bindString(18, value.insuranceValue);
        }
        if (value.getFrequentlyFillerno() == null) {
          stmt.bindNull(19);
        } else {
          stmt.bindString(19, value.getFrequentlyFillerno());
        }
        if (value.getSpecialRequest() == null) {
          stmt.bindNull(20);
        } else {
          stmt.bindString(20, value.getSpecialRequest());
        }
      }
    };
    this.__insertionAdapterOfAccommodationModel = new EntityInsertionAdapter<AccommodationModel>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `AccommodationModel` (`id`,`ciy`,`fromdate`,`todate`,`checkintime`,`checkouttime`,`hotellocation`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, AccommodationModel value) {
        stmt.bindLong(1, value.getId());
        if (value.getCity() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getCity());
        }
        if (value.getFromDate() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getFromDate());
        }
        if (value.getTodate() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getTodate());
        }
        if (value.getCheckintime() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getCheckintime());
        }
        if (value.getCheckouttime() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getCheckouttime());
        }
        if (value.getHotellocation() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getHotellocation());
        }
      }
    };
    this.__insertionAdapterOfAdvanceModel = new EntityInsertionAdapter<AdvanceModel>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `AdvanceModel` (`id`,`amount`,`currency`,`paymode`,`remarks`,`paymodeid`,`currencyid`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, AdvanceModel value) {
        stmt.bindLong(1, value.getId());
        if (value.getAmount() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getAmount());
        }
        if (value.getCurrency() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getCurrency());
        }
        if (value.getPaymode() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getPaymode());
        }
        if (value.getRemarks() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getRemarks());
        }
        if (value.getPaymodeid() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getPaymodeid());
        }
        if (value.getCurrencyid() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getCurrencyid());
        }
      }
    };
    this.__insertionAdapterOfCarDetailsModel = new EntityInsertionAdapter<CarDetailsModel>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `CarDetailsModel` (`id`,`pickupdate`,`pickupat`,`dropat`,`pickuptime`,`releasetime`,`comment`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CarDetailsModel value) {
        stmt.bindLong(1, value.getId());
        if (value.getPickupdate() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getPickupdate());
        }
        if (value.getPickupat() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getPickupat());
        }
        if (value.getDropat() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getDropat());
        }
        if (value.getPickuptime() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getPickuptime());
        }
        if (value.getReleasetime() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getReleasetime());
        }
        if (value.getComment() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getComment());
        }
      }
    };
    this.__insertionAdapterOfManagerDashboardMMTModel = new EntityInsertionAdapter<ManagerDashboardMMTModel>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `ManagerDashboardMMTModel` (`id`,`EMPLOYEE_CODE`,`EMPLOYEE_NAME`,`AVG_WORKTIME`,`AVG_IN_TIME`,`LEAVE`,`WFH`,`OD`,`AVG_WORKED1`,`AVG_OUT_TIME`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ManagerDashboardMMTModel value) {
        stmt.bindLong(1, value.getId());
        if (value.getEMPLOYEE_CODE() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getEMPLOYEE_CODE());
        }
        if (value.getEMPLOYEE_NAME() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getEMPLOYEE_NAME());
        }
        if (value.getAVG_WORKTIME() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getAVG_WORKTIME());
        }
        if (value.getAVG_IN_TIME() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getAVG_IN_TIME());
        }
        if (value.getLEAVE() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getLEAVE());
        }
        if (value.getWFH() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getWFH());
        }
        if (value.getOD() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getOD());
        }
        if (value.getAVG_WORKED1() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getAVG_WORKED1());
        }
        if (value.getAVG_OUT_TIME() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.getAVG_OUT_TIME());
        }
      }
    };
    this.__insertionAdapterOfTrackMeModel = new EntityInsertionAdapter<TrackMeModel>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `TrackMeModel` (`id`,`trackMeDetails`) VALUES (nullif(?, 0),?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, TrackMeModel value) {
        stmt.bindLong(1, value.getId());
        if (value.getTrackMeDetails() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getTrackMeDetails());
        }
      }
    };
    this.__insertionAdapterOfOfflineCredentialModel = new EntityInsertionAdapter<OfflineCredentialModel>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `OfflineCredentialModel` (`uid`,`username`,`password`) VALUES (?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, OfflineCredentialModel value) {
        stmt.bindLong(1, value.uid);
        if (value.getUsername() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getUsername());
        }
        if (value.getPassword() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getPassword());
        }
      }
    };
    this.__insertionAdapterOfOfflinePunchInModel = new EntityInsertionAdapter<OfflinePunchInModel>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `OfflinePunchInModel` (`id`,`user_name`,`latitude`,`longitude`,`currentdate`,`currenttime`,`comment`,`location`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, OfflinePunchInModel value) {
        stmt.bindLong(1, value.getId());
        if (value.getUserName() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getUserName());
        }
        if (value.getLatitude() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getLatitude());
        }
        if (value.getLongitude() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getLongitude());
        }
        if (value.getCurrentdate() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getCurrentdate());
        }
        if (value.getCurrenttime() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getCurrenttime());
        }
        if (value.getComment() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getComment());
        }
        if (value.getLocation() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getLocation());
        }
      }
    };
    this.__deletionAdapterOfOfflinePunchInModel = new EntityDeletionOrUpdateAdapter<OfflinePunchInModel>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `OfflinePunchInModel` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, OfflinePunchInModel value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__preparedStmtOfRemovePassengerDetail = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM passengermodel WHERE id= ?";
        return _query;
      }
    };
    this.__preparedStmtOfRemoveAllPassengerDetail = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM passengermodel";
        return _query;
      }
    };
    this.__preparedStmtOfRemoveIteneraryDetail = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM itenerarymodel WHERE id= ?";
        return _query;
      }
    };
    this.__preparedStmtOfRemoveAllItineraryData = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM itenerarymodel";
        return _query;
      }
    };
    this.__preparedStmtOfRemoveAccommodtionDetail = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM accommodationmodel WHERE id= ?";
        return _query;
      }
    };
    this.__preparedStmtOfRemoveAllAccommodtionData = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM accommodationmodel ";
        return _query;
      }
    };
    this.__preparedStmtOfRemoveAdvanceData = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM AdvanceModel WHERE id=?";
        return _query;
      }
    };
    this.__preparedStmtOfRemoveAllAdvanceData = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM AdvanceModel";
        return _query;
      }
    };
    this.__preparedStmtOfRemoveCarDetail = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM cardetailsmodel WHERE id=?";
        return _query;
      }
    };
    this.__preparedStmtOfRemoveAllCarDetail = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM cardetailsmodel";
        return _query;
      }
    };
    this.__preparedStmtOfRemoveDashboardData = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM managerdashboardmmtmodel";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllTrackMeData = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM TRACKMEMODEL";
        return _query;
      }
    };
  }

  @Override
  public long insertPassengerData(final PassengerModel passengerModel) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfPassengerModel.insertAndReturnId(passengerModel);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public long insertIteneraryData(final IteneraryModel iteneraryModel) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfIteneraryModel.insertAndReturnId(iteneraryModel);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public long insertAccommodationData(final AccommodationModel iteneraryModel) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfAccommodationModel.insertAndReturnId(iteneraryModel);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public long insertAdvanceData(final AdvanceModel advanceModel) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfAdvanceModel.insertAndReturnId(advanceModel);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public long insertCarDetails(final CarDetailsModel carDetailsModel) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfCarDetailsModel.insertAndReturnId(carDetailsModel);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public long insertManagerMMTData(final ManagerDashboardMMTModel mmtModel) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfManagerDashboardMMTModel.insertAndReturnId(mmtModel);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public long insertTrackMeDetails(final TrackMeModel trackMeModel) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfTrackMeModel.insertAndReturnId(trackMeModel);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertOfflineUser(final OfflineCredentialModel... users) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfOfflineCredentialModel.insert(users);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void insertOfflinePunchInData(final OfflinePunchInModel... models) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfOfflinePunchInModel.insert(models);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteOfflinePunchData(final OfflinePunchInModel... models) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfOfflinePunchInModel.handleMultiple(models);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void removePassengerDetail(final int id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfRemovePassengerDetail.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfRemovePassengerDetail.release(_stmt);
    }
  }

  @Override
  public void removeAllPassengerDetail() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveAllPassengerDetail.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfRemoveAllPassengerDetail.release(_stmt);
    }
  }

  @Override
  public void removeIteneraryDetail(final int id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveIteneraryDetail.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfRemoveIteneraryDetail.release(_stmt);
    }
  }

  @Override
  public void removeAllItineraryData() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveAllItineraryData.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfRemoveAllItineraryData.release(_stmt);
    }
  }

  @Override
  public void removeAccommodtionDetail(final int id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveAccommodtionDetail.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfRemoveAccommodtionDetail.release(_stmt);
    }
  }

  @Override
  public void removeAllAccommodtionData() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveAllAccommodtionData.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfRemoveAllAccommodtionData.release(_stmt);
    }
  }

  @Override
  public void removeAdvanceData(final int id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveAdvanceData.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfRemoveAdvanceData.release(_stmt);
    }
  }

  @Override
  public void removeAllAdvanceData() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveAllAdvanceData.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfRemoveAllAdvanceData.release(_stmt);
    }
  }

  @Override
  public void removeCarDetail(final int id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveCarDetail.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfRemoveCarDetail.release(_stmt);
    }
  }

  @Override
  public void removeAllCarDetail() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveAllCarDetail.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfRemoveAllCarDetail.release(_stmt);
    }
  }

  @Override
  public void removeDashboardData() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveDashboardData.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfRemoveDashboardData.release(_stmt);
    }
  }

  @Override
  public void deleteAllTrackMeData() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllTrackMeData.acquire();
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteAllTrackMeData.release(_stmt);
    }
  }

  @Override
  public List<PassengerModel> getPassengerData() {
    final String _sql = "SELECT * FROM passengermodel";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfFirstname = CursorUtil.getColumnIndexOrThrow(_cursor, "firstname");
      final int _cursorIndexOfMiddlename = CursorUtil.getColumnIndexOrThrow(_cursor, "middlename");
      final int _cursorIndexOfLastname = CursorUtil.getColumnIndexOrThrow(_cursor, "lastname");
      final int _cursorIndexOfContact = CursorUtil.getColumnIndexOrThrow(_cursor, "contact");
      final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
      final int _cursorIndexOfEmployeetype = CursorUtil.getColumnIndexOrThrow(_cursor, "employeetype");
      final int _cursorIndexOfFoodId = CursorUtil.getColumnIndexOrThrow(_cursor, "foodid");
      final int _cursorIndexOfFoodValue = CursorUtil.getColumnIndexOrThrow(_cursor, "foodvalue");
      final int _cursorIndexOfAge = CursorUtil.getColumnIndexOrThrow(_cursor, "age");
      final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
      final List<PassengerModel> _result = new ArrayList<PassengerModel>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final PassengerModel _item;
        _item = new PassengerModel();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpFirstname;
        _tmpFirstname = _cursor.getString(_cursorIndexOfFirstname);
        _item.setFirstname(_tmpFirstname);
        final String _tmpMiddlename;
        _tmpMiddlename = _cursor.getString(_cursorIndexOfMiddlename);
        _item.setMiddlename(_tmpMiddlename);
        final String _tmpLastname;
        _tmpLastname = _cursor.getString(_cursorIndexOfLastname);
        _item.setLastname(_tmpLastname);
        final String _tmpContact;
        _tmpContact = _cursor.getString(_cursorIndexOfContact);
        _item.setContact(_tmpContact);
        final String _tmpAddress;
        _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
        _item.setAddress(_tmpAddress);
        final String _tmpEmployeetype;
        _tmpEmployeetype = _cursor.getString(_cursorIndexOfEmployeetype);
        _item.setEmployeetype(_tmpEmployeetype);
        final String _tmpFoodId;
        _tmpFoodId = _cursor.getString(_cursorIndexOfFoodId);
        _item.setFoodId(_tmpFoodId);
        final String _tmpFoodValue;
        _tmpFoodValue = _cursor.getString(_cursorIndexOfFoodValue);
        _item.setFoodValue(_tmpFoodValue);
        final int _tmpAge;
        _tmpAge = _cursor.getInt(_cursorIndexOfAge);
        _item.setAge(_tmpAge);
        final String _tmpGender;
        _tmpGender = _cursor.getString(_cursorIndexOfGender);
        _item.setGender(_tmpGender);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<IteneraryModel> getIteneraryData() {
    final String _sql = "SELECT * FROM itenerarymodel";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
      final int _cursorIndexOfDestination = CursorUtil.getColumnIndexOrThrow(_cursor, "destination");
      final int _cursorIndexOfDeparturedate = CursorUtil.getColumnIndexOrThrow(_cursor, "departuredate");
      final int _cursorIndexOfReturndate = CursorUtil.getColumnIndexOrThrow(_cursor, "returndate");
      final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
      final int _cursorIndexOfClasscode = CursorUtil.getColumnIndexOrThrow(_cursor, "classcode");
      final int _cursorIndexOfStarttime = CursorUtil.getColumnIndexOrThrow(_cursor, "starttime");
      final int _cursorIndexOfEndtime = CursorUtil.getColumnIndexOrThrow(_cursor, "endtime");
      final int _cursorIndexOfFlightdetail = CursorUtil.getColumnIndexOrThrow(_cursor, "flightdetail");
      final int _cursorIndexOfTravelwaytype = CursorUtil.getColumnIndexOrThrow(_cursor, "travelwaytype");
      final int _cursorIndexOfSourceid = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceid");
      final int _cursorIndexOfDestinationid = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationid");
      final int _cursorIndexOfModeid = CursorUtil.getColumnIndexOrThrow(_cursor, "modeid");
      final int _cursorIndexOfClassid = CursorUtil.getColumnIndexOrThrow(_cursor, "classid");
      final int _cursorIndexOfSeatprefid = CursorUtil.getColumnIndexOrThrow(_cursor, "seatprefid");
      final int _cursorIndexOfSeatpreValue = CursorUtil.getColumnIndexOrThrow(_cursor, "seatprefvalue");
      final int _cursorIndexOfInsuranceValue = CursorUtil.getColumnIndexOrThrow(_cursor, "insurancevalue");
      final int _cursorIndexOfFrequentlyFillerno = CursorUtil.getColumnIndexOrThrow(_cursor, "frequentlyfillerno");
      final int _cursorIndexOfSpecialRequest = CursorUtil.getColumnIndexOrThrow(_cursor, "specialrequest");
      final List<IteneraryModel> _result = new ArrayList<IteneraryModel>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final IteneraryModel _item;
        _item = new IteneraryModel();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpSource;
        _tmpSource = _cursor.getString(_cursorIndexOfSource);
        _item.setSource(_tmpSource);
        final String _tmpDestination;
        _tmpDestination = _cursor.getString(_cursorIndexOfDestination);
        _item.setDestination(_tmpDestination);
        final String _tmpDeparturedate;
        _tmpDeparturedate = _cursor.getString(_cursorIndexOfDeparturedate);
        _item.setDeparturedate(_tmpDeparturedate);
        final String _tmpReturndate;
        _tmpReturndate = _cursor.getString(_cursorIndexOfReturndate);
        _item.setReturndate(_tmpReturndate);
        final String _tmpMode;
        _tmpMode = _cursor.getString(_cursorIndexOfMode);
        _item.setMode(_tmpMode);
        final String _tmpClasscode;
        _tmpClasscode = _cursor.getString(_cursorIndexOfClasscode);
        _item.setClasscode(_tmpClasscode);
        final String _tmpStarttime;
        _tmpStarttime = _cursor.getString(_cursorIndexOfStarttime);
        _item.setStarttime(_tmpStarttime);
        final String _tmpEndtime;
        _tmpEndtime = _cursor.getString(_cursorIndexOfEndtime);
        _item.setEndtime(_tmpEndtime);
        final String _tmpFlightdetail;
        _tmpFlightdetail = _cursor.getString(_cursorIndexOfFlightdetail);
        _item.setFlightdetail(_tmpFlightdetail);
        final String _tmpTravelwaytype;
        _tmpTravelwaytype = _cursor.getString(_cursorIndexOfTravelwaytype);
        _item.setTravelwaytype(_tmpTravelwaytype);
        final String _tmpSourceid;
        _tmpSourceid = _cursor.getString(_cursorIndexOfSourceid);
        _item.setSourceid(_tmpSourceid);
        final String _tmpDestinationid;
        _tmpDestinationid = _cursor.getString(_cursorIndexOfDestinationid);
        _item.setDestinationid(_tmpDestinationid);
        final String _tmpModeid;
        _tmpModeid = _cursor.getString(_cursorIndexOfModeid);
        _item.setModeid(_tmpModeid);
        final String _tmpClassid;
        _tmpClassid = _cursor.getString(_cursorIndexOfClassid);
        _item.setClassid(_tmpClassid);
        final String _tmpSeatprefid;
        _tmpSeatprefid = _cursor.getString(_cursorIndexOfSeatprefid);
        _item.setSeatprefid(_tmpSeatprefid);
        final String _tmpSeatpreValue;
        _tmpSeatpreValue = _cursor.getString(_cursorIndexOfSeatpreValue);
        _item.setSeatpreValue(_tmpSeatpreValue);
        final String _tmpInsuranceValue;
        _tmpInsuranceValue = _cursor.getString(_cursorIndexOfInsuranceValue);
        _item.setInsuranceValue(_tmpInsuranceValue);
        final String _tmpFrequentlyFillerno;
        _tmpFrequentlyFillerno = _cursor.getString(_cursorIndexOfFrequentlyFillerno);
        _item.setFrequentlyFillerno(_tmpFrequentlyFillerno);
        final String _tmpSpecialRequest;
        _tmpSpecialRequest = _cursor.getString(_cursorIndexOfSpecialRequest);
        _item.setSpecialRequest(_tmpSpecialRequest);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<AccommodationModel> getaccommodationData() {
    final String _sql = "SELECT * FROM accommodationmodel";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "ciy");
      final int _cursorIndexOfFromDate = CursorUtil.getColumnIndexOrThrow(_cursor, "fromdate");
      final int _cursorIndexOfTodate = CursorUtil.getColumnIndexOrThrow(_cursor, "todate");
      final int _cursorIndexOfCheckintime = CursorUtil.getColumnIndexOrThrow(_cursor, "checkintime");
      final int _cursorIndexOfCheckouttime = CursorUtil.getColumnIndexOrThrow(_cursor, "checkouttime");
      final int _cursorIndexOfHotellocation = CursorUtil.getColumnIndexOrThrow(_cursor, "hotellocation");
      final List<AccommodationModel> _result = new ArrayList<AccommodationModel>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final AccommodationModel _item;
        _item = new AccommodationModel();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpCity;
        _tmpCity = _cursor.getString(_cursorIndexOfCity);
        _item.setCity(_tmpCity);
        final String _tmpFromDate;
        _tmpFromDate = _cursor.getString(_cursorIndexOfFromDate);
        _item.setFromDate(_tmpFromDate);
        final String _tmpTodate;
        _tmpTodate = _cursor.getString(_cursorIndexOfTodate);
        _item.setTodate(_tmpTodate);
        final String _tmpCheckintime;
        _tmpCheckintime = _cursor.getString(_cursorIndexOfCheckintime);
        _item.setCheckintime(_tmpCheckintime);
        final String _tmpCheckouttime;
        _tmpCheckouttime = _cursor.getString(_cursorIndexOfCheckouttime);
        _item.setCheckouttime(_tmpCheckouttime);
        final String _tmpHotellocation;
        _tmpHotellocation = _cursor.getString(_cursorIndexOfHotellocation);
        _item.setHotellocation(_tmpHotellocation);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<AdvanceModel> getAdvanceData() {
    final String _sql = "SELECT * FROM advancemodel";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
      final int _cursorIndexOfCurrency = CursorUtil.getColumnIndexOrThrow(_cursor, "currency");
      final int _cursorIndexOfPaymode = CursorUtil.getColumnIndexOrThrow(_cursor, "paymode");
      final int _cursorIndexOfRemarks = CursorUtil.getColumnIndexOrThrow(_cursor, "remarks");
      final int _cursorIndexOfPaymodeid = CursorUtil.getColumnIndexOrThrow(_cursor, "paymodeid");
      final int _cursorIndexOfCurrencyid = CursorUtil.getColumnIndexOrThrow(_cursor, "currencyid");
      final List<AdvanceModel> _result = new ArrayList<AdvanceModel>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final AdvanceModel _item;
        _item = new AdvanceModel();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpAmount;
        _tmpAmount = _cursor.getString(_cursorIndexOfAmount);
        _item.setAmount(_tmpAmount);
        final String _tmpCurrency;
        _tmpCurrency = _cursor.getString(_cursorIndexOfCurrency);
        _item.setCurrency(_tmpCurrency);
        final String _tmpPaymode;
        _tmpPaymode = _cursor.getString(_cursorIndexOfPaymode);
        _item.setPaymode(_tmpPaymode);
        final String _tmpRemarks;
        _tmpRemarks = _cursor.getString(_cursorIndexOfRemarks);
        _item.setRemarks(_tmpRemarks);
        final String _tmpPaymodeid;
        _tmpPaymodeid = _cursor.getString(_cursorIndexOfPaymodeid);
        _item.setPaymodeid(_tmpPaymodeid);
        final String _tmpCurrencyid;
        _tmpCurrencyid = _cursor.getString(_cursorIndexOfCurrencyid);
        _item.setCurrencyid(_tmpCurrencyid);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<CarDetailsModel> getCarDetails() {
    final String _sql = "SELECT * FROM cardetailsmodel";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfPickupdate = CursorUtil.getColumnIndexOrThrow(_cursor, "pickupdate");
      final int _cursorIndexOfPickupat = CursorUtil.getColumnIndexOrThrow(_cursor, "pickupat");
      final int _cursorIndexOfDropat = CursorUtil.getColumnIndexOrThrow(_cursor, "dropat");
      final int _cursorIndexOfPickuptime = CursorUtil.getColumnIndexOrThrow(_cursor, "pickuptime");
      final int _cursorIndexOfReleasetime = CursorUtil.getColumnIndexOrThrow(_cursor, "releasetime");
      final int _cursorIndexOfComment = CursorUtil.getColumnIndexOrThrow(_cursor, "comment");
      final List<CarDetailsModel> _result = new ArrayList<CarDetailsModel>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final CarDetailsModel _item;
        _item = new CarDetailsModel();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpPickupdate;
        _tmpPickupdate = _cursor.getString(_cursorIndexOfPickupdate);
        _item.setPickupdate(_tmpPickupdate);
        final String _tmpPickupat;
        _tmpPickupat = _cursor.getString(_cursorIndexOfPickupat);
        _item.setPickupat(_tmpPickupat);
        final String _tmpDropat;
        _tmpDropat = _cursor.getString(_cursorIndexOfDropat);
        _item.setDropat(_tmpDropat);
        final String _tmpPickuptime;
        _tmpPickuptime = _cursor.getString(_cursorIndexOfPickuptime);
        _item.setPickuptime(_tmpPickuptime);
        final String _tmpReleasetime;
        _tmpReleasetime = _cursor.getString(_cursorIndexOfReleasetime);
        _item.setReleasetime(_tmpReleasetime);
        final String _tmpComment;
        _tmpComment = _cursor.getString(_cursorIndexOfComment);
        _item.setComment(_tmpComment);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ManagerDashboardMMTModel> getMDshboardMMTData() {
    final String _sql = "SELECT * FROM managerdashboardmmtmodel ORDER BY id ASC LIMIT 20 OFFSET 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfEMPLOYEECODE = CursorUtil.getColumnIndexOrThrow(_cursor, "EMPLOYEE_CODE");
      final int _cursorIndexOfEMPLOYEENAME = CursorUtil.getColumnIndexOrThrow(_cursor, "EMPLOYEE_NAME");
      final int _cursorIndexOfAVGWORKTIME = CursorUtil.getColumnIndexOrThrow(_cursor, "AVG_WORKTIME");
      final int _cursorIndexOfAVGINTIME = CursorUtil.getColumnIndexOrThrow(_cursor, "AVG_IN_TIME");
      final int _cursorIndexOfLEAVE = CursorUtil.getColumnIndexOrThrow(_cursor, "LEAVE");
      final int _cursorIndexOfWFH = CursorUtil.getColumnIndexOrThrow(_cursor, "WFH");
      final int _cursorIndexOfOD = CursorUtil.getColumnIndexOrThrow(_cursor, "OD");
      final int _cursorIndexOfAVGWORKED1 = CursorUtil.getColumnIndexOrThrow(_cursor, "AVG_WORKED1");
      final int _cursorIndexOfAVGOUTTIME = CursorUtil.getColumnIndexOrThrow(_cursor, "AVG_OUT_TIME");
      final List<ManagerDashboardMMTModel> _result = new ArrayList<ManagerDashboardMMTModel>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ManagerDashboardMMTModel _item;
        _item = new ManagerDashboardMMTModel();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpEMPLOYEE_CODE;
        _tmpEMPLOYEE_CODE = _cursor.getString(_cursorIndexOfEMPLOYEECODE);
        _item.setEMPLOYEE_CODE(_tmpEMPLOYEE_CODE);
        final String _tmpEMPLOYEE_NAME;
        _tmpEMPLOYEE_NAME = _cursor.getString(_cursorIndexOfEMPLOYEENAME);
        _item.setEMPLOYEE_NAME(_tmpEMPLOYEE_NAME);
        final String _tmpAVG_WORKTIME;
        _tmpAVG_WORKTIME = _cursor.getString(_cursorIndexOfAVGWORKTIME);
        _item.setAVG_WORKTIME(_tmpAVG_WORKTIME);
        final String _tmpAVG_IN_TIME;
        _tmpAVG_IN_TIME = _cursor.getString(_cursorIndexOfAVGINTIME);
        _item.setAVG_IN_TIME(_tmpAVG_IN_TIME);
        final String _tmpLEAVE;
        _tmpLEAVE = _cursor.getString(_cursorIndexOfLEAVE);
        _item.setLEAVE(_tmpLEAVE);
        final String _tmpWFH;
        _tmpWFH = _cursor.getString(_cursorIndexOfWFH);
        _item.setWFH(_tmpWFH);
        final String _tmpOD;
        _tmpOD = _cursor.getString(_cursorIndexOfOD);
        _item.setOD(_tmpOD);
        final String _tmpAVG_WORKED1;
        _tmpAVG_WORKED1 = _cursor.getString(_cursorIndexOfAVGWORKED1);
        _item.setAVG_WORKED1(_tmpAVG_WORKED1);
        final String _tmpAVG_OUT_TIME;
        _tmpAVG_OUT_TIME = _cursor.getString(_cursorIndexOfAVGOUTTIME);
        _item.setAVG_OUT_TIME(_tmpAVG_OUT_TIME);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ManagerDashboardMMTModel> getData(final int limit, final int lastId) {
    final String _sql = "SELECT * FROM managerdashboardmmtmodel ORDER BY id ASC LIMIT ? OFFSET ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    _argIndex = 2;
    _statement.bindLong(_argIndex, lastId);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfEMPLOYEECODE = CursorUtil.getColumnIndexOrThrow(_cursor, "EMPLOYEE_CODE");
      final int _cursorIndexOfEMPLOYEENAME = CursorUtil.getColumnIndexOrThrow(_cursor, "EMPLOYEE_NAME");
      final int _cursorIndexOfAVGWORKTIME = CursorUtil.getColumnIndexOrThrow(_cursor, "AVG_WORKTIME");
      final int _cursorIndexOfAVGINTIME = CursorUtil.getColumnIndexOrThrow(_cursor, "AVG_IN_TIME");
      final int _cursorIndexOfLEAVE = CursorUtil.getColumnIndexOrThrow(_cursor, "LEAVE");
      final int _cursorIndexOfWFH = CursorUtil.getColumnIndexOrThrow(_cursor, "WFH");
      final int _cursorIndexOfOD = CursorUtil.getColumnIndexOrThrow(_cursor, "OD");
      final int _cursorIndexOfAVGWORKED1 = CursorUtil.getColumnIndexOrThrow(_cursor, "AVG_WORKED1");
      final int _cursorIndexOfAVGOUTTIME = CursorUtil.getColumnIndexOrThrow(_cursor, "AVG_OUT_TIME");
      final List<ManagerDashboardMMTModel> _result = new ArrayList<ManagerDashboardMMTModel>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ManagerDashboardMMTModel _item;
        _item = new ManagerDashboardMMTModel();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpEMPLOYEE_CODE;
        _tmpEMPLOYEE_CODE = _cursor.getString(_cursorIndexOfEMPLOYEECODE);
        _item.setEMPLOYEE_CODE(_tmpEMPLOYEE_CODE);
        final String _tmpEMPLOYEE_NAME;
        _tmpEMPLOYEE_NAME = _cursor.getString(_cursorIndexOfEMPLOYEENAME);
        _item.setEMPLOYEE_NAME(_tmpEMPLOYEE_NAME);
        final String _tmpAVG_WORKTIME;
        _tmpAVG_WORKTIME = _cursor.getString(_cursorIndexOfAVGWORKTIME);
        _item.setAVG_WORKTIME(_tmpAVG_WORKTIME);
        final String _tmpAVG_IN_TIME;
        _tmpAVG_IN_TIME = _cursor.getString(_cursorIndexOfAVGINTIME);
        _item.setAVG_IN_TIME(_tmpAVG_IN_TIME);
        final String _tmpLEAVE;
        _tmpLEAVE = _cursor.getString(_cursorIndexOfLEAVE);
        _item.setLEAVE(_tmpLEAVE);
        final String _tmpWFH;
        _tmpWFH = _cursor.getString(_cursorIndexOfWFH);
        _item.setWFH(_tmpWFH);
        final String _tmpOD;
        _tmpOD = _cursor.getString(_cursorIndexOfOD);
        _item.setOD(_tmpOD);
        final String _tmpAVG_WORKED1;
        _tmpAVG_WORKED1 = _cursor.getString(_cursorIndexOfAVGWORKED1);
        _item.setAVG_WORKED1(_tmpAVG_WORKED1);
        final String _tmpAVG_OUT_TIME;
        _tmpAVG_OUT_TIME = _cursor.getString(_cursorIndexOfAVGOUTTIME);
        _item.setAVG_OUT_TIME(_tmpAVG_OUT_TIME);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ManagerDashboardMMTModel> getAllDashboardData() {
    final String _sql = "SELECT * FROM managerdashboardmmtmodel";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfEMPLOYEECODE = CursorUtil.getColumnIndexOrThrow(_cursor, "EMPLOYEE_CODE");
      final int _cursorIndexOfEMPLOYEENAME = CursorUtil.getColumnIndexOrThrow(_cursor, "EMPLOYEE_NAME");
      final int _cursorIndexOfAVGWORKTIME = CursorUtil.getColumnIndexOrThrow(_cursor, "AVG_WORKTIME");
      final int _cursorIndexOfAVGINTIME = CursorUtil.getColumnIndexOrThrow(_cursor, "AVG_IN_TIME");
      final int _cursorIndexOfLEAVE = CursorUtil.getColumnIndexOrThrow(_cursor, "LEAVE");
      final int _cursorIndexOfWFH = CursorUtil.getColumnIndexOrThrow(_cursor, "WFH");
      final int _cursorIndexOfOD = CursorUtil.getColumnIndexOrThrow(_cursor, "OD");
      final int _cursorIndexOfAVGWORKED1 = CursorUtil.getColumnIndexOrThrow(_cursor, "AVG_WORKED1");
      final int _cursorIndexOfAVGOUTTIME = CursorUtil.getColumnIndexOrThrow(_cursor, "AVG_OUT_TIME");
      final List<ManagerDashboardMMTModel> _result = new ArrayList<ManagerDashboardMMTModel>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ManagerDashboardMMTModel _item;
        _item = new ManagerDashboardMMTModel();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpEMPLOYEE_CODE;
        _tmpEMPLOYEE_CODE = _cursor.getString(_cursorIndexOfEMPLOYEECODE);
        _item.setEMPLOYEE_CODE(_tmpEMPLOYEE_CODE);
        final String _tmpEMPLOYEE_NAME;
        _tmpEMPLOYEE_NAME = _cursor.getString(_cursorIndexOfEMPLOYEENAME);
        _item.setEMPLOYEE_NAME(_tmpEMPLOYEE_NAME);
        final String _tmpAVG_WORKTIME;
        _tmpAVG_WORKTIME = _cursor.getString(_cursorIndexOfAVGWORKTIME);
        _item.setAVG_WORKTIME(_tmpAVG_WORKTIME);
        final String _tmpAVG_IN_TIME;
        _tmpAVG_IN_TIME = _cursor.getString(_cursorIndexOfAVGINTIME);
        _item.setAVG_IN_TIME(_tmpAVG_IN_TIME);
        final String _tmpLEAVE;
        _tmpLEAVE = _cursor.getString(_cursorIndexOfLEAVE);
        _item.setLEAVE(_tmpLEAVE);
        final String _tmpWFH;
        _tmpWFH = _cursor.getString(_cursorIndexOfWFH);
        _item.setWFH(_tmpWFH);
        final String _tmpOD;
        _tmpOD = _cursor.getString(_cursorIndexOfOD);
        _item.setOD(_tmpOD);
        final String _tmpAVG_WORKED1;
        _tmpAVG_WORKED1 = _cursor.getString(_cursorIndexOfAVGWORKED1);
        _item.setAVG_WORKED1(_tmpAVG_WORKED1);
        final String _tmpAVG_OUT_TIME;
        _tmpAVG_OUT_TIME = _cursor.getString(_cursorIndexOfAVGOUTTIME);
        _item.setAVG_OUT_TIME(_tmpAVG_OUT_TIME);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<TrackMeModel> getTrackMeDetails() {
    final String _sql = "Select * from TRACKMEMODEL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTrackMeDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "trackMeDetails");
      final List<TrackMeModel> _result = new ArrayList<TrackMeModel>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final TrackMeModel _item;
        _item = new TrackMeModel();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpTrackMeDetails;
        _tmpTrackMeDetails = _cursor.getString(_cursorIndexOfTrackMeDetails);
        _item.setTrackMeDetails(_tmpTrackMeDetails);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<OfflineCredentialModel> getAllOfflineUser() {
    final String _sql = "SELECT * FROM OfflineCredentialModel";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfUid = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
      final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
      final int _cursorIndexOfPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "password");
      final List<OfflineCredentialModel> _result = new ArrayList<OfflineCredentialModel>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final OfflineCredentialModel _item;
        _item = new OfflineCredentialModel();
        _item.uid = _cursor.getInt(_cursorIndexOfUid);
        final String _tmpUsername;
        _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
        _item.setUsername(_tmpUsername);
        final String _tmpPassword;
        _tmpPassword = _cursor.getString(_cursorIndexOfPassword);
        _item.setPassword(_tmpPassword);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<OfflinePunchInModel> getAllOfflinePunch() {
    final String _sql = "SELECT * FROM OfflinePunchInModel";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUserName = CursorUtil.getColumnIndexOrThrow(_cursor, "user_name");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfCurrentdate = CursorUtil.getColumnIndexOrThrow(_cursor, "currentdate");
      final int _cursorIndexOfCurrenttime = CursorUtil.getColumnIndexOrThrow(_cursor, "currenttime");
      final int _cursorIndexOfComment = CursorUtil.getColumnIndexOrThrow(_cursor, "comment");
      final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
      final List<OfflinePunchInModel> _result = new ArrayList<OfflinePunchInModel>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final OfflinePunchInModel _item;
        _item = new OfflinePunchInModel();
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmpUserName;
        _tmpUserName = _cursor.getString(_cursorIndexOfUserName);
        _item.setUserName(_tmpUserName);
        final String _tmpLatitude;
        _tmpLatitude = _cursor.getString(_cursorIndexOfLatitude);
        _item.setLatitude(_tmpLatitude);
        final String _tmpLongitude;
        _tmpLongitude = _cursor.getString(_cursorIndexOfLongitude);
        _item.setLongitude(_tmpLongitude);
        final String _tmpCurrentdate;
        _tmpCurrentdate = _cursor.getString(_cursorIndexOfCurrentdate);
        _item.setCurrentdate(_tmpCurrentdate);
        final String _tmpCurrenttime;
        _tmpCurrenttime = _cursor.getString(_cursorIndexOfCurrenttime);
        _item.setCurrenttime(_tmpCurrenttime);
        final String _tmpComment;
        _tmpComment = _cursor.getString(_cursorIndexOfComment);
        _item.setComment(_tmpComment);
        final String _tmpLocation;
        _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
        _item.setLocation(_tmpLocation);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public void deleteDashBoardData(final int[] userIds) {
    __db.assertNotSuspendingTransaction();
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("DELETE  FROM managerdashboardmmtmodel WHERE id IN(");
    final int _inputSize = userIds.length;
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
    int _argIndex = 1;
    for (int _item : userIds) {
      _stmt.bindLong(_argIndex, _item);
      _argIndex ++;
    }
    __db.beginTransaction();
    try {
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }
}
