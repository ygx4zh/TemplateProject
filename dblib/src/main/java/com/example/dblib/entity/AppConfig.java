package com.example.dblib.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class AppConfig {

    @Id
    private String key;
    private String value;

    private String extra;

    @Generated(hash = 946556175)
    public AppConfig(String key, String value, String extra) {
        this.key = key;
        this.value = value;
        this.extra = extra;
    }

    @Generated(hash = 136961441)
    public AppConfig() {
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getExtra() {
        return this.extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
