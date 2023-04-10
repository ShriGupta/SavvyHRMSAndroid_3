package com.savvy.hrmsnewapp.model;

/**
 * Created by Amulya on 8/13/2016.
 */
public class SemResultModel {

    public String subject;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getObtained_marks() {
        return obtained_marks;
    }

    public void setObtained_marks(String obtained_marks) {
        this.obtained_marks = obtained_marks;
    }

    public String getMaximum_marks() {
        return maximum_marks;
    }

    public void setMaximum_marks(String maximum_marks) {
        this.maximum_marks = maximum_marks;
    }

    public String getSubject_stutas() {
        return subject_stutas;
    }

    public void setSubject_stutas(String subject_stutas) {
        this.subject_stutas = subject_stutas;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String obtained_marks;
    public String maximum_marks;
    public String subject_stutas;
    public String grade;

}
