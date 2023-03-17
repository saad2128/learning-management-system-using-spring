package com.example.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name="test_holidays")
public class Holiday extends BaseEntity{
    @Id
    private String holiday;

    private String reason;

    @Enumerated(EnumType.STRING)
    private Type type;


    public enum Type {
        FESTIVAL, FEDERAL
    }

}
