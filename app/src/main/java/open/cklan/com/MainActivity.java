package open.cklan.com;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import open.cklan.com.widget.GeneralEditText;

public class MainActivity extends AppCompatActivity implements GeneralEditText.TextChangedListener, CompoundButton.OnCheckedChangeListener {
    GeneralEditText editText;
    TextView textView;
    CheckBox cbClear;
    CheckBox cbFormat;
    CheckBox cbPasswordVisible;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText= (GeneralEditText) findViewById(R.id.edit_text);
        editText.setTextChangedListener(this);
        textView= (TextView) findViewById(R.id.text_view);
        cbClear= (CheckBox) findViewById(R.id.cb_clear);
        cbClear.setOnCheckedChangeListener(this);
        cbFormat= (CheckBox) findViewById(R.id.cb_format);
        cbFormat.setOnCheckedChangeListener(this);
        cbPasswordVisible= (CheckBox) findViewById(R.id.cb_password_visible);
        cbPasswordVisible.setOnCheckedChangeListener(this);
        editText.setShowSwitchPasswordVisible(true);
    }

    @Override
    public void onTextChanged(CharSequence str) {
        textView.setText(str);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.cb_clear:
                editText.setShowClearView(isChecked);
                break;
            case R.id.cb_format:
                editText.setFormatText(isChecked);
                if(isChecked){
                    editText.setCondition(new GeneralEditText.Condition() {
                        @Override
                        public boolean getCondition(int i) {
                            return i==2|| i==8;
                        }
                    });
                }else{
                    editText.setCondition(null);
                }
                break;
            case R.id.cb_password_visible:
                editText.setShowSwitchPasswordVisible(isChecked);
                break;
        }
    }
}

