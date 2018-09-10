package com.example.dblib;

public abstract class Querier<Key, Value> {

    private Key key;
    private Value defValue;

    public Key getKey() {
        return key;
    }

    public Value getDefValue() {
        return defValue;
    }

    public Querier(Key key){
        this.key = key;
    }

    public Querier(Key key, Value defValue){
        this.key = key;
        this.defValue = defValue;
    }

    protected abstract void onQuery(Value value);
}
