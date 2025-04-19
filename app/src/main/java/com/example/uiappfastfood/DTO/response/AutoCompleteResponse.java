package com.example.uiappfastfood.DTO.response;

import java.util.List;

public class AutoCompleteResponse {
    private List<AutoComplete> predictions;

    public List<AutoComplete> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<AutoComplete> predictions) {
        this.predictions = predictions;
    }
}
