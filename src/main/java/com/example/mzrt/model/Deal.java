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
@Table(name = "deal")
public class Deal {

    @Id
    private int id;
    @Column("user_id")
    private int userId;
    @Column("strategy_id")
    private int strategyId;
    private String strategy;
    private String ticker;
    private String side;
    @Column("first_price")
    private double firstPrice;
    @Column("second_price")
    private double secondPrice;
    @Column("third_price")
    private double thirdPrice;
    @Column("fourth_price")
    private double fourthPrice;
    @Column("fifth_price")
    private double fifthPrice;
    @Column("average_price")
    private double averagePrice;
    @Column("profit_price")
    private double profitPrice;
    @Column("closing_price")
    private double closingPrice;
    private boolean open;

}
