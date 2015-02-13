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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
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
    Hashtable<String,Integer> ids = new Hashtable<>();
    Integer ids_count = 10000;
    Timer timer;
    String server="http://tabacan.com/mayecode/projects/";

    MyTimerTask myTimerTask;


    method m;
    Boolean waiting=false;
    TextView textView = null;
    Button button = null;
    ImageView imageview = null;
    RadioButton radioButton = null;
    CheckBox checkBox = null;
    Switch aSwitch = null;
    ToggleButton toggleButton = null;
    ImageButton imageButton = null;
    ProgressBar progressBar = null;
    SeekBar seekBar = null;
    View generalView =  null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lienzo = (RelativeLayout) findViewById(R.id.lienzo);
        m = new method(this);
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
        findViewById(R.id.buttonLoadPreview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                server = server + ((EditText)findViewById(R.id.programId)).getText()+"/";
                findViewById(R.id.login).setVisibility(View.GONE);
                findViewById(R.id.lienzo).setVisibility(View.VISIBLE);
                timer = new Timer();
                myTimerTask = new MyTimerTask();
                timer.schedule(myTimerTask, 0, 2500);
            }
        });



    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {

            if(!waiting){buscar();}
        }

    }

    public void buscar() {
        aq.ajaxCancel();
        waiting=true;

        aq.ajax(server+"new.json", String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                Log.e("status",status.getMessage());
                if (!object.equals(last_change)) {
                    last_change = object;
                    aq.ajax(server+"data.json", JSONObject.class, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject object, AjaxStatus status) {
                            try {
                                Log.e("recive", object.toString());
                                lienzo.removeAllViews();
                                searchIds(object);
                                parse(0, object, lienzo);
                                waiting=false;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    waiting=false;
                }
            }
        });
    }


    private  void searchIds(JSONObject json){
        Iterator<String> iter = json.keys();
        String key;
        while (iter.hasNext()) {
            key = iter.next();
            try {
                Object value = json.get(key);
                if (key.equals("id")) {
                    ids_count++;
                    Log.e("--------------", "[" + value.toString() + "]:" + String.valueOf(ids_count));
                    ids.put(value.toString(), ids_count);

                }
                if (key.equals("children")) {
                    if (value instanceof JSONArray) {
                        for (int tt = 0; tt < json.getJSONArray("children").length(); tt++) {
                            try {
                                searchIds(json.getJSONArray("children").getJSONObject(tt));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            searchIds(json.getJSONObject("children"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e("Json Error:", e.getMessage());
            }
        }
    }


    private void parse(int type_father, JSONObject json, ViewGroup parent) {
        String key;
        View this_parent = null;
        int index_padre = 0;
        int type_class = -1;
        int view_id = 0;
        int layout_width = ViewGroup.LayoutParams.MATCH_PARENT;
        int layout_height = ViewGroup.LayoutParams.MATCH_PARENT;
        int margin_top = 0, margin_bottom = 0, margin_left = 0, margin_right = 0;
        int gravity = Gravity.NO_GRAVITY;
        ArrayList<Integer> layoutParams = new ArrayList<Integer>();

        Hashtable<Integer,Integer> layoutParams2 = new Hashtable<>();

        RelativeLayout.LayoutParams rlp;
        LinearLayout.LayoutParams llp;
        int text_style = 0;
        int style = -1;
        int this_id = -1;
        String tipo = null;
        try {
            tipo = json.getString("class");
        } catch (JSONException e) {
            e.printStackTrace();
        }




        Iterator<String> iter = json.keys();

        if (tipo.equals(type.XML_RELATIVE_LAYOUT)) {
            index_padre = cont_relativelayout;
            cont_relativelayout++;
            relativeLayout[index_padre] = new RelativeLayout(baseContecxt);
            this_parent = relativeLayout[index_padre];
            type_class = type.RELATIVE_LAYOUT;
        }
        if (tipo.equals(type.XML_LINEAR_LAYOUT)) {
            index_padre = cont_linearlayout;
            cont_linearlayout++;
            linearLayout[index_padre] = new LinearLayout(baseContecxt);
            this_parent = linearLayout[index_padre];
            type_class = type.LINEAR_LAYOUT;
        }

        if (tipo.equals(type.XML_TEXTVIEW)) {
            textView = new TextView(baseContecxt);
            this_parent = textView;
            type_class = type.TEXTVIEW;
        }
        if (tipo.equals(type.XML_BUTTON)) {
            button = new Button(baseContecxt);
            this_parent = button;
            type_class = type.BUTTON;
        }
        if (tipo.equals(type.XML_IMAGEVIEW)) {
            imageview = new ImageView(baseContecxt);
            this_parent = imageview;
            type_class = type.IMAGEVIEW;
        }
        if (tipo.equals(type.XML_RADIOBUTTON)) {
            radioButton = new RadioButton(baseContecxt);
            this_parent = radioButton;
            type_class = type.RADIOBUTTON;
        }
        if (tipo.equals(type.XML_CHECKBOX)) {
            checkBox = new CheckBox(baseContecxt);
            this_parent = checkBox;
            type_class = type.CHECKBOX;
        }
        if (tipo.equals(type.XML_SWITCH)) {
            aSwitch = new Switch(baseContecxt);
            this_parent = aSwitch;
            type_class = type.SWITCH;
        }

        if (tipo.equals(type.XML_TOGGLE_BUTTON)) {
            toggleButton = new ToggleButton(baseContecxt);
            this_parent = toggleButton;
            type_class = type.TOGGLE_BUTTON;
        }
        if (tipo.equals(type.XML_IMAGE_BUTTON)){
            imageButton = new ImageButton(baseContecxt);
            this_parent = imageButton;
            type_class=type.IMAGE_BUTTON;
        }
        if(tipo.equals(type.XML_PROGRESSBAR)){
            progressBar = new ProgressBar(baseContecxt);
            this_parent = progressBar;
            type_class = type.PROGRESSBAR;
        }
        if(tipo.equals(type.XML_SEEKBAR)){
            seekBar = new SeekBar(baseContecxt);
            this_parent = seekBar;
            type_class = type.SEEKBAR;
        }
        while (iter.hasNext()) {
            key = iter.next();
            try {
                Object value = json.get(key);
                if(key.equals("id")){
                    this_id = ids.get(value.toString());
                }
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
                        layout_width = m.dp(Integer.valueOf(value.toString().split("dp")[0]));
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
                        layout_height = m.dp(Integer.valueOf(value.toString().split("dp")[0]));
                    }

                }

                /* Colors */
                if (key.equals("background")) {
                    this_parent.setBackgroundColor(method.ParseColor(value.toString()));
                }
                if (key.equals("textColor")) {
                    textView.setTextColor(method.ParseColor(value.toString()));
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
                    Log.e("centrado", "vertical");
                    layoutParams.add(RelativeLayout.CENTER_VERTICAL);
                }
                if (key.equals("layout_centerHorizontal") && value.toString().toLowerCase().equals("true")) {
                    Log.e("centrado", "horizontal");
                    layoutParams.add(RelativeLayout.CENTER_HORIZONTAL);
                }

                /* Align */
                if (key.equals("layout_below")) {
                    layoutParams2.put(RelativeLayout.BELOW, ids.get(value.toString()));
                }
                if (key.equals("layout_above")) {
                    layoutParams2.put(RelativeLayout.ABOVE, ids.get(value.toString()));
                }
                if (key.equals("layout_alignTop")) {
                    layoutParams2.put(RelativeLayout.ALIGN_TOP, ids.get(value.toString()));
                }
                if (key.equals("layout_toRightOf")) {
                    layoutParams2.put(RelativeLayout.RIGHT_OF, ids.get(value.toString()));
                }
                if (key.equals("layout_toLeftOf")) {
                    layoutParams2.put(RelativeLayout.LEFT_OF, ids.get(value.toString()));
                }
                if (key.equals("layout_toEndOf")) {
                    layoutParams2.put(RelativeLayout.END_OF, ids.get(value.toString()));
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
                if (key.equals("orientation") && value.toString().toLowerCase().equals("vertical") && type_class == type.LINEAR_LAYOUT) {
                    linearLayout[index_padre].setOrientation(LinearLayout.VERTICAL);
                }
                if (key.equals("orientation") && value.toString().toLowerCase().equals("horizontal") && type_class == type.LINEAR_LAYOUT) {
                    linearLayout[index_padre].setOrientation(LinearLayout.HORIZONTAL);
                }

                /*Text*/
                if (key.equals("text")) {
                    switch (type_class) {
                        case type.TEXTVIEW:
                            textView.setText("" + value);
                            break;
                        case type.BUTTON:
                            button.setText("" + value);
                            break;
                        case type.RADIOBUTTON:
                            radioButton.setText(""+ value);
                            break;
                        case type.CHECKBOX:
                            checkBox.setText(""+value);
                            break;
                        case type.SWITCH:
                            aSwitch.setText("" + value);
                            break;
                        case type.TOGGLE_BUTTON:
                            toggleButton.setText("" + value);
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
                    aq.id(imageview).image(server+"img/"+value.toString().replace("@drawable/",""),false,false,0,R.drawable.place);
                }
                /* ProgressBar */
                if(key.equals("style") && type_class == type.PROGRESSBAR) {
                    if (value.equals("?android:attr/progressBarStyleLarge")){
                        //style = android.R.attr.progressBarStyleLarge;
                        progressBar =  new ProgressBar(baseContecxt, null,android.R.attr.progressBarStyleLarge);
                    }
                    if (value.equals("?android:attr/progressBarStyleHorizontal")){
                        //style = android.R.attr.progressBarStyleHorizontal;
                        progressBar =  new ProgressBar(baseContecxt, null,android.R.attr.progressBarStyleHorizontal);
                    }
                    if (value.equals("?android:attr/progressBarStyleSmall")){
                        //style = android.R.attr.progressBarStyleSmall;
                        progressBar =  new ProgressBar(baseContecxt, null,android.R.attr.progressBarStyleSmall);
                    }

                }

                //Log.e("key--->", key);
                if (key.equals("children")) {
                    switch (type_class) {
                        case type.RELATIVE_LAYOUT:
                            if (value instanceof JSONArray) {
                                for (int tt = 0; tt < json.getJSONArray("children").length(); tt++) {
                                    try {
                                        parse(type_class, json.getJSONArray("children").getJSONObject(tt), relativeLayout[index_padre]);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                try {
                                    parse(type_class, json.getJSONObject("children"), relativeLayout[index_padre]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case type.LINEAR_LAYOUT:
                            if (value instanceof JSONArray) {
                                for (int tt = 0; tt < json.getJSONArray("children").length(); tt++) {
                                    try {
                                        parse(type_class, json.getJSONArray("children").getJSONObject(tt), linearLayout[index_padre]);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                try {
                                    parse(type_class, json.getJSONObject(key), linearLayout[index_padre]);
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
            rlp.setMargins(m.dp(margin_left), m.dp(margin_top), m.dp(margin_right), m.dp(margin_bottom));
            llp.setMargins(m.dp(margin_left), m.dp(margin_top), m.dp(margin_right), m.dp(margin_bottom));
        //}

        for (int t : layoutParams) {
            Log.e("---- rela ----", String.valueOf(t));
            rlp.addRule(t);
        }

        for (int t : layoutParams2.keySet()) {
            Log.e("--- rela for----", String.valueOf(t)+"-"+String.valueOf(layoutParams2.get(t)));
            rlp.addRule(t, layoutParams2.get(t));
        }

        generalView = null;
        switch (type_class) {
            case type.RELATIVE_LAYOUT:
                relativeLayout[index_padre].setLayoutParams(llp);
                relativeLayout[index_padre].setGravity(gravity);
                parent.addView(relativeLayout[index_padre]);
                Log.e("added", "relative");
                break;
            case type.LINEAR_LAYOUT:
                linearLayout[index_padre].setLayoutParams(llp);
                linearLayout[index_padre].setGravity(gravity);
                parent.addView(linearLayout[index_padre]);
                Log.e("added", "linear");
                break;

            case type.TEXTVIEW:
                if (text_style != 0) {
                    textView.setTextAppearance(this, text_style);
                }
                textView.setGravity(gravity);
                generalView = textView;
                Log.e("added", "text");
                break;
            case type.BUTTON:
                button.setGravity(gravity);
                generalView = button;
                Log.e("added", "button");
                break;
            case type.IMAGEVIEW:
                generalView = imageview;
                Log.e("added", "ImageView");
                break;
            case type.RADIOBUTTON:
                generalView = radioButton;
                Log.e("added", "RadioButton");
                break;
            case type.CHECKBOX:
                generalView = checkBox;
                Log.e("added", "CheckBox");
                break;
            case type.SWITCH:
                generalView = aSwitch;
                Log.e("added", "Swich");
                break;
            case type.TOGGLE_BUTTON:
                generalView = toggleButton;
                Log.e("added", "Swich");
                break;
            case type.IMAGE_BUTTON:
                generalView = imageButton;
                Log.e("added","ImageButton");
                break;
            case type.PROGRESSBAR:

                generalView = progressBar;
                Log.e("added","ProgressBar");
                break;
            case type.SEEKBAR:
                generalView = seekBar;
                Log.e("added","SeekBar");
                break;
        }

        if(generalView!=null){
            generalView.setLayoutParams(rlp);
            if (this_id!=-1) generalView.setId(this_id);
            if (type_father ==  type.RELATIVE_LAYOUT) {
                parent.addView(generalView);
            }
            if (type_father == type.LINEAR_LAYOUT) {
                llp.gravity = gravity;
                parent.addView(generalView, llp);
            }
        }



    }

}
