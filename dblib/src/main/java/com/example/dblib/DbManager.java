package com.example.dblib;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.example.dblib.entity.AppConfig;
import com.example.dblib.greendao.AppConfigDao;
import com.example.dblib.greendao.DaoMaster;
import com.example.dblib.greendao.DaoSession;

import org.greenrobot.greendao.database.Database;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DbManager {

    private static DbManager sSingleton;
    private final Context mContext;
    private DaoSession mDaoSession;
    private ExecutorService mThreadPool;
    private Handler mHandler;


    private DbManager(Context ctx){
        mContext = ctx.getApplicationContext();

        initDaoSession();

        initThreadPool();

        mHandler = new Handler(Looper.getMainLooper());
    }

    private void initThreadPool() {
        mThreadPool = Executors.newFixedThreadPool(1);
    }

    private void initDaoSession(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(mContext, DbConstants.DB_NAME);
        Database db = helper.getWritableDb();
        mDaoSession = new DaoMaster(db).newSession();
    }

    public static DbManager getDefault(Context context){
        if(sSingleton == null){
            synchronized (DbManager.class){
                sSingleton = new DbManager(context);
            }
        }
        return sSingleton;
    }

    public void querySingleConfig(final Handler subcribe, final DbQuerier<String, String> querier){

        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                AppConfigDao appConfigDao = mDaoSession.getAppConfigDao();
                List<AppConfig> configList = appConfigDao.queryBuilder()
                        .where(AppConfigDao.Properties.Key.eq(querier.getKey()))
                        .build()
                        .list();
                final String value;
                if(configList == null || configList.size() == 0){
                    value = querier.getDefValue();
                }else{
                    value = configList.get(0).getValue();
                }
                Handler h;
                if (subcribe != null) {
                    h = subcribe;
                }else{
                    h = mHandler;
                }
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        querier.onQuery(value);

                    }
                });
            }
        });


    }
}
