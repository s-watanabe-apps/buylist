package com.swapps.buylist.dto;

import java.util.Date;
import java.util.List;

public class ParentItem {
    private String name;
    private Date createdAt;
    private List<ChildItem> items;

    public ParentItem() {}

    public ParentItem(String name, Date createdAt, List<ChildItem> items) {
        this.name = name;
        this.createdAt = createdAt;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<ChildItem> getItems() {
        return items;
    }

    public void setItems(List<ChildItem> items) {
        this.items = items;
    }
}
