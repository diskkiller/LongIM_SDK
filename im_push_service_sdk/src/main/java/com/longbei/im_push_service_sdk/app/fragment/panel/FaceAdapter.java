package com.longbei.im_push_service_sdk.app.fragment.panel;

import android.view.View;

import com.longbei.im_push_service_sdk.R;
import com.longbei.im_push_service_sdk.common.app.face.Face;
import com.longbei.im_push_service_sdk.common.widget.recycler.RecyclerAdapter;

import java.util.List;


/**
 * @version 1.0.0
 */
public class FaceAdapter extends RecyclerAdapter<Face.Bean> {

    public FaceAdapter(List<Face.Bean> beans, AdapterListener<Face.Bean> listener) {
        super(beans, listener);
    }

    @Override
    protected int getItemViewType(int position, Face.Bean bean) {
        return R.layout.cell_face;
    }

    @Override
    protected ViewHolder<Face.Bean> onCreateViewHolder(View root, int viewType) {
        return new FaceHolder(root);
    }
}
