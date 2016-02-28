package com.ilenlab.ilentt.havetodo.data;

import android.provider.BaseColumns;

/**
 * Created by ADMIN on 2/27/2016.
 */
public class TaskContract {
    //each xxxEntry corresponds to a table in the database
    public class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_TASK = "task";
        public static final String COLUMN_PRIORITY = "priority";
    }
}
