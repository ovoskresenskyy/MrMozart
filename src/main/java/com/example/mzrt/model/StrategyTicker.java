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
@Table(name = "strategy_ticker")
public class StrategyTicker {

    @Id
    private int id;

    @Column("ticker_id")
    private int tickerId;
    @Column("name")
    private String name;
    @Column("strategy_id")
    private int strategyId;

    @Column("percent_tp_1")
    private double percentTP1;
    @Column("percent_tp_2")
    private double percentTP2;
    @Column("percent_tp_3")
    private double percentTP3;
    @Column("percent_tp_4")
    private double percentTP4;
    @Column("percent_tp_5")
    private double percentTP5;

    @Column("stop_when_used")
    private boolean stopWhenUsed;
}
