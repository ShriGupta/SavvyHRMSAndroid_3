package com.savvy.hrmsnewapp.room_database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
@Entity
public class PassengerModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "firstname")
    private String firstname;

    @ColumnInfo(name = "middlename")
    private String middlename;

    @ColumnInfo(name = "lastname")
    private String lastname;

    @ColumnInfo(name = "contact")
    private String contact;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "employeetype")
    private String employeetype;

    @ColumnInfo(name = "foodid")
    private String foodId;

    public void setFoodValue(String foodValue) {
        this.foodValue = foodValue;
    }

    public String getFoodValue() {
        return foodValue;
    }

    @ColumnInfo(name = "foodvalue")
    private String foodValue;


    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }



    public void setEmployeetype(String employeetype) {
        this.employeetype = employeetype;
    }

    public String getEmployeetype() {
        return employeetype;
    }




    public void setId(int id) {
        this.id = id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @ColumnInfo(name = "age")
    private int age;

    public int getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public String getLastname() {
        return lastname;
    }

    public String getContact() {
        return contact;
    }

    public String getAddress() {
        return address;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    @ColumnInfo(name = "gender")
    private String gender;

}
