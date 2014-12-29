package maye.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {
    RelativeLayout lienzo;
    Context baseContecxt;
    AQuery aq;
    RelativeLayout relativeLayout[];
    LinearLayout linearLayout[];
    ViewGroup view[];
    int cont_relativelayout = 0;
    String last_change = "";
    int cont_linearlayout = 0;
    String[] support_Views = {"RelativeLayout", "LinearLayout", "TextView", "Button", "ImageView"};
    Timer timer;
    String server="http://192.168.1.175/maye/";
    MyTimerTask myTimerTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lienzo = (RelativeLayout) findViewById(R.id.lienzo);
        aq = new AQuery(this);
        baseContecxt = getBaseContext();
        int max_layouts = 200;
        relativeLayout = new RelativeLayout[max_layouts];
        linearLayout = new LinearLayout[max_layouts];
        view = new ViewGroup[max_layouts * 2];


        if(timer != null){
            timer.cancel();
        }

        //re-schedule timer here
        //otherwise, IllegalStateException of
        //"TimerTask is scheduled already"
        //will be thrown
        timer = new Timer();
        myTimerTask = new MyTimerTask();


            timer.schedule(myTimerTask, 0, 2500);


    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            buscar();
        }

    }

    public void buscar() {
        aq.ajaxCancel();
        aq.ajax(server+"new.json", String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                if (!object.equals(last_change)) {
                    last_change = object;
                    aq.ajax(server+"data.json", JSONObject.class, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject object, AjaxStatus status) {

                            try {
                                Log.e("recive", object.toString());
                                lienzo.removeAllViews();
                                parse(0, object, lienzo);
                                //String fir=object.keys().next();
                                //Log.e("Primaera llave--->",fir);
                                //check_2(fir, object, lienzo);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });


    }

    public int dp(int dpValue) {
        float d = getResources().getDisplayMetrics().density;
        return (int) ((dpValue * d) + 0.5f);

    }

    public void check_2(String tipo, JSONObject json, ViewGroup parent) {
        parse(0, json, parent);
    }

    private void parse(int type_father, JSONObject json, ViewGroup parent) {
        String key;
        View this_parent = null;
        int index_padre = 0;
        int tipo_class = 0;
        int layout_width = ViewGroup.LayoutParams.MATCH_PARENT;
        int layout_height = ViewGroup.LayoutParams.WRAP_CONTENT;
        int margin_top = 0, margin_bottom = 0, margin_left = 0, margin_right = 0;
        int gravity = Gravity.NO_GRAVITY;
        ArrayList<Integer> layoutParams = new ArrayList<Integer>();
        RelativeLayout.LayoutParams rlp;
        LinearLayout.LayoutParams llp;
        int text_style = 0;
        String tipo = null;
        try {
            tipo = json.getString("class");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView textView = null;
        Button button = null;
        ImageView imageview = null;

        Iterator<String> iter = json.keys();
        if (tipo.equals("RelativeLayout")) {
            Log.e("View----->", "RelativeLayout");
            index_padre = cont_relativelayout;
            cont_relativelayout++;
            relativeLayout[index_padre] = new RelativeLayout(baseContecxt);
            this_parent = relativeLayout[index_padre];
            tipo_class = 0;
            //break;
        }
        if (tipo.equals("LinearLayout")) {
            Log.e("View----->", "LinearLayout");
            index_padre = cont_linearlayout;
            cont_linearlayout++;
            linearLayout[index_padre] = new LinearLayout(baseContecxt);
            this_parent = linearLayout[index_padre];

            tipo_class = 1;

        }


        if (tipo.equals("TextView")) {
            Log.e("View----->", "Textview");
            textView = new TextView(baseContecxt);
            this_parent = textView;
            tipo_class = 2;
        }
        if (tipo.equals("Button")) {
            Log.e("View----->", "Button");
            button = new Button(baseContecxt);
            this_parent = button;
            tipo_class = 3;
        }
        if (tipo.equals("ImageView")) {
            Log.e("View----->", "ImageView");
            imageview = new ImageView(baseContecxt);
            this_parent = imageview;
            tipo_class = 4;
        }

        while (iter.hasNext()) {
            key = iter.next();
            try {
                Object value = json.get(key);
                Log.e("----------------------------", key + " <-> " + value.toString());
                /* Layour w,h*/
                if (key.equals("layout_width")) {
                    if (value.equals("fill_parent")) {
                        layout_width = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                    if (value.equals("match_parent")) {
                        layout_width = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                    if (value.equals("wrap_content")) {
                        layout_width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                    if (value.toString().contains("dp")) {
                        layout_width = dp(Integer.valueOf(value.toString().split("dp")[0]));
                    }

                }
                if (key.equals("layout_height")) {
                    if (value.equals("fill_parent")) {
                        layout_height = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                    if (value.equals("match_parent")) {
                        layout_height = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                    if (value.equals("wrap_content")) {
                        layout_height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                    if (value.toString().contains("dp")) {
                        layout_height = dp(Integer.valueOf(value.toString().split("dp")[0]));
                    }

                }

                /* Colors */
                if (key.equals("background")) {
                    this_parent.setBackgroundColor(ParseColor(value.toString()));
                }
                if (key.equals("textColor")) {
                    textView.setTextColor(ParseColor(value.toString()));
                }

                /* Margins */
                if (key.equals("layout_marginTop")) {
                    margin_top = Integer.valueOf(value.toString().split("dp")[0]);
                }
                if (key.equals("layout_marginBottom")) {
                    margin_bottom = Integer.valueOf(value.toString().split("dp")[0]);
                }
                if (key.equals("layout_marginLeft")) {
                    margin_left = Integer.valueOf(value.toString().split("dp")[0]);
                }
                if (key.equals("layout_marginRight")) {
                    margin_right = Integer.valueOf(value.toString().split("dp")[0]);
                }
                if (key.equals("layout_margin")) {
                    margin_top = Integer.valueOf(value.toString().split("dp")[0]);
                    margin_bottom = Integer.valueOf(value.toString().split("dp")[0]);
                    margin_left = Integer.valueOf(value.toString().split("dp")[0]);
                    margin_right = Integer.valueOf(value.toString().split("dp")[0]);
                }

                /* Extra params*/
                if (key.equals("layout_alignParentRight") && value.toString().toLowerCase().equals("true")) {
                    layoutParams.add(RelativeLayout.ALIGN_PARENT_RIGHT);
                }
                if (key.equals("layout_alignParentLeft") && value.toString().toLowerCase().equals("true")) {
                    layoutParams.add(RelativeLayout.ALIGN_PARENT_LEFT);
                }
                if (key.equals("layout_alignParentTop") && value.toString().toLowerCase().equals("true")) {
                    layoutParams.add(RelativeLayout.ALIGN_PARENT_TOP);
                }
                if (key.equals("layout_alignParentBottom") && value.toString().toLowerCase().equals("true")) {
                    layoutParams.add(RelativeLayout.ALIGN_PARENT_BOTTOM);
                }
                if (key.equals("layout_centerVertical") && value.toString().toLowerCase().equals("true")) {
                    layoutParams.add(RelativeLayout.CENTER_VERTICAL);
                }
                if (key.equals("layout_centerHorizontal") && value.toString().toLowerCase().equals("true")) {
                    layoutParams.add(RelativeLayout.CENTER_HORIZONTAL);
                }
                if (key.equals("layout_gravity") && value.toString().toLowerCase().equals("center_horizontal")) {
                    gravity = Gravity.CENTER_HORIZONTAL;
                }
                if (key.equals("layout_gravity") && value.toString().toLowerCase().equals("center_vertical")) {
                    gravity = Gravity.CENTER_VERTICAL;
                }
                if (key.equals("layout_gravity") && value.toString().toLowerCase().equals("right")) {
                    gravity = Gravity.END;
                }
                if (key.equals("layout_gravity") && value.toString().toLowerCase().equals("left")) {
                    gravity = Gravity.START;
                }
                if (key.equals("layout_gravity") && value.toString().toLowerCase().equals("bottom")) {
                    gravity = Gravity.BOTTOM;
                }
                if (key.equals("layout_gravity") && value.toString().toLowerCase().equals("top")) {
                    gravity = Gravity.TOP;
                }
                if (key.equals("orientation") && value.toString().toLowerCase().equals("vertical") && tipo_class == 1) {
                    linearLayout[index_padre].setOrientation(LinearLayout.VERTICAL);
                }

                /*Text*/
                if (key.equals("text")) {
                    switch (tipo_class) {
                        case 2:
                            textView.setText("" + value);
                            break;
                        case 3:
                            button.setText("" + value);
                            break;
                    }
                }
                if (key.equals("textAppearance")) {
                    if (value.equals("?attr/textAppearanceMedium")) {
                        text_style = android.R.style.TextAppearance_Medium;
                    }
                    if (value.equals("?attr/textAppearanceSmall")) {
                        text_style = android.R.style.TextAppearance_Small;
                    }
                    if (value.equals("?attr/textAppearanceLarge")) {
                        text_style = android.R.style.TextAppearance_Large;
                    }
                }

                /* Images*/
                if (key.equals("src")){
                    Log.e("----src----",server+"img/+"+value.toString().replace("@drawable/",""));
                    aq.id(imageview).image(server+"img/"+value.toString().replace("@drawable/",""),false,false,0,R.drawable.place);
                }

                //Log.e("key--->", key);
                if (key.equals("children")) {


                    switch (tipo_class) {
                        case 0:
                            if (value instanceof JSONArray) {
                                for (int tt = 0; tt < json.getJSONArray("children").length(); tt++) {

                                    try {
                                        parse(tipo_class, json.getJSONArray("children").getJSONObject(tt), relativeLayout[index_padre]);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            } else {
                                try {
                                    parse(tipo_class, json.getJSONObject("children"), relativeLayout[index_padre]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case 1:
                            if (value instanceof JSONArray) {
                                for (int tt = 0; tt < json.getJSONArray("children").length(); tt++) {
                                    try {
                                        parse(tipo_class, json.getJSONArray("children").getJSONObject(tt), linearLayout[index_padre]);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {

                                try {
                                    parse(tipo_class, json.getJSONObject(key), linearLayout[index_padre]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                    }

                }
            } catch (JSONException e) {
                Log.e("Json Error:", e.getMessage());
            }
        }
        /********************* Seteo t odo lo relacionado con layouts, margenesm etc ****************************/
        llp = new LinearLayout.LayoutParams(layout_width, layout_height);

        rlp = new RelativeLayout.LayoutParams(layout_width, layout_height);
       // if (margin_bottom != 0 || margin_left != 0 || margin_right != 0 || margin_top != 0) {
            rlp.setMargins(dp(margin_left), dp(margin_top), dp(margin_right), dp(margin_bottom));
            llp.setMargins(dp(margin_left), dp(margin_top), dp(margin_right), dp(margin_bottom));
        //}

        for (int t : layoutParams) {
            rlp.addRule(t);
        }

        switch (tipo_class) {
            case 0:
                relativeLayout[index_padre].setLayoutParams(llp);
                relativeLayout[index_padre].setGravity(gravity);
                parent.addView(relativeLayout[index_padre]);
                Log.e("added", "relative");
                break;
            case 1:
                linearLayout[index_padre].setLayoutParams(llp);
                linearLayout[index_padre].setGravity(gravity);
                parent.addView(linearLayout[index_padre]);
                Log.e("added", "linear");
                break;
            case 2:
                if (text_style != 0) {
                    textView.setTextAppearance(this, text_style);
                }
                textView.setGravity(gravity);
                if (type_father == 0) {
                    parent.addView(textView, rlp);
                }
                if (type_father == 1) {
                    llp.gravity = gravity;
                    parent.addView(textView, llp);
                }
                Log.e("added", "text");
                break;
            case 3:
                button.setGravity(gravity);
                if (type_father == 0) {
                    parent.addView(button, rlp);
                }
                if (type_father == 1) {
                    llp.gravity = gravity;
                    parent.addView(button, llp);
                }
                Log.e("added", "button");
                break;
            case 4:
                //imageview.setGravity(gravity);
                if (type_father == 0) {
                    parent.addView(imageview, rlp);
                }
                if (type_father == 1) {
                    llp.gravity = gravity;
                    parent.addView(imageview, llp);
                }
                Log.e("added", "ImageView");
                break;
        }


    }
    public int ParseColor(String value) {


        if(value.length()==4) {
            String v=value.replaceAll("#","");
            v=v.substring(0,1)+v.substring(0,1)+v.substring(1,2)+v.substring(1,2)+v.substring(2,3)+v.substring(2,3);
            Log.e("---------color ----------",v);
            return Color.parseColor(String.format("#%06X", (0xFFFFFF & Integer.parseInt(v, 16))));
        }
        return Color.parseColor(value);
    }
}
