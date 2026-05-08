package com.swapps.buylist.dto;

public class ChildItem {
    String name;
    boolean check;
    int value;
    String memo;

    public ChildItem() {}

    public ChildItem(String name, boolean check, int value, String memo) {
        this.name = name;
        this.check = check;
        this.value = value;
        this.memo = memo;
    }

    public String getName() {
        return name;
    }

    public boolean getCheck() {
        return check;
    }

    public int getValue() {
        return value;
    }

    public String getMemo() {
        return memo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

}
