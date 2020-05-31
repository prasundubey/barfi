package com.timpra.barfi.Objects;

import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;

/**
 * Object of each card
 */
public class UserObject implements Serializable {
    private String  userId = "",
                    name = "",
                    profileImageUrl = "default",
                    age = "",
                    userSex = "",
                    interest = "",
                    phone = "", email = "";


    private String
            about = "",
            job = "",
            town = "",
            school = "",
            degree = "",
            city= "";

    private String
            status = "",
            religion = "",
            height = "",
            zodiac = "",
            drinking = "",
            smoking = "",
            exercise = "",
            pets = "",
            kids = "",
            diet = "",
            politics = "",
            reading = "";

    private String jobTitle, company;



    //image1
    private String imageUrl = "default";

    //image3
    private String imageUrl3 = "default";


    float searchDistance = 100;

    public UserObject(){}


    public void parseObject(DataSnapshot dataSnapshot){
        if(!dataSnapshot.exists()){return;}
        userId = dataSnapshot.getKey();

        if(dataSnapshot.child("name").getValue()!=null)
            name = dataSnapshot.child("name").getValue().toString();
        if(dataSnapshot.child("sex").getValue()!=null)
            userSex = dataSnapshot.child("sex").getValue().toString();
        if(dataSnapshot.child("age").getValue()!=null)
            age = dataSnapshot.child("age").getValue().toString();
        if(dataSnapshot.child("city").getValue()!=null)
            city = dataSnapshot.child("city").getValue().toString();




        if (dataSnapshot.child("profileImageUrl").getValue()!=null)
            profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
        //image1
        if (dataSnapshot.child("imageUrl").getValue()!=null)
            imageUrl = dataSnapshot.child("imageUrl").getValue().toString();
        //image3
        if (dataSnapshot.child("imageUrl3").getValue()!=null)
            imageUrl3 = dataSnapshot.child("imageUrl3").getValue().toString();


        if (dataSnapshot.child("phone").getValue()!=null)
            phone = dataSnapshot.child("phone").getValue().toString();
        if (dataSnapshot.child("email").getValue()!=null)
            email = dataSnapshot.child("email").getValue().toString();
        if(dataSnapshot.child("interest").getValue()!=null)
            interest = dataSnapshot.child("interest").getValue().toString();
        if(dataSnapshot.child("search_distance").getValue()!=null)
            searchDistance = Float.parseFloat(dataSnapshot.child("search_distance").getValue().toString());


        if(dataSnapshot.child("details").child("about").getValue()!=null)
            about = dataSnapshot.child("details").child("about").getValue().toString();

        if(dataSnapshot.child("details").child("job").getValue()!=null)
            job = dataSnapshot.child("details").child("job").getValue().toString();

        if(dataSnapshot.child("details").child("town").getValue()!=null)
            town = dataSnapshot.child("details").child("town").getValue().toString();

        if(dataSnapshot.child("details").child("school").getValue()!=null)
            school = dataSnapshot.child("details").child("school").getValue().toString();

        if(dataSnapshot.child("details").child("degree").getValue()!=null)
            degree = dataSnapshot.child("details").child("degree").getValue().toString();

//unused in zoomcard but for profile details page
        if(dataSnapshot.child("details").child("jobTitle").getValue()!=null)
            jobTitle = dataSnapshot.child("details").child("jobTitle").getValue().toString();
        if(dataSnapshot.child("details").child("company").getValue()!=null)
            company = dataSnapshot.child("details").child("company").getValue().toString();


       /* if(dataSnapshot.child("details").child("job").getValue()!=null)
            job = dataSnapshot.child("details").child("job").getValue().toString();*/

        if(dataSnapshot.child("details").child("status").getValue()!=null)
            status = dataSnapshot.child("details").child("status").getValue().toString();

        if(dataSnapshot.child("details").child("religion").getValue()!=null)
            religion = dataSnapshot.child("details").child("religion").getValue().toString();

        if(dataSnapshot.child("details").child("height").getValue()!=null)
            height = dataSnapshot.child("details").child("height").getValue().toString();

        if(dataSnapshot.child("details").child("zodiac").getValue()!=null)
            zodiac = dataSnapshot.child("details").child("zodiac").getValue().toString();

        if(dataSnapshot.child("details").child("drinking").getValue()!=null)
            drinking = dataSnapshot.child("details").child("drinking").getValue().toString();

        if(dataSnapshot.child("details").child("smoking").getValue()!=null)
            smoking = dataSnapshot.child("details").child("smoking").getValue().toString();

        if(dataSnapshot.child("details").child("exercise").getValue()!=null)
            exercise = dataSnapshot.child("details").child("exercise").getValue().toString();

        if(dataSnapshot.child("details").child("pets").getValue()!=null)
            pets = dataSnapshot.child("details").child("pets").getValue().toString();

        if(dataSnapshot.child("details").child("kids").getValue()!=null)
            kids = dataSnapshot.child("details").child("kids").getValue().toString();

        if(dataSnapshot.child("details").child("diet").getValue()!=null)
            diet = dataSnapshot.child("details").child("diet").getValue().toString();

        if(dataSnapshot.child("details").child("politics").getValue()!=null)
            politics = dataSnapshot.child("details").child("politics").getValue().toString();

        if(dataSnapshot.child("details").child("reading").getValue()!=null)
            reading = dataSnapshot.child("details").child("reading").getValue().toString();



    }

    public String getUserId(){
        return userId;
    }
    public String getName(){
        return name;
    }
    public String getAge(){
        return age;
    }

    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    //image1
    public String getImageUrl(){
        return imageUrl;
    }
    //image3
    public String getImageUrl3(){ return imageUrl3; }

    public float getSearchDistance() {
        return searchDistance;
    }

    public String getInterest() {
        return interest;
    }

    public String getUserSex() {
        return userSex;
    }

    public String getPhone() {
        return phone;
    }
    public String getEmail() {
        return email;
    }


    public String getAbout(){
        return about;
    }
    public String getJob(){
        return job;
    }
    public String getTown(){
        return town;
    }
    public String getSchool(){
        return school;
    }
    public String getDegree(){
        return degree;
    }
    public String getCity(){
        return city;
    }

    public String getJobTitle(){
        return jobTitle;
    }
    public String getCompany(){
        return company;
    }


    public String getStatus(){
        return status;
    }
    public String getReligion(){
        return religion;
    }
    public String getHeight(){
        return height;
    }
    public String getZodiac(){
        return zodiac;
    }
    public String getDrinking(){
        return drinking;
    }
    public String getSmoking(){
        return smoking;
    }
    public String getExercise(){
        return exercise;
    }
    public String getPets(){
        return pets;
    }
    public String getKids(){
        return kids;
    }
    public String getDiet(){
        return diet;
    }
    public String getPolitics(){
        return politics;
    }
    public String getReading(){
        return reading;
    }




}
