package com.mhysa.waimai.ui.adapters.personal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joey.devilfish.utils.StringUtils;
import com.mhysa.waimai.R;
import com.mhysa.waimai.model.card.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件描述
 * Date: 2017/9/19
 *
 * @author xusheng
 */

public class CardListAdapter extends BaseAdapter {

    private Context mContext;

    private List<Card> mCards = new ArrayList<Card>();

    private CardListListener mListener;

    public CardListAdapter(Context context, List<Card> cards) {
        this.mContext = context;
        this.mCards = cards;
    }

    @Override
    public int getCount() {

        if (null != mCards) {
            return mCards.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != mCards && mCards.size() > position) {
            return mCards.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_card, null);

            holder.mCardNoTv = (TextView) convertView.findViewById(R.id.tv_card_no);
            holder.mDeleteTv = (TextView) convertView.findViewById(R.id.tv_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Card card = mCards.get(position);
        if (null != card) {

            StringUtils.getInstance().setText(getStarCardNo(card.card_no),
                    holder.mCardNoTv);

            holder.mDeleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.delete(position, card);
                    }
                }
            });
        }

        return convertView;
    }

    private String getStarCardNo(String cardNo) {
        if (StringUtils.getInstance().isNullOrEmpty(cardNo)) {
            return "";
        }

        int length = cardNo.length();
        if (cardNo.length() <= 4) {
            return cardNo;
        }

        int index = length - 4;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < index; i++) {
            stringBuilder.append("*");

            if (i % 4 == 3) {
                stringBuilder.append(" ");
            }
        }
        stringBuilder.append(cardNo.substring(index));

        return stringBuilder.toString();
    }

    public void setListener(CardListListener listener) {
        this.mListener = listener;
    }

    class ViewHolder {
        TextView mCardNoTv;

        TextView mDeleteTv;
    }

    public interface CardListListener {

        void delete(int position, Card card);
    }
}
