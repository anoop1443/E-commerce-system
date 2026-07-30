package com.example.homeadmin.ui.home.edit;

import android.content.Context;
import android.graphics.Color;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.homeadmin.R;

public class ColorSelectionAdapter extends BaseAdapter {

    private final Context context;
    private final String[] colorNames;
    private final String[] colorHexes;

    public ColorSelectionAdapter(Context context, String[] colorNames, String[] colorHexes) {
        this.context = context;
        this.colorNames = colorNames;
        this.colorHexes = colorHexes;
    }

    @Override
    public int getCount() {
        return colorNames.length;
    }

    @Override
    public Object getItem(int position) {
        return colorHexes[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.dialog_color_selection_item, parent, false);
        }

        View colorPreview = convertView.findViewById(R.id.color_preview_circle);
        TextView colorName = convertView.findViewById(R.id.color_name_text);

        colorName.setText(colorNames[position]);
        if ("CUSTOM".equals(colorHexes[position])) {
            colorPreview.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            colorName.setText(colorNames[position]); // "Custom Hex Code..."
        } else {
            try {
                colorPreview.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colorHexes[position])));
            } catch (Exception ignored) {
            }
        }

        return convertView;
    }
}