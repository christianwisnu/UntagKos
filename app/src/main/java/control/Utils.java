package control;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.project.untagkos.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class Utils {

    public static final ImageLoader imgloader = ImageLoader.getInstance();

    public static void GetImage(String url, ImageView img, Context context){
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.no_image)
                .showImageForEmptyUri(R.drawable.no_image)
                .showImageOnFail(R.drawable.no_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .build();
        imgloader.init(ImageLoaderConfiguration.createDefault(context));
        imgloader.displayImage(url, img, options);
        return;
    }

    public static void GetCycleImage(String url,ImageView img,Context context){
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer((int) 50.5f))
                .showImageOnLoading(R.mipmap.ic_action_person)
                .showImageForEmptyUri(R.mipmap.ic_action_person)
                .showImageOnFail(R.mipmap.ic_action_person)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .build();
        imgloader.init(ImageLoaderConfiguration.createDefault(context));
        imgloader.displayImage(url, img, options);


        return;
    }
}
