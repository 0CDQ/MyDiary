package com.kiminonawa.mydiary.setting;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.shared.ColorTools;
import com.kiminonawa.mydiary.shared.SPFManager;
import com.kiminonawa.mydiary.shared.ThemeManager;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by daxia on 2016/11/30.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener,
        ColorPickerFragment.colorPickerCallback, AdapterView.OnItemSelectedListener {

    /**
     * Theme
     */
    private ThemeManager themeManager;
    private boolean isThemeFirstRun = true;
    private boolean isLanguageFirstRun = true;

    /**
     * UI
     */
    private Spinner SP_setting_theme, SP_setting_language;
    private ImageView IV_setting_theme_main_color, IV_setting_theme_dark_color;
    private Button But_setting_theme_default, But_setting_theme_apply;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        themeManager = ThemeManager.getInstance();

        SP_setting_theme = (Spinner) findViewById(R.id.SP_setting_theme);
        IV_setting_theme_main_color = (ImageView) findViewById(R.id.IV_setting_theme_main_color);
        IV_setting_theme_dark_color = (ImageView) findViewById(R.id.IV_setting_theme_dark_color);
        But_setting_theme_default = (Button) findViewById(R.id.But_setting_theme_default);
        But_setting_theme_apply = (Button) findViewById(R.id.But_setting_theme_apply);
        But_setting_theme_apply.setOnClickListener(this);

        SP_setting_language = (Spinner) findViewById(R.id.SP_setting_language);
        initSpinner();
        initTheme(themeManager.getCurrentTheme());
        initLanguage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Revert current theme
        themeManager.setCurrentTheme(SPFManager.getTheme(this));
    }


    private void initLanguage() {
        if (SPFManager.getLocalLanguageCode(this) != -1) {
            SP_setting_language.setSelection(SPFManager.getLocalLanguageCode(this));
        }
    }

    private void initTheme(int themeId) {
        if (themeId == ThemeManager.CUSTOM) {
            IV_setting_theme_main_color.setOnClickListener(this);
            IV_setting_theme_dark_color.setOnClickListener(this);
            But_setting_theme_default.setOnClickListener(this);
            But_setting_theme_default.setEnabled(true);
        } else {
            IV_setting_theme_main_color.setOnClickListener(null);
            IV_setting_theme_dark_color.setOnClickListener(null);
            But_setting_theme_default.setOnClickListener(null);
            But_setting_theme_default.setEnabled(false);
        }
        setThemeColor();
    }

    private void setThemeColor() {
        IV_setting_theme_main_color.setBackgroundColor(themeManager.getThemeMainColor(this));
        IV_setting_theme_dark_color.setBackgroundColor(themeManager.getThemeDarkColor(this));
    }

    private void initSpinner() {
        //Theme Spinner
        ArrayAdapter themeAdapter = new ArrayAdapter(this, R.layout.spinner_simple_text,
                getResources().getStringArray(R.array.theme_list));
        SP_setting_theme.setAdapter(themeAdapter);
        SP_setting_theme.setSelection(themeManager.getCurrentTheme());
        SP_setting_theme.setOnItemSelectedListener(this);

        //Language spinner
        ArrayAdapter languageAdapter = new ArrayAdapter(this, R.layout.spinner_simple_text,
                getResources().getStringArray(R.array.language_list));
        SP_setting_language.setAdapter(languageAdapter);
        SP_setting_language.setSelection(0);
        SP_setting_language.setOnItemSelectedListener(this);
    }

    private void applySetting() {
        //Restart App
        Intent i = this.getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(this.getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        this.finish();
        startActivity(i);
    }

    @Override
    public void onColorChange(int colorCode, int viewId) {
        switch (viewId) {
            case R.id.IV_setting_theme_main_color:
                IV_setting_theme_main_color.setBackgroundColor(colorCode);
                break;
            case R.id.IV_setting_theme_dark_color:
                IV_setting_theme_dark_color.setBackgroundColor(colorCode);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_setting_theme_default:
                IV_setting_theme_main_color.setBackgroundColor(ColorTools.getColor(this,
                        R.color.themeColor_custom_default));
                IV_setting_theme_dark_color.setBackgroundColor(ColorTools.getColor(this,
                        R.color.theme_dark_color_custom_default));
                break;
            case R.id.But_setting_theme_apply:
                SPFManager.setMainColor(this,
                        ((ColorDrawable) IV_setting_theme_main_color.getBackground()).getColor());
                SPFManager.setSecondaryColor(this,
                        ((ColorDrawable) IV_setting_theme_dark_color.getBackground()).getColor());
                themeManager.saveTheme(SettingActivity.this, SP_setting_theme.getSelectedItemPosition());
                //Send Toast
                Toast.makeText(this, getString(R.string.toast_change_theme), Toast.LENGTH_SHORT).show();
                applySetting();
                break;
            case R.id.IV_setting_theme_main_color:
                ColorPickerFragment mainColorPickerFragment
                        = ColorPickerFragment.newInstance(themeManager.getThemeMainColor(this));
                mainColorPickerFragment.setCallBack(this, R.id.IV_setting_theme_main_color);
                mainColorPickerFragment.show(getSupportFragmentManager(), "mainColorPickerFragment");
                break;
            case R.id.IV_setting_theme_dark_color:
                ColorPickerFragment secColorPickerFragment =
                        ColorPickerFragment.newInstance(themeManager.getThemeDarkColor(this));
                secColorPickerFragment.setCallBack(this, R.id.IV_setting_theme_dark_color);
                secColorPickerFragment.show(getSupportFragmentManager(), "secColorPickerFragment");
                break;

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.SP_setting_theme:
                if (!isThemeFirstRun) {
                    //Temp set currentTheme .
                    //If it doesn't apply , revert it on onDestroy .
                    themeManager.setCurrentTheme(position);
                    initTheme(position);
                } else {
                    isThemeFirstRun = false;
                }
                break;
            case R.id.SP_setting_language:
                Log.e("test", "test2");
                if (!isLanguageFirstRun) {
                    Log.e("test", "test1");
                    SPFManager.setLocalLanguageCode(this, position);
                    applySetting();
                } else {
                    isLanguageFirstRun = false;
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing

    }
}
