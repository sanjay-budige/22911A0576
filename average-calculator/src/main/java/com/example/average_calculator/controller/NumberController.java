package com.example.average_calculator.controller;

import com.example.average_calculator.model.Response;
import com.example.average_calculator.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/numbers")
public class NumberController {

    @Autowired
    private NumberService numberService;

    @GetMapping("/{numberId}")
    public Response getNumbers(@PathVariable String numberId) {
        return numberService.processNumbers(numberId);
    }
}
