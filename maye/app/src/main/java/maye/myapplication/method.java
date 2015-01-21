package maye.myapplication;

import android.content.Context;
import android.graphics.Color;


/**
 * Created by SBTV-RCORTES on 21-01-2015.
 */
public class method {
    Context ctx;
    private float density;
    public method(Context ctx){
        this.ctx = ctx;
        this.density = ctx.getResources().getDisplayMetrics().density;
    }
    public static int ParseColor(String value) {
        if(value.length()==4) {
            String v=value.replaceAll("#","");
            v=v.substring(0,1)+v.substring(0,1)+v.substring(1,2)+v.substring(1,2)+v.substring(2,3)+v.substring(2,3);
            return Color.parseColor(String.format("#%06X", (0xFFFFFF & Integer.parseInt(v, 16))));
        }
        return Color.parseColor(value);
    }
    public int dp(int dpValue) {
        return (int) ((dpValue * this.density) + 0.5f);
    }
}
