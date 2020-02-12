package com.example.myquizuserapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CatagoreyAdapter extends RecyclerView.Adapter<CatagoreyAdapter.MyViewHolder> {

    private List<CatagoreyModel> catagoreyModelList;

    public CatagoreyAdapter(List<CatagoreyModel> catagoreyModelList) {
        this.catagoreyModelList = catagoreyModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(catagoreyModelList.get(position).getUrl(),catagoreyModelList.get(position).getName(),catagoreyModelList.get(position).getSets());

    }

    @Override
    public int getItemCount() {
        return catagoreyModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView circleImageView;
        private TextView title;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView=itemView.findViewById(R.id.categoryImgeviewid);
            title=itemView.findViewById(R.id.titleId);

        }


        private void setData(String url, final String title, final int sets)
        {
            Glide.with(itemView.getContext()).load(url).into(circleImageView);
            this.title.setText(title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent setIntent=new Intent(itemView.getContext(),SetsActivity.class);
                    setIntent.putExtra("title",title);
                    setIntent.putExtra("sets",sets);
                    itemView.getContext().startActivity(setIntent);
                }
            });
        }

    }
}
