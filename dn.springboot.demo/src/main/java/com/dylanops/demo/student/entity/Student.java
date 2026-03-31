package com.dylanops.demo.student.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Integer id;

    @NotNull
    @Size(min = 5, max = 30, message = "Invalid Name: Must be of 5 - 30 characters")
    @JsonProperty("name")
    private String name;

    @NotNull
    @JsonProperty("height")
    private Float height;

}
