package si.fri.besedko;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Bitmap[] mImages;
	
    public ImageAdapter(Context c) { mContext = c; }
    public int getCount() { return mImages.length; }
    public Object getItem(int position) { return position; }
    public long getItemId(int position) { return position; }   
    public void setImages(Bitmap[] images) { mImages = images; }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView i = new ImageView(mContext);
        // TODO: ta funkcija predpostavlja da prej klicemo setImages()! fix this
        i.setImageBitmap(mImages[position]);
        i.setScaleType(ImageView.ScaleType.FIT_XY);
        i.setLayoutParams(new Gallery.LayoutParams(136, 88));

        return i;
    }
}