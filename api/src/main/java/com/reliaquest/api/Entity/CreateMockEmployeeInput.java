package com.reliaquest.api.Entity;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateMockEmployeeInput {

    @NotBlank(message = "Name must not be blank.")
    private String name;

    @NotNull(message = "Salary must not be null.")
    @Min(value = 1, message = "Salary must be > zero.")
    private Integer salary;

    @NotNull(message = "Age must not be null.")
    @Min(value = 16)
    @Max(value = 75)
    private Integer age;

    @NotBlank(message = "Title must not be blank.")
    private String title;

    @Override
    public String toString() {
        return "CreateMockEmployeeInput{" +
                "name='" + name + '\'' +
                ", salary=" + salary +
                ", age=" + age +
                ", title='" + title + '\'' +
                '}';
    }
}
