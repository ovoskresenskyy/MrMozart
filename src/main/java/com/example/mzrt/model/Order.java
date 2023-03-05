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
    private String name;
    private String secret;
    private String side;
    private String symbol;
    @Column("user_id")
    private int userId;
}
