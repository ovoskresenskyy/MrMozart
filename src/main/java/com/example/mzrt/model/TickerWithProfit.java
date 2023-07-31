package com.example.mzrt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TickerWithProfit {

    private Ticker ticker;
    private PercentProfit percent1;
    private PercentProfit percent2;
    private PercentProfit percent3;
    private PercentProfit percent4;
    private PercentProfit percent5;
}
