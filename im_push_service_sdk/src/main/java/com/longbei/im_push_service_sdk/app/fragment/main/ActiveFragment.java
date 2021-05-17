package com.longbei.im_push_service_sdk.app.fragment.main;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.longbei.im_push_service_sdk.R;
import com.longbei.im_push_service_sdk.R2;
import com.longbei.im_push_service_sdk.app.activitys.MessageActivity;
import com.longbei.im_push_service_sdk.common.app.PresenterFragment;
import com.longbei.im_push_service_sdk.common.app.face.Face;
import com.longbei.im_push_service_sdk.common.app.kit.ui.Ui;
import com.longbei.im_push_service_sdk.common.app.widget.EmptyView;
import com.longbei.im_push_service_sdk.common.app.widget.PortraitView;
import com.longbei.im_push_service_sdk.common.utils.DateTimeUtil;
import com.longbei.im_push_service_sdk.common.widget.recycler.RecyclerAdapter;
import com.longbei.im_push_service_sdk.im.db.GroupMember;
import com.longbei.im_push_service_sdk.im.db.Session;
import com.longbei.im_push_service_sdk.im.db.helper.DbHelper;
import com.longbei.im_push_service_sdk.im.lpresenter.presenter.message.SessionContract;
import com.longbei.im_push_service_sdk.im.lpresenter.presenter.message.SessionPresenter;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class ActiveFragment extends PresenterFragment<SessionContract.Presenter>
        implements SessionContract.View {

    @BindView(R2.id.empty)
    EmptyView mEmptyView;

    @BindView(R2.id.recycler)
    SwipeRecyclerView mRecycler;

    // 适配器，User，可以直接从数据库查询数据
    private RecyclerAdapter<Session> mAdapter;


    public ActiveFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        // 初始化Recycler
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // 创建菜单：
        SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int viewType) {
           /* SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
            // 各种文字和图标属性设置。
            leftMenu.addMenuItem(deleteItem); // 在Item左侧添加一个菜单。*/


                // 在Item右侧添加一个菜单。
                // 1.编辑
                // 各种文字和图标属性设置。
                SwipeMenuItem modifyItem = new SwipeMenuItem(getActivity())
                        .setBackgroundColor(getResources().getColor(R.color.yellow_100))
                        .setText("编辑")
                        .setTextColor(Color.BLACK)
                        .setTextSize(15) // 文字大小。
                        .setWidth(240)
                        .setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                rightMenu.addMenuItem(modifyItem);
                // 2 删除
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                deleteItem.setText("删除")
                        .setBackgroundColor(getResources().getColor(R.color.red_100))
                        .setTextColor(Color.WHITE) // 文字颜色。
                        .setTextSize(15) // 文字大小。
                        .setWidth(240)
                        .setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

                rightMenu.addMenuItem(deleteItem);

                // 注意：哪边不想要菜单，那么不要添加即可。
            }
        };
        // 设置监听器。
        mRecycler.setSwipeMenuCreator(mSwipeMenuCreator);


        OnItemMenuClickListener mItemMenuClickListener = new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int position) {
                // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
                menuBridge.closeMenu();

                // 左侧还是右侧菜单：
                int direction = menuBridge.getDirection();
                // 菜单在Item中的Position：
                int menuPosition = menuBridge.getPosition();
                if (menuPosition == 0) {

                    Log.i("datainfo","编辑");

                } else {
                    Log.i("datainfo","删除");
                    // 从数据源移除该Item对应的数据，并刷新Adapter。
                    mAdapter.getItems().remove(position);
                    mAdapter.notifyItemRemoved(position);

                }
            }
        };
        // 菜单点击监听。
        mRecycler.setOnItemMenuClickListener(mItemMenuClickListener);








        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<Session>() {
            @Override
            protected int getItemViewType(int position, Session session) {
                // 返回cell的布局id
                return R.layout.cell_chat_list;
            }

            @Override
            protected ViewHolder<Session> onCreateViewHolder(View root, int viewType) {
                return new ActiveFragment.ViewHolder(root);
            }
        });


        // 点击事件监听
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Session>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Session session) {
                // 跳转到聊天界面
                if(session.getUnReadCount()>0){
                    session.setUnReadCount(0);
                    List<Session> mSession = new ArrayList<>();
                    mSession.add(session);
                    DbHelper.save(Session.class,mSession.toArray(new Session[0]));
                    mAdapter.notifyItemChanged(holder.getAdapterPosition());
                }

                MessageActivity.show(getContext(), session);

            }
        });

        // 初始化占位布局
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);

    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        // 进行一次数据加载
        mPresenter.start();
    }

    @Override
    protected SessionContract.Presenter initPresenter() {
        return new SessionPresenter(this);
    }

    @Override
    public RecyclerAdapter<Session> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    @Override
    public void onAdapterDataChanged(List<Session> dataList) {
        Log.i("datainfo","ActiveFragment  onAdapterDataChanged :数据返回到界面");

    }

    // 界面数据渲染
    class ViewHolder extends RecyclerAdapter.ViewHolder<Session> {
        @BindView(R2.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R2.id.txt_name)
        TextView mName;

        @BindView(R2.id.txt_content)
        TextView mContent;

        @BindView(R2.id.txt_time)
        TextView mTime;

        @BindView(R2.id.unread_count_text)
        TextView unread_count_text;

        ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Session session) {
            mPortraitView.setup(Glide.with(ActiveFragment.this), session.getPicture());

            int unReadCount = session.getUnReadCount();
            if(unReadCount>0){
                unread_count_text.setText(unReadCount+"");
                unread_count_text.setVisibility(View.VISIBLE);
            }else{
                unread_count_text.setText("");
                unread_count_text.setVisibility(View.INVISIBLE);
            }

            mName.setText(session.getTitle());

            Spannable spannable = new SpannableString(TextUtils.isEmpty(session.getContent()) ? "" : session.getContent());

            // 解析表情
            Face.decode(mContent, spannable, (int) Ui.dipToPx(getResources(), 20));

            // 把内容设置到布局上
            mContent.setText(spannable);

            mTime.setText(DateTimeUtil.getSampleDate(session.getModifyAt()));
        }
    }
}
