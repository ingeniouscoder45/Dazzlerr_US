package com.dazzlerr_usa.utilities.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.dazzlerr_usa.R;

public class CustomDialog
{

    Context con;
    Dialog dialog;
    Button dialogPositiveBtn;
    Button dialogNegativeBtn;
    TextView dialogAlertTxt;
    TextView dialogTitleTxt;

    public CustomDialog(Context con)
    {
        this.con  = con;
        dialog = new Dialog(con, R.style.NewDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_layout);

        dialogAlertTxt = (TextView) dialog.findViewById(R.id.dialogAlertTxt);
        dialogTitleTxt = (TextView) dialog.findViewById(R.id.dialogTitleTxt);
        dialogPositiveBtn = (Button) dialog.findViewById(R.id.dialogPositiveBtn);
        dialogNegativeBtn = (Button) dialog.findViewById(R.id.dialogNegativeBtn);
    }

    public void setTitle(String title)
    {
        if(dialogAlertTxt!=null)
            dialogTitleTxt.setText(title);
    }

    public void setMessage(String message)
    {
        if(dialogAlertTxt!=null)
        dialogAlertTxt.setText(message);
    }


    public void setPositiveButton(String text , DialogListenerInterface.onPositiveClickListener positiveListener)
    {
        if(dialogPositiveBtn!=null) {
            dialogPositiveBtn.setText(text);
            dialogPositiveBtn.setVisibility(View.VISIBLE);
        }

        if(dialogPositiveBtn!=null) {
            dialogPositiveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null)
                        dialog.dismiss();


                    positiveListener.onPositiveClick();

                }
            });

        }
    }


    public void setNegativeButton(String text , DialogListenerInterface.onNegetiveClickListener negetiveListener)
    {
        if(dialogNegativeBtn!=null) {
            dialogNegativeBtn.setText(text);
            dialogNegativeBtn.setVisibility(View.VISIBLE);
        }

        if(dialogNegativeBtn!=null) {
            dialogNegativeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null)
                        dialog.dismiss();


                    negetiveListener.onNegetiveClick();
                }
            });
        }

    }


    public void show()
    {
        if(dialog!=null)
        dialog.show();
    }

    public void dismiss()
    {
        if(dialog!=null)
            dialog.dismiss();
    }

}
