package com.github.fdkvandr.springwebfluxexample.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
@Table("anime")
public class Anime {

    @Id
    private Integer id;
    @NotNull
    @NotEmpty(message = "The name of this anime can not be empty")
    private String name;
}
