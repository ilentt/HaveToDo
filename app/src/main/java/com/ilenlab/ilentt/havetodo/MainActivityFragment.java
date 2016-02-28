package com.ilenlab.ilentt.havetodo;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.ilenlab.ilentt.havetodo.data.TaskAdapter;
import com.ilenlab.ilentt.havetodo.data.TaskContract;
import com.ilenlab.ilentt.havetodo.data.TaskDBHelper;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    TaskAdapter mTaskAdapter;

    public final String ADD_TITLE = "Add a new task";
    public final String ADD_MESSAGE = "Enter task name and priority";
    public final String TASK_HINT = "Task name";
    public final String PRIORITY_HINT = "Priority";

    // These indices care tied to TASK_COLUMN. If TASK_COLUMN changes these must change
    public static final int COL_TASK_ID = 0;
    public static final int COL_TASK_NAME = 1;
    public static final int COL_TASK_PRIORITY = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.tasks_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_task:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(ADD_TITLE);
                builder.setMessage(ADD_MESSAGE);

                final EditText nameTask = new EditText(getActivity());
                nameTask.setHint(TASK_HINT);
                builder.setView(nameTask);

                final EditText priorityTask = new EditText(getActivity());
                priorityTask.setHint(PRIORITY_HINT);
                builder.setView(priorityTask);

                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(nameTask);
                layout.addView(priorityTask);

                builder.setView(layout);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // get user input
                        String task = nameTask.getText().toString();
                        String priority = priorityTask.getText().toString();

                        // get DBHelper to write database
                        TaskDBHelper helper = new TaskDBHelper(getActivity());
                        SQLiteDatabase db = helper.getWritableDatabase();

                        // put in the value with ContentValue
                        ContentValues values = new ContentValues();
                        values.clear();
                        values.put(TaskContract.TaskEntry.COLUMN_TASK, task);
                        values.put(TaskContract.TaskEntry.COLUMN_PRIORITY, priority);

                        // insert the value into the table for task
                        db.insertWithOnConflict(
                                TaskContract.TaskEntry.TABLE_NAME,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_IGNORE
                        );

                        // query database again to get update data
                        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE_NAME,
                                new String[]{TaskContract.TaskEntry._ID,
                                TaskContract.TaskEntry.COLUMN_TASK,
                                TaskContract.TaskEntry.COLUMN_PRIORITY},
                                null, null, null, null, null);

                        // swap old data with new data for display
                        mTaskAdapter.swapCursor(cursor);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // usual inflating of the fragment layout file
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // find the listView
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_tasks);

        // get DBHelper to read from database
        TaskDBHelper helper = new TaskDBHelper(getActivity());
        SQLiteDatabase sqlDB = helper.getReadableDatabase();

        // query database to get any existing data
        Cursor cursor = sqlDB.query(TaskContract.TaskEntry.TABLE_NAME,
                new String[]{TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_TASK,
                TaskContract.TaskEntry.COLUMN_PRIORITY},
                null, null, null, null, null);

        // create a new TaskAdapter and bind it to ListView
        mTaskAdapter = new TaskAdapter(getActivity(),cursor);
        listView.setAdapter(mTaskAdapter);
        return rootView;
    }
}
