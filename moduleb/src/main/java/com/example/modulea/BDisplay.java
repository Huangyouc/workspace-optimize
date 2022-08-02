package com.example.modulea;

import com.example.processortest.Display;
import com.google.auto.service.AutoService;

@AutoService(Display.class)
public class BDisplay  implements Display {
    @Override
    public String display() {
        return "B Display";
    }
}
