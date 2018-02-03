package com.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class GoodsImagesAdapter extends RecyclerView.Adapter<GoodsImagesAdapter.MyViewHolder> {
    private Activity activity;
    private ArrayList<Bitmap> listImages;
    public GoodsImagesAdapter(Activity activity, ArrayList<Bitmap> listImages){
        this.activity=activity;
        this.listImages=listImages;
    }

    public GoodsImagesAdapter() {}

    public void notiFydata(Bitmap bitmap,int count)
    {
        if(count==listImages.size()){
            listImages.add(bitmap);
            notifyDataSetChanged();
        }else{
            listImages.set(count,bitmap);
            notifyItemChanged(count);
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView,crossImage;

        public MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_goods);
            crossImage = view.findViewById(R.id.cross_image);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_goods_image, parent, false);
        return new MyViewHolder(itemView);



    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Bitmap bitmap = listImages.get(position);
        if (bitmap != null){
           // Bitmap scaledImage = setPic(holder.imageView, bitmap);
        //   holder.imageView.animate().rotation(90).start();

            holder.imageView.setImageBitmap(bitmap);
            holder.crossImage.setOnClickListener(view -> removeItem(holder.getAdapterPosition()));



           /* ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
            final byte[] byteArray = stream.toByteArray();
            holder.imageView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {

                  *//* Bundle bundle=new Bundle();
                   Intent intent =new Intent(activity, ZoomActivity.class);
                   bundle.putByteArray("byteArray",byteArray);
                   intent.putExtras(bundle);
                   activity.startActivity(intent);*//*

               }
           });*/

          // holder.imageView.setImageResource(R.drawable.driver_2);
        }
    }


    private void removeItem(int position) {
        MainActivity.imagecount--;
        listImages.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listImages.size());
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public int getItemCount() {
        return (null != listImages ? listImages.size() : 0);
    }
}
