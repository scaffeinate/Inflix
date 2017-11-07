package dev.learn.movies.app.popular_movies.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import dev.learn.movies.app.popular_movies.R;

/**
 * DialogBuilderHelper - Contains helpers to build and show a dialog
 */
public final class DialogBuilderHelper {

    public static void build(Context context, String title, List<String> list, Dialog.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(true);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_dialog);
        for (String item : list) {
            adapter.add(item);
        }

        builder.setAdapter(adapter, onClickListener);
        builder.show();
    }
}
