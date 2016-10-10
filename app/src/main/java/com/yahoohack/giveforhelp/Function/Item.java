package com.yahoohack.giveforhelp.Function;

public class Item {
    private String userName, userContact, postType, itemName, itemDescription;

    public Item() {
    }

    public Item(String userName, String userContact, String postType, String itemName, String itemDescription) {
        this.userName = userName;
        this.userContact = userContact;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.postType = postType;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public String getUserContact() {
        return userContact;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
