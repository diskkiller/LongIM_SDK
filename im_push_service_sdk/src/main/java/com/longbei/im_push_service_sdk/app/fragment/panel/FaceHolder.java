package com.longbei.im_push_service_sdk.app.fragment.panel;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.longbei.im_push_service_sdk.R;
import com.longbei.im_push_service_sdk.R2;
import com.longbei.im_push_service_sdk.common.app.face.Face;
import com.longbei.im_push_service_sdk.common.widget.recycler.RecyclerAdapter;


import butterknife.BindView;

/**
 * @version 1.0.0
 */
public class FaceHolder extends RecyclerAdapter.ViewHolder<Face.Bean> {
    @BindView(R2.id.im_face)
    ImageView mFace;

    public FaceHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void onBind(Face.Bean bean) {
        if (bean != null
                // drawable 资源 id
                && ((bean.preview instanceof Integer)
                // face zip 包资源路径
                || bean.preview instanceof String))
            Glide.with(itemView.getContext())
            .asBitmap()
                    .load(bean.preview)
                    .format(DecodeFormat.PREFER_ARGB_8888) //设置解码格式8888，保证清晰度
                    .placeholder(R.drawable.default_face)
                    .into(mFace);
    }
}
