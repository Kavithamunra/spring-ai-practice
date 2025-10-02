package com.example.chatbot.tools;

import org.springframework.ai.tool.annotation.Tool;

public class InformationDesk {
    @Tool(description = "This tool will give you my name")
    String getMyName(){
        return "I am Kavitha";
    }

    @Tool(description = "This tool will give you my age")
    String getMyAge(){
        return "I am 30 years old ";
    }

    @Tool(description = "This tool will give you how I spend my time")
    String getMyHobbies(){
        return "I like sketching and reading books. I enjoy playing with young kids and teaching them about the world.";
    }

    @Tool(description = "This tool will give you information about railroad employees counts in a given month, year, state. Use rrb.gov website to get the information about railroad employee counts. This has information only up to 2023")
    Long getRailroadEmployeeCounts(Long employeeCount, String state, String month, String year){
        return employeeCount;
    }
}
