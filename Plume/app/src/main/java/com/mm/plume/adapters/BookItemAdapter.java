package com.mm.plume.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mm.plume.R;
import com.mm.plume.javaclasses.BookInfo;

import java.util.ArrayList;

/**
 * Created by MM on 12/25/2017.
 */

public class BookItemAdapter extends RecyclerView.Adapter<BookItemAdapter.BookItemAdapterViewHolder>{
    private ArrayList<BookInfo> booksData;
    private Context context;

    private final BookItemAdapterOnClickHandler clickHandler;

    public interface BookItemAdapterOnClickHandler {
        void onClick(BookInfo book);
    }

    public BookItemAdapter(Context context,BookItemAdapterOnClickHandler mClickHandler){
        this.context = context;
        clickHandler = mClickHandler;
    }

    public class BookItemAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView bookPoster;

        public BookItemAdapterViewHolder(View view){
            super(view);
            bookPoster = (ImageView) view.findViewById(R.id.book_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            BookInfo book = booksData.get(adapterPosition);
            clickHandler.onClick(book);
        }
    }

    @Override
    public BookItemAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int LayoutIdForBookInList = R.layout.book_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(LayoutIdForBookInList, viewGroup,false);
        return new BookItemAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookItemAdapterViewHolder holder, int position) {
//        Picasso.with(context).load(booksData.get(position).getPosterPath()).into(holder.bookPoster);
        Glide
                .with(context)
                .load(booksData.get(position).getThumbnail())
//                .apply(new RequestOptions()
//                        .override(400, 200)
//                        .placeholder(logo))
                .into(holder.bookPoster);
    }

    @Override
    public int getItemCount() {
        if(booksData == null) return 0;
        return booksData.size();
    }
    public void setBookData(ArrayList<BookInfo> bookData) {
        booksData = bookData;
        notifyDataSetChanged();
    }

}

