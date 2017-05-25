package com.example.leoguti.riskassessment.modelo;

import android.content.Context;
import android.database.SQLException;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leoguti.riskassessment.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by leoguti on 09/05/2017.
 */

public class CustomAdapter extends ArrayAdapter<Alerta> implements View.OnClickListener{

    private final static String TAG = CustomAdapter.class.getSimpleName();
    private ArrayList<Alerta> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtFechaRegistro;
        TextView txtDescripcion;
        TextView txtNivelAlerta;
        ImageView info;
    }

    public CustomAdapter(ArrayList<Alerta> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Alerta dataModel=(Alerta)object;

        switch (v.getId())
        {
            case R.id.item_info:


                eliminarAlerta(v, dataModel);
                dataSet.remove(position);
                Toast toast = Toast.makeText(v.getContext(), R.string.message_delete_record, Toast.LENGTH_SHORT);
                toast.show();
              //  Snackbar.make(v, "Se elimina la alerta  " +desc, Snackbar.LENGTH_LONG)
              //               .setAction("No action", null).show();
                this.notifyDataSetChanged();
                break;
        }
    }

    private void eliminarAlerta(View v, Alerta dataModel) {
        EventDataSource datasource = new EventDataSource(v.getContext());
        try {
            datasource.open();
            datasource.deleteAlert(dataModel);
        }catch (SQLException e){
            Log.e(TAG, "-- errorOpenOrDelete: "+ e.getMessage());
        }finally {
            datasource.close();
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Alerta dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtFechaRegistro = (TextView) convertView.findViewById(R.id.fecha);
            viewHolder.txtDescripcion = (TextView) convertView.findViewById(R.id.descripcion);
            viewHolder.txtNivelAlerta = (TextView) convertView.findViewById(R.id.nivel_alerta);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

       /* Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);*/
        lastPosition = position;

        ///DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

        viewHolder.txtFechaRegistro.setText(dateFormat.format(dataModel.getFechaRegistro()));
        viewHolder.txtDescripcion.setText(dataModel.getDescripcion());
        viewHolder.txtNivelAlerta.setText(dataModel.getNivelAlerta().name());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
