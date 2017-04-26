package open.cklan.com.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * AUTHOR：lanchuanke on 17/4/12 12:03
 * 包含隐藏显示密码，一键清除输入，根据需求格式化文本，比如格式化手机号：1xx xxxx xxxx
 */
public class GeneralEditText extends LinearLayout implements View.OnClickListener, View.OnFocusChangeListener, View.OnLayoutChangeListener {
    private ImageButton clearInput;//清空按钮
    private ImageButton switchPasswordVisible;//切换密码可见按钮
    private EditText editText;//输入文本框
    private TextChangedListener textChangedListener;//输入文字监听
    private int mInputTypeBackup;
    private boolean passwordVisible;//是否需要显示 切换密码可见
    private boolean showSwitchPasswordVisible;
    private boolean showClearView;//是否需要显示清空按钮
    private int maxLength;//最大长度
    //format number
    public static final String SEPARATOR_TEXT=" ";//默认的分隔符
    private String separatorText=SEPARATOR_TEXT;//分隔符
    private boolean formatText;//文本是否需要格式化
    private Condition condition;//格式化文本的条件，比如第几个字符串后加分隔符

    public GeneralEditText(Context context) {
        this(context,null);
    }

    public GeneralEditText(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GeneralEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_general_edittext,this);
        initEditText(context, attrs, defStyleAttr);
        clearInput= (ImageButton) findViewById(R.id.clearInput);
        switchPasswordVisible = (ImageButton) findViewById(R.id.passwordVisible);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GeneralEditText);
        showClearView=typedArray.getBoolean(R.styleable.GeneralEditText_showClearView,false);
        invalidateClearInputView();
        showSwitchPasswordVisible=typedArray.getBoolean(R.styleable.GeneralEditText_switchPasswordVisible,false);
        invalidateSwitchPasswordVisibleView();
        formatText =typedArray.getBoolean(R.styleable.GeneralEditText_formatText,false);
        addTextChangedListener();
        maxLength=typedArray.getInteger(R.styleable.GeneralEditText_maxLength,Integer.MAX_VALUE);
        switchPasswordVisible.setOnClickListener(this);
        clearInput.setOnClickListener(this);
        this.addOnLayoutChangeListener(this);
        typedArray.recycle();
    }

    private void addTextChangedListener() {
        editText.addTextChangedListener(formatText?new SeparatorTextWatcher():new NormalTextWatcher());
    }

    private void invalidateSwitchPasswordVisibleView() {
        switchPasswordVisible.setVisibility(supportShowHidePassword() ?VISIBLE:GONE);

    }

    private void invalidateClearInputView() {
        clearInput.setVisibility(showClearView && editText.length()>0?VISIBLE:GONE);
    }

    private void initEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        editText=new EditText(context,attrs,defStyleAttr);
        editText.setBackgroundDrawable(null);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setOnFocusChangeListener(this);
        editText.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams layoutParams=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight=1;
        layoutParams.gravity= Gravity.CENTER_VERTICAL;
        addView(editText,0,layoutParams);
    }

    private boolean supportShowHidePassword(){
        int inputType=editText.getInputType();
        mInputTypeBackup=inputType;
        passwordVisible = ((inputType & InputType.TYPE_TEXT_VARIATION_PASSWORD) == InputType
                .TYPE_TEXT_VARIATION_PASSWORD ||
                (inputType & InputType.TYPE_NUMBER_VARIATION_PASSWORD) == InputType
                        .TYPE_NUMBER_VARIATION_PASSWORD || (inputType & InputType
                .TYPE_TEXT_VARIATION_WEB_PASSWORD) == InputType
                .TYPE_TEXT_VARIATION_WEB_PASSWORD )&& showSwitchPasswordVisible;
        return passwordVisible;
    }


    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public void setTextChangedListener(TextChangedListener textChangedListener) {
        this.textChangedListener = textChangedListener;
    }

    public void setEnable(boolean enable){
        int childCount=getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view=getChildAt(i);
            if(view!=null)view.setEnabled(enable);
        }
    }

    public void setMaxLength(int maxLength){
        this.maxLength=maxLength;
    }

    public void setSeparatorText(String separatorText) {
        this.separatorText = separatorText;
    }

    public void setFormatText(boolean formatText) {
        this.formatText = formatText;
        addTextChangedListener();
    }

    public void setShowSwitchPasswordVisible(boolean showSwitchPasswordVisible) {
        this.showSwitchPasswordVisible = showSwitchPasswordVisible;
        invalidateSwitchPasswordVisibleView();
    }

    public void setShowClearView(boolean showClearView) {
        this.showClearView = showClearView;
        invalidateClearInputView();
    }

    public EditText getEditText(){
        return editText;
    }

    public String getText(){
        return editText.getText().toString();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.clearInput){
            editText.getText().clear();
            v.setVisibility(INVISIBLE);
        }else if(v.getId()==R.id.passwordVisible){
            if (v.isSelected()) {
                editText.setInputType(mInputTypeBackup);
            } else {
                editText.setInputType(mInputTypeBackup & ~InputType
                        .TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            v.setSelected(!v.isSelected());
            editText.setSelection(editText.getText().length());
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(v==editText){
            invalidateSwitchPasswordVisibleView();
            invalidateClearInputView();
        }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

    }


    public interface Condition{
        boolean getCondition(int i);
    }

    public interface TextChangedListener{
        void onTextChanged(CharSequence str);
    }


    private class NormalTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(textChangedListener!=null){
                textChangedListener.onTextChanged(s.toString());
            }
            invalidateClearInputView();
        }
    }

    private class SeparatorTextWatcher implements TextWatcher{
        private int start;
        private int count;
        private int before;
        private String beforeText;
        private int separatorCount;
        private int currentPos;
        private boolean isAfterFormatResetText;
        private StringBuilder builder=new StringBuilder();
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if(isAfterFormatResetText)return;
            this.beforeText=s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(isAfterFormatResetText)return;
            this.start=start;
            this.before=before;
            this.count=count;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(isAfterFormatResetText){
                isAfterFormatResetText=false;
                int selection=currentPos+separatorCount;
                if(selection>s.length())selection=s.length();
                if(selection<0)selection=0;
                editText.setSelection(selection);
                invalidateClearInputView();
                if(textChangedListener!=null)textChangedListener.onTextChanged(s.toString());
                return;
            }
            int delCount=s.length()-maxLength;
            if(delCount>0){
                s.delete(start+count-delCount,start+count);
            }
            String rawText = s.toString();
            String textBeforeStart = s.subSequence(0, start).toString();
            int realStart = getRealText(textBeforeStart).length();
            currentPos = Math.min(maxLength, realStart + count);

            boolean delSingleChar = (before == 1 && count == 0);
            boolean delSeparator = delSingleChar && (beforeText.charAt(start) + "").equals(separatorText);

            if (delSeparator) {
                currentPos--;
                builder.delete(0, builder.length());
                builder.append(rawText.substring(0, start - 1))
                        .append(rawText.substring(start));
                rawText = builder.toString();
            }

            separatorCount = 0;
            builder.delete(0, builder.length());
            String realText = getRealText(rawText);
            int length = Math.min(maxLength, realText.length());

            for (int i = 0; i < length; i++) {
                builder.append(realText.charAt(i));
                if (condition!=null && condition.getCondition(i) && i < length - 1) {
                    builder.append(separatorText);
                    if (i < currentPos) {
                        separatorCount++;
                    }
                }
            }
            isAfterFormatResetText=true;
            editText.setText(builder.toString());
            
        }

        private String getRealText(String str){
            if(!TextUtils.isEmpty(str)){
                return str.replaceAll(separatorText,"");
            }
            return "";
        }
    }
}
