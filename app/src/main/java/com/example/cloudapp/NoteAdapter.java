package com.example.cloudapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends ArrayAdapter<Note> {

    private LayoutInflater inflater;
    private int layout;
    private List<Note> notes;
    private List<Note> constNotes;
    private ItemFilter mFilter = new ItemFilter();
    private ImportanceFilter iFilter = new ImportanceFilter();


    public NoteAdapter(Context context, int resource, List<Note> notes) {
        super(context, resource, notes);
        this.notes = notes;
        this.constNotes = notes;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);

    }


    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        ImageView picView = view.findViewById(R.id.pic);
        TextView nameView = view.findViewById(R.id.name);
        TextView dateView = view.findViewById(R.id.date);
        ImageView importanceView = view.findViewById(R.id.importance);


        Note note = notes.get(position);
        if(note.getUri()!=null) {

            final Intent innt = new Intent(Intent.ACTION_VIEW);
            innt.setData(note.getUri());
            innt.setType(note.getMime());

            final List<ResolveInfo> matches = getContext().getPackageManager().queryIntentActivities(innt, 0);

            for (ResolveInfo match : matches) {
                final Drawable icon = match.loadIcon(getContext().getPackageManager());
                final CharSequence label = match.loadLabel(getContext().getPackageManager());
                picView.setImageDrawable(icon);
            }
        }
        else {
            picView.setImageURI(Uri.parse("content://media/external/images/media/" + note.getPicResource()));
        }
        nameView.setText(note.getName());

        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(note.getDate());
        dateView.setText(date);
        switch (note.getImportance()){
            case Low: importanceView.setImageResource(R.drawable.l); break;
            case Medium: importanceView.setImageResource(R.drawable.m); break;
            case High: importanceView.setImageResource(R.drawable.h); break;
        }

        return view;
    }
    public int getCount() {

        return notes.size();
    }


    public long getItemId(int position) {
        return position;
    }

    public Note getItem(int position) {
        return notes.get(position);
    }

    public Filter getFilter() {
        return mFilter;
    }

    public Filter getIFilter() {
        return iFilter;
    }

    static class ViewHolder {
        TextView text;
    }

    public int getConstNotePosition(int pos){
        int constPos = constNotes.indexOf(notes.get(pos));
        return constPos;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Note> list = constNotes;

            int count = list.size();
            final ArrayList<Note> nlist = new ArrayList<Note>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getName();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(constNotes.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }



        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notes = (ArrayList<Note>) results.values;
            notifyDataSetChanged();
        }

    }

    public void notifyListChanged(){
        notes = constNotes;
        notifyDataSetChanged();

    }

    private class ImportanceFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            Importance imp = Importance.valueOf(String.valueOf(constraint));
            FilterResults results = new FilterResults();

            final List<Note> list = constNotes;

            int count = list.size();
            final ArrayList<Note> nlist = new ArrayList<Note>(count);

            Importance filterableImportance ;

            for (int i = 0; i < count; i++) {
                filterableImportance = list.get(i).getImportance();
                if (filterableImportance.equals(imp)) {
                    nlist.add(constNotes.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notes = (ArrayList<Note>) results.values;
            notifyDataSetChanged();
        }

    }


}

