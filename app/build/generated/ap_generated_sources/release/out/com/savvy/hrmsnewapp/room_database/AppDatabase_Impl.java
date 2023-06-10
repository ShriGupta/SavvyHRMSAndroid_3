package com.savvy.hrmsnewapp.room_database;

import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile PassengerDao _passengerDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `PassengerModel` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `firstname` TEXT, `middlename` TEXT, `lastname` TEXT, `contact` TEXT, `address` TEXT, `employeetype` TEXT, `foodid` TEXT, `foodvalue` TEXT, `age` INTEGER NOT NULL, `gender` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `IteneraryModel` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `source` TEXT, `destination` TEXT, `departuredate` TEXT, `returndate` TEXT, `mode` TEXT, `classcode` TEXT, `starttime` TEXT, `endtime` TEXT, `flightdetail` TEXT, `travelwaytype` TEXT, `sourceid` TEXT, `destinationid` TEXT, `modeid` TEXT, `classid` TEXT, `seatprefid` TEXT, `seatprefvalue` TEXT, `insurancevalue` TEXT, `frequentlyfillerno` TEXT, `specialrequest` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `AdvanceModel` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `amount` TEXT, `currency` TEXT, `paymode` TEXT, `remarks` TEXT, `paymodeid` TEXT, `currencyid` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `CarDetailsModel` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `pickupdate` TEXT, `pickupat` TEXT, `dropat` TEXT, `pickuptime` TEXT, `releasetime` TEXT, `comment` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `AccommodationModel` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `ciy` TEXT, `fromdate` TEXT, `todate` TEXT, `checkintime` TEXT, `checkouttime` TEXT, `hotellocation` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `ManagerDashboardMMTModel` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `EMPLOYEE_CODE` TEXT, `EMPLOYEE_NAME` TEXT, `AVG_WORKTIME` TEXT, `AVG_IN_TIME` TEXT, `LEAVE` TEXT, `WFH` TEXT, `OD` TEXT, `AVG_WORKED1` TEXT, `AVG_OUT_TIME` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `TrackMeModel` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `trackMeDetails` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `OfflineCredentialModel` (`uid` INTEGER NOT NULL, `username` TEXT, `password` TEXT, PRIMARY KEY(`uid`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `OfflinePunchInModel` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `user_name` TEXT, `latitude` TEXT, `longitude` TEXT, `currentdate` TEXT, `currenttime` TEXT, `comment` TEXT, `location` TEXT)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'd1f4972962214f020c7a64c872a1f643')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `PassengerModel`");
        _db.execSQL("DROP TABLE IF EXISTS `IteneraryModel`");
        _db.execSQL("DROP TABLE IF EXISTS `AdvanceModel`");
        _db.execSQL("DROP TABLE IF EXISTS `CarDetailsModel`");
        _db.execSQL("DROP TABLE IF EXISTS `AccommodationModel`");
        _db.execSQL("DROP TABLE IF EXISTS `ManagerDashboardMMTModel`");
        _db.execSQL("DROP TABLE IF EXISTS `TrackMeModel`");
        _db.execSQL("DROP TABLE IF EXISTS `OfflineCredentialModel`");
        _db.execSQL("DROP TABLE IF EXISTS `OfflinePunchInModel`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      protected RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsPassengerModel = new HashMap<String, TableInfo.Column>(11);
        _columnsPassengerModel.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengerModel.put("firstname", new TableInfo.Column("firstname", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengerModel.put("middlename", new TableInfo.Column("middlename", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengerModel.put("lastname", new TableInfo.Column("lastname", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengerModel.put("contact", new TableInfo.Column("contact", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengerModel.put("address", new TableInfo.Column("address", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengerModel.put("employeetype", new TableInfo.Column("employeetype", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengerModel.put("foodid", new TableInfo.Column("foodid", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengerModel.put("foodvalue", new TableInfo.Column("foodvalue", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengerModel.put("age", new TableInfo.Column("age", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengerModel.put("gender", new TableInfo.Column("gender", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPassengerModel = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPassengerModel = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPassengerModel = new TableInfo("PassengerModel", _columnsPassengerModel, _foreignKeysPassengerModel, _indicesPassengerModel);
        final TableInfo _existingPassengerModel = TableInfo.read(_db, "PassengerModel");
        if (! _infoPassengerModel.equals(_existingPassengerModel)) {
          return new RoomOpenHelper.ValidationResult(false, "PassengerModel(com.savvy.hrmsnewapp.room_database.PassengerModel).\n"
                  + " Expected:\n" + _infoPassengerModel + "\n"
                  + " Found:\n" + _existingPassengerModel);
        }
        final HashMap<String, TableInfo.Column> _columnsIteneraryModel = new HashMap<String, TableInfo.Column>(20);
        _columnsIteneraryModel.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("source", new TableInfo.Column("source", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("destination", new TableInfo.Column("destination", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("departuredate", new TableInfo.Column("departuredate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("returndate", new TableInfo.Column("returndate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("mode", new TableInfo.Column("mode", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("classcode", new TableInfo.Column("classcode", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("starttime", new TableInfo.Column("starttime", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("endtime", new TableInfo.Column("endtime", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("flightdetail", new TableInfo.Column("flightdetail", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("travelwaytype", new TableInfo.Column("travelwaytype", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("sourceid", new TableInfo.Column("sourceid", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("destinationid", new TableInfo.Column("destinationid", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("modeid", new TableInfo.Column("modeid", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("classid", new TableInfo.Column("classid", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("seatprefid", new TableInfo.Column("seatprefid", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("seatprefvalue", new TableInfo.Column("seatprefvalue", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("insurancevalue", new TableInfo.Column("insurancevalue", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("frequentlyfillerno", new TableInfo.Column("frequentlyfillerno", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIteneraryModel.put("specialrequest", new TableInfo.Column("specialrequest", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIteneraryModel = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesIteneraryModel = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoIteneraryModel = new TableInfo("IteneraryModel", _columnsIteneraryModel, _foreignKeysIteneraryModel, _indicesIteneraryModel);
        final TableInfo _existingIteneraryModel = TableInfo.read(_db, "IteneraryModel");
        if (! _infoIteneraryModel.equals(_existingIteneraryModel)) {
          return new RoomOpenHelper.ValidationResult(false, "IteneraryModel(com.savvy.hrmsnewapp.room_database.IteneraryModel).\n"
                  + " Expected:\n" + _infoIteneraryModel + "\n"
                  + " Found:\n" + _existingIteneraryModel);
        }
        final HashMap<String, TableInfo.Column> _columnsAdvanceModel = new HashMap<String, TableInfo.Column>(7);
        _columnsAdvanceModel.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAdvanceModel.put("amount", new TableInfo.Column("amount", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAdvanceModel.put("currency", new TableInfo.Column("currency", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAdvanceModel.put("paymode", new TableInfo.Column("paymode", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAdvanceModel.put("remarks", new TableInfo.Column("remarks", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAdvanceModel.put("paymodeid", new TableInfo.Column("paymodeid", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAdvanceModel.put("currencyid", new TableInfo.Column("currencyid", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAdvanceModel = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAdvanceModel = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAdvanceModel = new TableInfo("AdvanceModel", _columnsAdvanceModel, _foreignKeysAdvanceModel, _indicesAdvanceModel);
        final TableInfo _existingAdvanceModel = TableInfo.read(_db, "AdvanceModel");
        if (! _infoAdvanceModel.equals(_existingAdvanceModel)) {
          return new RoomOpenHelper.ValidationResult(false, "AdvanceModel(com.savvy.hrmsnewapp.room_database.AdvanceModel).\n"
                  + " Expected:\n" + _infoAdvanceModel + "\n"
                  + " Found:\n" + _existingAdvanceModel);
        }
        final HashMap<String, TableInfo.Column> _columnsCarDetailsModel = new HashMap<String, TableInfo.Column>(7);
        _columnsCarDetailsModel.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarDetailsModel.put("pickupdate", new TableInfo.Column("pickupdate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarDetailsModel.put("pickupat", new TableInfo.Column("pickupat", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarDetailsModel.put("dropat", new TableInfo.Column("dropat", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarDetailsModel.put("pickuptime", new TableInfo.Column("pickuptime", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarDetailsModel.put("releasetime", new TableInfo.Column("releasetime", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCarDetailsModel.put("comment", new TableInfo.Column("comment", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCarDetailsModel = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCarDetailsModel = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCarDetailsModel = new TableInfo("CarDetailsModel", _columnsCarDetailsModel, _foreignKeysCarDetailsModel, _indicesCarDetailsModel);
        final TableInfo _existingCarDetailsModel = TableInfo.read(_db, "CarDetailsModel");
        if (! _infoCarDetailsModel.equals(_existingCarDetailsModel)) {
          return new RoomOpenHelper.ValidationResult(false, "CarDetailsModel(com.savvy.hrmsnewapp.room_database.CarDetailsModel).\n"
                  + " Expected:\n" + _infoCarDetailsModel + "\n"
                  + " Found:\n" + _existingCarDetailsModel);
        }
        final HashMap<String, TableInfo.Column> _columnsAccommodationModel = new HashMap<String, TableInfo.Column>(7);
        _columnsAccommodationModel.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccommodationModel.put("ciy", new TableInfo.Column("ciy", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccommodationModel.put("fromdate", new TableInfo.Column("fromdate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccommodationModel.put("todate", new TableInfo.Column("todate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccommodationModel.put("checkintime", new TableInfo.Column("checkintime", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccommodationModel.put("checkouttime", new TableInfo.Column("checkouttime", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccommodationModel.put("hotellocation", new TableInfo.Column("hotellocation", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAccommodationModel = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAccommodationModel = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAccommodationModel = new TableInfo("AccommodationModel", _columnsAccommodationModel, _foreignKeysAccommodationModel, _indicesAccommodationModel);
        final TableInfo _existingAccommodationModel = TableInfo.read(_db, "AccommodationModel");
        if (! _infoAccommodationModel.equals(_existingAccommodationModel)) {
          return new RoomOpenHelper.ValidationResult(false, "AccommodationModel(com.savvy.hrmsnewapp.room_database.AccommodationModel).\n"
                  + " Expected:\n" + _infoAccommodationModel + "\n"
                  + " Found:\n" + _existingAccommodationModel);
        }
        final HashMap<String, TableInfo.Column> _columnsManagerDashboardMMTModel = new HashMap<String, TableInfo.Column>(10);
        _columnsManagerDashboardMMTModel.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManagerDashboardMMTModel.put("EMPLOYEE_CODE", new TableInfo.Column("EMPLOYEE_CODE", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManagerDashboardMMTModel.put("EMPLOYEE_NAME", new TableInfo.Column("EMPLOYEE_NAME", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManagerDashboardMMTModel.put("AVG_WORKTIME", new TableInfo.Column("AVG_WORKTIME", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManagerDashboardMMTModel.put("AVG_IN_TIME", new TableInfo.Column("AVG_IN_TIME", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManagerDashboardMMTModel.put("LEAVE", new TableInfo.Column("LEAVE", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManagerDashboardMMTModel.put("WFH", new TableInfo.Column("WFH", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManagerDashboardMMTModel.put("OD", new TableInfo.Column("OD", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManagerDashboardMMTModel.put("AVG_WORKED1", new TableInfo.Column("AVG_WORKED1", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsManagerDashboardMMTModel.put("AVG_OUT_TIME", new TableInfo.Column("AVG_OUT_TIME", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysManagerDashboardMMTModel = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesManagerDashboardMMTModel = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoManagerDashboardMMTModel = new TableInfo("ManagerDashboardMMTModel", _columnsManagerDashboardMMTModel, _foreignKeysManagerDashboardMMTModel, _indicesManagerDashboardMMTModel);
        final TableInfo _existingManagerDashboardMMTModel = TableInfo.read(_db, "ManagerDashboardMMTModel");
        if (! _infoManagerDashboardMMTModel.equals(_existingManagerDashboardMMTModel)) {
          return new RoomOpenHelper.ValidationResult(false, "ManagerDashboardMMTModel(com.savvy.hrmsnewapp.room_database.ManagerDashboardMMTModel).\n"
                  + " Expected:\n" + _infoManagerDashboardMMTModel + "\n"
                  + " Found:\n" + _existingManagerDashboardMMTModel);
        }
        final HashMap<String, TableInfo.Column> _columnsTrackMeModel = new HashMap<String, TableInfo.Column>(2);
        _columnsTrackMeModel.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTrackMeModel.put("trackMeDetails", new TableInfo.Column("trackMeDetails", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTrackMeModel = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTrackMeModel = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTrackMeModel = new TableInfo("TrackMeModel", _columnsTrackMeModel, _foreignKeysTrackMeModel, _indicesTrackMeModel);
        final TableInfo _existingTrackMeModel = TableInfo.read(_db, "TrackMeModel");
        if (! _infoTrackMeModel.equals(_existingTrackMeModel)) {
          return new RoomOpenHelper.ValidationResult(false, "TrackMeModel(com.savvy.hrmsnewapp.room_database.TrackMeModel).\n"
                  + " Expected:\n" + _infoTrackMeModel + "\n"
                  + " Found:\n" + _existingTrackMeModel);
        }
        final HashMap<String, TableInfo.Column> _columnsOfflineCredentialModel = new HashMap<String, TableInfo.Column>(3);
        _columnsOfflineCredentialModel.put("uid", new TableInfo.Column("uid", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineCredentialModel.put("username", new TableInfo.Column("username", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineCredentialModel.put("password", new TableInfo.Column("password", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysOfflineCredentialModel = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesOfflineCredentialModel = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoOfflineCredentialModel = new TableInfo("OfflineCredentialModel", _columnsOfflineCredentialModel, _foreignKeysOfflineCredentialModel, _indicesOfflineCredentialModel);
        final TableInfo _existingOfflineCredentialModel = TableInfo.read(_db, "OfflineCredentialModel");
        if (! _infoOfflineCredentialModel.equals(_existingOfflineCredentialModel)) {
          return new RoomOpenHelper.ValidationResult(false, "OfflineCredentialModel(com.savvy.hrmsnewapp.room_database.OfflineCredentialModel).\n"
                  + " Expected:\n" + _infoOfflineCredentialModel + "\n"
                  + " Found:\n" + _existingOfflineCredentialModel);
        }
        final HashMap<String, TableInfo.Column> _columnsOfflinePunchInModel = new HashMap<String, TableInfo.Column>(8);
        _columnsOfflinePunchInModel.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflinePunchInModel.put("user_name", new TableInfo.Column("user_name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflinePunchInModel.put("latitude", new TableInfo.Column("latitude", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflinePunchInModel.put("longitude", new TableInfo.Column("longitude", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflinePunchInModel.put("currentdate", new TableInfo.Column("currentdate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflinePunchInModel.put("currenttime", new TableInfo.Column("currenttime", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflinePunchInModel.put("comment", new TableInfo.Column("comment", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflinePunchInModel.put("location", new TableInfo.Column("location", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysOfflinePunchInModel = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesOfflinePunchInModel = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoOfflinePunchInModel = new TableInfo("OfflinePunchInModel", _columnsOfflinePunchInModel, _foreignKeysOfflinePunchInModel, _indicesOfflinePunchInModel);
        final TableInfo _existingOfflinePunchInModel = TableInfo.read(_db, "OfflinePunchInModel");
        if (! _infoOfflinePunchInModel.equals(_existingOfflinePunchInModel)) {
          return new RoomOpenHelper.ValidationResult(false, "OfflinePunchInModel(com.savvy.hrmsnewapp.room_database.OfflinePunchInModel).\n"
                  + " Expected:\n" + _infoOfflinePunchInModel + "\n"
                  + " Found:\n" + _existingOfflinePunchInModel);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "d1f4972962214f020c7a64c872a1f643", "2989c018e41bd889872b9f1ba886424a");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "PassengerModel","IteneraryModel","AdvanceModel","CarDetailsModel","AccommodationModel","ManagerDashboardMMTModel","TrackMeModel","OfflineCredentialModel","OfflinePunchInModel");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `PassengerModel`");
      _db.execSQL("DELETE FROM `IteneraryModel`");
      _db.execSQL("DELETE FROM `AdvanceModel`");
      _db.execSQL("DELETE FROM `CarDetailsModel`");
      _db.execSQL("DELETE FROM `AccommodationModel`");
      _db.execSQL("DELETE FROM `ManagerDashboardMMTModel`");
      _db.execSQL("DELETE FROM `TrackMeModel`");
      _db.execSQL("DELETE FROM `OfflineCredentialModel`");
      _db.execSQL("DELETE FROM `OfflinePunchInModel`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  public PassengerDao passengerDao() {
    if (_passengerDao != null) {
      return _passengerDao;
    } else {
      synchronized(this) {
        if(_passengerDao == null) {
          _passengerDao = new PassengerDao_Impl(this);
        }
        return _passengerDao;
      }
    }
  }
}
