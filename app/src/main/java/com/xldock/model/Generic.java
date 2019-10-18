package com.xldock.model;

/**
 * Created by Honey Shah on 11-01-2018.
 */

public class Generic<T> {
    private T t;

    public T get(){
        return this.t;
    }

    public void set(T t1){
        this.t=t1;
    }
}
