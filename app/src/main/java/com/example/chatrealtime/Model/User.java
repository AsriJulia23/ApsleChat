package com.example.chatrealtime.Model;

public class User {

    private String uid;
    private String name;
    private String email;

    public User(){}

    public String getId() {
        return uid;
    }

    public void setId(String id) {
        this.uid = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString(){
        return "user{"+
                "uid='"+uid+'\''+
                ", name='"+ name +'\''+
                ", email='"+ email+'\''+
                '}';
    }
}
