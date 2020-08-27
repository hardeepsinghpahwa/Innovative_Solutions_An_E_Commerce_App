package com.example.itshop.Notifications;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {NotiDetails.class},exportSchema = false,version = 1)
public abstract class NotiDatabase extends RoomDatabase {

    private static NotiDatabase instance;
    public abstract NotiDao notiDao();


    public static synchronized NotiDatabase getDatabase(Context context){

        if(instance==null)
        {
           instance= Room.databaseBuilder(context.getApplicationContext(),NotiDatabase.class,"notidatabase")
                   .fallbackToDestructiveMigration()
                   .build();
        }

        return instance;
    }

}


