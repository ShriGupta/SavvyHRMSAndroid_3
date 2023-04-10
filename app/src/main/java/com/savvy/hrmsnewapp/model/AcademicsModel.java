package com.savvy.hrmsnewapp.model;

import java.util.ArrayList;

/**
 * Created by Amulya on 8/13/2016.
 */
public class AcademicsModel {

    public ArrayList<SemesterListModel> getSem_list() {
        return sem_list;
    }

    public void setSem_list(ArrayList<SemesterListModel> sem_list) {
        this.sem_list = sem_list;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<SemResultModel> getResult_list() {
        return result_list;
    }

    public void setResult_list(ArrayList<SemResultModel> result_list) {
        this.result_list = result_list;
    }

    public String getTotal_of_obtainedmarks() {
        return total_of_obtainedmarks;
    }

    public void setTotal_of_obtainedmarks(String total_of_obtainedmarks) {
        this.total_of_obtainedmarks = total_of_obtainedmarks;
    }

    public String getTotal_of_maximummarks() {
        return total_of_maximummarks;
    }

    public void setTotal_of_maximummarks(String total_of_maximummarks) {
        this.total_of_maximummarks = total_of_maximummarks;
    }

    public String getFinal_grade() {
        return final_grade;
    }

    public void setFinal_grade(String final_grade) {
        this.final_grade = final_grade;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public ArrayList<TopicModel> getTopics_list() {
        return topics_list;
    }

    public void setTopics_list(ArrayList<TopicModel> topics_list) {
        this.topics_list = topics_list;
    }

    public ArrayList<SemesterListModel> sem_list;
    public ArrayList<SemResultModel> result_list;
    public ArrayList<TopicModel> topics_list;
    public String total_of_obtainedmarks;
    public String total_of_maximummarks;
    public String final_grade;
    public String percentage;
    public String status;
    public String code;
    public String message;

}
