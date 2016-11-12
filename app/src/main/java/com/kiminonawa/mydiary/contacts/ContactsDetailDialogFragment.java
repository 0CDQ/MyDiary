package com.kiminonawa.mydiary.contacts;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kiminonawa.mydiary.R;
import com.kiminonawa.mydiary.db.DBManager;
import com.kiminonawa.mydiary.shared.ThemeManager;

import static com.kiminonawa.mydiary.R.id.EDT_diary_content;
import static com.kiminonawa.mydiary.R.id.EDT_diary_title;
import static com.kiminonawa.mydiary.R.id.SP_diary_mood;
import static com.kiminonawa.mydiary.R.id.SP_diary_weather;


/**
 * Created by daxia on 2016/8/27.
 */
public class ContactsDetailDialogFragment extends DialogFragment implements View.OnClickListener {


    /**
     * UI
     */
    private LinearLayout LL_contacts_detail_top_content;
    private EditText EDT_contacts_detail_name, EDT_contacts_detail_phone_number;
    private Button But_contacts_detail_delete, But_contacts_detail_cancel, But_contacts_detail_ok;

    /**
     * Contacts Info
     */
    private long contactsId;
    private String contactsName, contactsPhoneNumber;
    //Edit or add contacts
    private boolean isEditMode = false;


    public static ContactsDetailDialogFragment newInstance(long contactsId, String contactsName, String contactsPhoneNumber) {
        Bundle args = new Bundle();
        ContactsDetailDialogFragment fragment = new ContactsDetailDialogFragment();
        //contactsId = -1 is edit
        args.putLong("contactsId", contactsId);
        args.putString("contactsName", contactsName);
        args.putString("contactsPhoneNumber", contactsPhoneNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(false);
        View rootView = inflater.inflate(R.layout.dialog_fragment_contacts_detail, container);

        LL_contacts_detail_top_content = (LinearLayout) rootView.findViewById(R.id.LL_contacts_detail_top_content);
        LL_contacts_detail_top_content.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        EDT_contacts_detail_name = (EditText) rootView.findViewById(R.id.EDT_contacts_detail_name);
        EDT_contacts_detail_phone_number = (EditText) rootView.findViewById(R.id.EDT_contacts_detail_phone_number);

        //Set tint
        EDT_contacts_detail_name.getBackground().mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        EDT_contacts_detail_phone_number.getBackground().mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        But_contacts_detail_delete = (Button) rootView.findViewById(R.id.But_contacts_detail_delete);
        But_contacts_detail_cancel = (Button) rootView.findViewById(R.id.But_contacts_detail_cancel);
        But_contacts_detail_cancel.setOnClickListener(this);
        But_contacts_detail_ok = (Button) rootView.findViewById(R.id.But_contacts_detail_ok);
        But_contacts_detail_ok.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contactsId = getArguments().getLong("contactsId", -1);
        contactsName = getArguments().getString("contactsName", "");
        contactsPhoneNumber = getArguments().getString("contactsPhoneNumber", "");
        if (contactsId == -1) {
            isEditMode = false;
            But_contacts_detail_delete.setVisibility(View.GONE);
            But_contacts_detail_delete.setOnClickListener(this);
        } else {
            isEditMode = true;
            But_contacts_detail_delete.setVisibility(View.VISIBLE);

            EDT_contacts_detail_name.setText(contactsName);
            EDT_contacts_detail_phone_number.setText(contactsPhoneNumber);
        }
    }


    private void addContacts() {
        DBManager dbManager = new DBManager(getActivity());
        dbManager.opeDB();
        dbManager.insertContacts(EDT_contacts_detail_name.getText().toString(), EDT_contacts_detail_phone_number.getText().toString(),
                "",);
        dbManager.closeDB();
    }


    private void updateContacts() {
        DBManager dbManager = new DBManager(getActivity());
        dbManager.opeDB();
        dbManager.updateContacts(contactsId,
                EDT_contacts_detail_name.getText().toString(), EDT_contacts_detail_phone_number.getText().toString(), "");
        dbManager.closeDB();
    }


    private void deleteContacts() {
        DBManager dbManager = new DBManager(getActivity());
        dbManager.opeDB();
        dbManager.delContacts(contactsId);
        dbManager.closeDB();
    }

    private void buttonOkEvent() {
        if (isEditMode) {

        } else {

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_contacts_detail_delete:
                deleteContacrs();
                dismiss();
                break;
            case R.id.But_contacts_detail_cancel:
                dismiss();
                break;
            case R.id.But_contacts_detail_ok:
                dismiss();
                break;
        }
    }


}
