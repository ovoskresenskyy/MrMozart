package com.example.mzrt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "deal")
public class Deal {
    //TODO: need to remake with JPA
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

    @Column("take_price_1")
    private double takePrice1;
    @Column("take_price_2")
    private double takePrice2;
    @Column("take_price_3")
    private double takePrice3;
    @Column("take_price_4")
    private double takePrice4;
    @Column("take_price_5")
    private double takePrice5;

    @Column("average_price")
    private double averagePrice;
    @Column("profit_price")
    private double profitPrice;

    @Column("closing_price")
    private double closingPrice;
    @Column("closing_alert")
    private String closingAlert;
    private boolean open;
    @Column("last_change_time")
    private LocalDateTime lastChangeTime;

}
