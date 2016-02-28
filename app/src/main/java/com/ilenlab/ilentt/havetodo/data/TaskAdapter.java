package com.ilenlab.ilentt.havetodo.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.ilenlab.ilentt.havetodo.MainActivityFragment;
import com.ilenlab.ilentt.havetodo.R;

/**
 * Created by ADMIN on 2/27/2016.
 */
public class TaskAdapter extends CursorAdapter {

    private static Context context;
    TaskDBHelper helper;

    public TaskAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.context = context;
        helper = new TaskDBHelper(context);
    }

    // the newView method is used to inflate a new view and return it
    // don't need bind any data to the view at this point
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    // the bindView method is used to bind all data to a given view
    // such as setting the text on a textView
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // find Views to populate in inflate template
        TextView taskName = (TextView) view.findViewById(R.id.list_item_task_name);
        TextView taskPriority = (TextView) view.findViewById(R.id.list_item_task_priority);
        Button taskDone = (Button) view.findViewById(R.id.list_item_done_button);

        // extract properties from cursor
        final String id = cursor.getString(MainActivityFragment.COL_TASK_ID);
        final String task = cursor.getString(MainActivityFragment.COL_TASK_NAME);
        final String priority = cursor.getString(MainActivityFragment.COL_TASK_PRIORITY);

        // populate with extracted properties
        taskName.setText(task);
        taskPriority.setText(priority);
        taskDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create sql command for deleting a particular ID
                String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                        TaskContract.TaskEntry.TABLE_NAME,
                        TaskContract.TaskEntry._ID,
                        id);

                SQLiteDatabase sqlDB = helper.getWritableDatabase();
                // execute the delete command
                sqlDB.execSQL(sql);
                notifyDataSetChanged();

                // query database for update data
                Cursor cursor = sqlDB.query(TaskContract.TaskEntry.TABLE_NAME,
                        new String[]{TaskContract.TaskEntry._ID,
                                TaskContract.TaskEntry.COLUMN_TASK,
                                TaskContract.TaskEntry.COLUMN_PRIORITY},
                        null, null, null, null, null);

                // instance method with TaskAdapter so no need to use adapter.swapCursor()
                swapCursor(cursor);
            }
        });


    }
}
