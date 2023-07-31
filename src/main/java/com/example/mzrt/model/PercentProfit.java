package com.example.mzrt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "percent_profit")
public class PercentProfit {

    @Id
    private int id;
    @Column("ticker_id")
    private int tickerId;
    @Column("strategy_id")
    private int strategyId;
    private int number;
    private double value;
}
