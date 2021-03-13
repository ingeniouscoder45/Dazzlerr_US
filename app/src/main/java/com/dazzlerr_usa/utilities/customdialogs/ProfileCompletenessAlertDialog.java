package com.dazzlerr_usa.utilities.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dazzlerr_usa.R;

public class ProfileCompletenessAlertDialog
{

    Context con;
    Dialog dialog;
    Button dialogPositiveBtn;
    TextView dialogNegativeBtn;
    TextView dialogAlertTxt;
    TextView progressTitle;
    ProgressBar progressView;
    public ProfileCompletenessAlertDialog(Context con)
    {
        this.con  = con;
        dialog = new Dialog(con, R.style.NewDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.profile_completeness_alertdialog_layout);

        dialogAlertTxt = (TextView) dialog.findViewById(R.id.dialogAlertTxt);
        progressTitle = (TextView) dialog.findViewById(R.id.progressTitle);
        progressView = (ProgressBar) dialog.findViewById(R.id.progressView);
        dialogPositiveBtn = (Button) dialog.findViewById(R.id.dialogPositiveBtn);
        dialogNegativeBtn = (TextView) dialog.findViewById(R.id.dialogNegativeBtn);

    }

    public void setMessage(String message)
    {
        if(dialogAlertTxt!=null)
        dialogAlertTxt.setText(message);
    }

    public void setprogressValue(int profileCompletenessVal)
    {
    progressTitle.setText(""+profileCompletenessVal+"%");
    progressView.setProgress(profileCompletenessVal);
    }

    public void setPositiveButton(String text , ProfileCompletenessListener.onPositiveClickListener positiveListener)
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


    public void setNegativeButton(String text , ProfileCompletenessListener.onNegativeClickListener negetiveListener)
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


                    negetiveListener.onNegativeClick();
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
