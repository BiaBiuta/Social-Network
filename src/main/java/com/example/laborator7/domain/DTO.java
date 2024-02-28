package com.example.laborator7.domain;


import java.time.LocalDate;

public record DTO(
     Long id,
     User from,
   User to,
   String message,
  LocalDate date){
}
