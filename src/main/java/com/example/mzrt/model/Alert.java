package com.example.mzrt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(schema = "mzrt", name = "alert")
public class Alert {

    @Id
    private int id;
    private String name;
    private String webhook;
    private String secret;
    private String side;
}
