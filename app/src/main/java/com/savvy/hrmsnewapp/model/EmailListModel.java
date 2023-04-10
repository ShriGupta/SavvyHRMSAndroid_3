package com.savvy.hrmsnewapp.model;

/**
 * Created by Hari Om on 8/12/2016.
 */
public class EmailListModel {
    public String getMailsender_id() {
        return mailsender_id;
    }

    public void setMailsender_id(String mailsender_id) {
        this.mailsender_id = mailsender_id;
    }

    public String getMailsender_name() {
        return mailsender_name;
    }

    public void setMailsender_name(String mailsender_name) {
        this.mailsender_name = mailsender_name;
    }

    public String getMailsender_subject() {
        return mailsender_subject;
    }

    public void setMailsender_subject(String mailsender_subject) {
        this.mailsender_subject = mailsender_subject;
    }

    public String getMailsender_image_url() {
        return mailsender_image_url;
    }

    public void setMailsender_image_url(String mailsender_image_url) {
        this.mailsender_image_url = mailsender_image_url;
    }

    public String getMailsender_message() {
        return mailsender_message;
    }

    public void setMailsender_message(String mailsender_message) {
        this.mailsender_message = mailsender_message;
    }

    public String getMail_sending_date() {
        return mail_sending_date;
    }

    public void setMail_sending_date(String mail_sending_date) {
        this.mail_sending_date = mail_sending_date;
    }

    public String mailsender_id;
    public String mailsender_name;
    public String mailsender_subject;
    public String mailsender_image_url;
    public String mailsender_message;
    public String mail_sending_date;

}
