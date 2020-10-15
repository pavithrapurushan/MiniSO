package in.co.tsmith.miniso;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;

public class ReportActivityAdapter extends ArrayAdapter<String> {

    public ReportActivityAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
}
