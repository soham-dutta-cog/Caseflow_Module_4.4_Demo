package com.demo;

import java.util.List;

public class stageTemplate {

    public static List<StageDefinition> getDefaultStage(){
        return List.of(
        new StageDefinition(1, "Clerk", 3), // case apply
        new StageDefinition(2, "Clerk", 5), // doc verifixation
        new StageDefinition(3, "Clerk", 5), // hearing scheduling
        new StageDefinition(4, "Judge", 10) // judgement
        );
    }
}
