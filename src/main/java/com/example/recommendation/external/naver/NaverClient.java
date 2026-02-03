package com.example.recommendation.external.naver;

import java.util.List;

public interface NaverClient {

    List<Product> search(String keyword);

}
