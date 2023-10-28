package com.pg.owner.custom_exception;

public class ResourceExistException extends Exception{
    public ResourceExistException(){
        super("Resource already Exist!!");
    }

    public ResourceExistException(String msg){
        super(msg);
    }
}
