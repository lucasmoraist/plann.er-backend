package com.lucasmoraist.planner.exception;

public class DatesInconsistency extends RuntimeException{

    public DatesInconsistency(String message){
        super(message);
    }

    public DatesInconsistency(){
        super("Invalid dates");
    }

}
