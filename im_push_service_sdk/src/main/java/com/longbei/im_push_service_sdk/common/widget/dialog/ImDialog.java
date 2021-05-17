package com.longbei.im_push_service_sdk.common.widget.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.longbei.im_push_service_sdk.R;


/**
 * 聊天dialog 使用方式同原生dialog 如果要显示编辑框，请传入hint
 * 
 */
public class ImDialog extends Dialog {
    public ImDialog(Context context) {
        super(context);
    }

    public ImDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {
        private Context context;
        private String title;
        private String msg;
        private String hint;
        private String showMsg;
        private String positiveButtonText;
        private String negativeButtonText;

        private Button ok, cancel;
        private TextView tvt, tmsg;
        private EditText edmsg;
        public interface ImDialogInterface {
            void onClick(DialogInterface dialog, View v, ImDialog dlg, EditText edmsg);
        }
        private ImDialogInterface positiveButtonClickListener, negativeButtonClickListener;
        
        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set the Dialog title from resource
         * 
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         * 
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set the Dialog message for resource
         * 
         * @param msg
         * @return
         */
        public Builder setMessage(int msg) {
            this.msg = (String) context.getText(msg);
            return this;
        }

        /**
         * Set the Dialog message for string
         * 
         * @param msg
         * @return
         */
        public Builder setMessage(String msg) {
            this.msg = msg;
            return this;
        }

        /**
         * Set the Dialog Edit hint for resource
         * 
         * @param
         * @return
         */
        public Builder setEditMsg(int hint) {
            this.hint = (String) context.getText(hint);
            return this;
        }

        /**
         * Set the Dialog Edit hint for String
         * 
         * @param
         * @return
         */
        public Builder setEditMsg(String hint) {
            this.hint = hint;
            return this;
        }

        public Builder setEditShowMsg(String showMsg){
            this.showMsg = showMsg;
            return this;
        }

        /**
         * Set the positive button resource and it"s listener
         * 
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText, ImDialogInterface listener) {
            this.positiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it"s listener
         * 
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText, ImDialogInterface listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it"s listener
         * 
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(int negativeButtonText, ImDialogInterface listener) {
            this.negativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button text and it"s listener
         * 
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(String negativeButtonText, ImDialogInterface listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }


        /**
         * Create the custom dialog
         */
        @SuppressLint("Override")
        public ImDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ImDialog dialog = new ImDialog(context, R.style.im_dialog);
            View layout = inflater.inflate(R.layout.im_dialog, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            Window window = dialog == null ? null : dialog.getWindow();
            if (window != null) {
                //设置dialog靠近顶部
                window.setGravity(Gravity.TOP);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                //设置margin为屏幕的20%
                WindowManager.LayoutParams lps = window.getAttributes();
                lps.verticalMargin = 0.15f;
                window.setAttributes(lps);
            }

            setViewToDialog(layout);
            if (title != null) {
                tvt.setText(title);
                tvt.setVisibility(View.VISIBLE);
            } else {
                tvt.setVisibility(View.GONE);
            }
            if (msg != null) {
                tmsg.setText(msg);
                tmsg.setVisibility(View.VISIBLE);
            } else {
                tmsg.setVisibility(View.INVISIBLE);
            }
            if(showMsg != null){
                edmsg.setText(showMsg);
                edmsg.setVisibility(View.VISIBLE);
            }else{
                edmsg.setVisibility(View.INVISIBLE);
            }
            if (hint != null) {
                edmsg.setHint(hint);
                edmsg.setVisibility(View.VISIBLE);
            } else {
                if(showMsg != null)
                    edmsg.setVisibility(View.VISIBLE);
                else
                    edmsg.setVisibility(View.INVISIBLE);
            }
            ok.setText(positiveButtonText != null ? positiveButtonText : "确定");
                ok.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String tag = null;
                        if (edmsg.getText() != null){
                            tag = edmsg.getText().toString();
                        }
                        if (edmsg != null) v.setTag(tag);
                        if (positiveButtonClickListener != null)
                            positiveButtonClickListener.onClick(dialog, v,dialog,edmsg);
//                        dialog.dismiss();
                    }
                });
            cancel.setText(negativeButtonText != null ? negativeButtonText : "取消");
                cancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (negativeButtonClickListener != null)
                            negativeButtonClickListener.onClick(dialog, v,dialog,edmsg);
//                        dialog.dismiss();
                    }
                });
            return dialog;
        }

        private void setViewToDialog(View layout) {
            ok = (Button) layout.findViewById(R.id.im_dialog_ok);
            cancel = (Button) layout.findViewById(R.id.im_dialog_cancel);
            tvt = (TextView) layout.findViewById(R.id.im_dialog_title);
            tmsg = (TextView) layout.findViewById(R.id.im_dialog_msg);
            edmsg = (EditText) layout.findViewById(R.id.im_dialog_edit);
        }
    }
}
