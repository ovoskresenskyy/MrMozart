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
@Table(name = "mzrt_order")
public class Order {

    @Id
    private int id;
    @Column("deal_id")
    private int dealId;
    private String name;
    private String secret;
    private String side;
    private String symbol;
    private String strategy;
    private String timestamp;
    @Column("timestamp_sent")
    private String timestampSent;
    private double price;
}
