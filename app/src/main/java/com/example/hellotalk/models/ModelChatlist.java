package com.example.hellotalk.models;

public class ModelChatlist {
    String id, name;

    public ModelChatlist(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public ModelChatlist() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
