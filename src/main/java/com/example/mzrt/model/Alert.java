package com.example.mzrt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "mzrt_alert")
public class Alert {

    @Id
    private int id;
    private String name;
    private int number;
    private String webhook;
    private String secret;
    private String side;
    @Column("user_id")
    private int userId;
    @Column("strategy_id")
    private int strategyId;
    private boolean opening;
    private int pause;
}
