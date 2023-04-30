package com.example.mzrt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TickerWithCurrentPrice {

    private Ticker ticker;
    private double futuresPrice;
    private double spotPrice;
}
