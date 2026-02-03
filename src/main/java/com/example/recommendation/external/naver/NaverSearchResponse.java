package com.example.recommendation.external.naver;

import java.util.List;

public class NaverSearchResponse {

    private List<NaverItem> items;

    public List<NaverItem> getItems() {
        return items;
    }

    public void setItems(List<NaverItem> items) {
        this.items = items;
    }
}

