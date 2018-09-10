package com.example.dblib;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.example.dblib.entity.AppConfig;
import com.example.dblib.greendao.AppConfigDao;
import com.example.dblib.greendao.DaoMaster;
import com.example.dblib.greendao.DaoSession;

import org.greenrobot.greendao.database.Database;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class DbManager {
    private static final String TAG = "DbManager";
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
        mThreadPool = Executors.newFixedThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, TAG);
            }
        });
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

    @WorkerThread
    public String queryConfig(String key, String defValue){
        AppConfigDao appConfigDao = mDaoSession.getAppConfigDao();
        List<AppConfig> configList = appConfigDao.queryBuilder()
                .where(AppConfigDao.Properties.Key.eq(key))
                .build()
                .list();
        final String value;
        if(configList == null || configList.size() == 0){
            value = defValue;
        }else{
            value = configList.get(0).getValue();
        }
        return value;
    }

    @WorkerThread
    public String[] queryConfigs(String[] keys, String[] defValues){
        AppConfigDao appConfigDao = mDaoSession.getAppConfigDao();
        final String[] values = new String[keys.length];

        for (int i = 0; i < keys.length; i++) {
            List<AppConfig> configList = appConfigDao.queryBuilder()
                    .where(AppConfigDao.Properties.Key.eq(keys[i]))
                    .build()
                    .list();
            final String value;
            if(configList == null || configList.size() == 0){
                if(defValues == null || i >= defValues.length)
                    value = null;
                else
                    value = defValues[i];
            }else{
                value = configList.get(0).getValue();
            }
            values[i] = value;
        }
        return values;
    }

    public void queryConfig(final Handler subcriber, final Querier<String, String> querier){

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
                if (subcriber != null) {
                    h = subcriber;
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

    public void queryConfigs(final Handler subcriber, final Querier<String[], String[]> querier){

        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                AppConfigDao appConfigDao = mDaoSession.getAppConfigDao();
                String[] keys = querier.getKey();
                String[] defValues = querier.getDefValue();
                final String[] values = new String[keys.length];

                for (int i = 0; i < keys.length; i++) {
                    List<AppConfig> configList = appConfigDao.queryBuilder()
                            .where(AppConfigDao.Properties.Key.eq(keys[i]))
                            .build()
                            .list();
                    final String value;
                    if(configList == null || configList.size() == 0){
                        if(defValues == null || i >= defValues.length)
                            value = null;
                        else
                            value = defValues[i];
                    }else{
                        value = configList.get(0).getValue();
                    }
                    values[i] = value;
                }

                Handler h;
                if (subcriber != null) {
                    h = subcriber;
                }else{
                    h = mHandler;
                }
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        querier.onQuery(values);

                    }
                });
            }
        });
    }
}
