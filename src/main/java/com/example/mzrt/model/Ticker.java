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
@Table(name = "ticker")
public class Ticker {

    @Id
    private int id;
    private String name;
    @Column("user_id")
    private int userId; //TODO: must be the part of Primary key
}
