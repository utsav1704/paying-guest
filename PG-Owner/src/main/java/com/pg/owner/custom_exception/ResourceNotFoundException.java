package com.pg.owner.custom_exception;

public class ResourceNotFoundException extends Exception{
    public ResourceNotFoundException(){
        super("Not able to find whatever you are looking for!!");
    }

    public ResourceNotFoundException(String msg){
        super(msg);
    }
}
