package com.example.dblib;

public abstract class DbQuerier<Key, Value> {

    private Key key;
    private Value defValue;

    public Key getKey() {
        return key;
    }

    public Value getDefValue() {
        return defValue;
    }

    public DbQuerier(Key key){
        this.key = key;
    }

    public DbQuerier(Key key, Value defValue){
        this.key = key;
        this.defValue = defValue;
    }

    protected abstract void onQuery(Value value);
}
