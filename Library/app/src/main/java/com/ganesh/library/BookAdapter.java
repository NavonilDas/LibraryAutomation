package com.ganesh.library;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BookAdapter extends ArrayAdapter<Book> {
    private Context ctx;
    int resource;

    public BookAdapter(@NonNull Context context, int resource, @NonNull Book[] objects) {
        super(context, resource, objects);
        this.ctx = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            LayoutInflater vi = LayoutInflater.from(this.ctx);
            view = vi.inflate(resource,null);
        }
        Book b = getItem(position);

        if( b!= null){
            TextView index = view.findViewById(R.id.index);
            TextView bookTitle = view.findViewById(R.id.book_title);
            TextView issue = view.findViewById(R.id.issue_date);

            index.setText(""+(position+1));
            bookTitle.setText(b.bookname);
            issue.setText(b.issuedate);
        }


        return view;
    }
}
