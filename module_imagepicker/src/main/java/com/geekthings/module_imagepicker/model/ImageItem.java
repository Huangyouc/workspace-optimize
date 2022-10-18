package com.geekthings.module_imagepicker.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by sunny on 2017/11/10.
 */
public class ImageItem implements Serializable, Parcelable {
    public String id;         //图片的ID
    public Uri uri;
    public String name;       //图片的名字
    public String path;       //图片的路径
    public String bucketId;   //文件夹id
    public String bucketName; //文件夹name
    public long size;         //图片的大小
    public int width;         //图片的宽度
    public int height;        //图片的高度
    public String mimeType;   //图片的类型
    public long addTime;      //图片的创建时间


    public ImageItem() {
    }

    protected ImageItem(Parcel in) {
        this.id = in.readString();
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.name = in.readString();
        this.path = in.readString();
        this.bucketId = in.readString();
        this.bucketName = in.readString();
        this.size = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
        this.mimeType = in.readString();
        this.addTime = in.readLong();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeParcelable(this.uri, flags);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.bucketId);
        dest.writeString(this.bucketName);
        dest.writeLong(this.size);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.mimeType);
        dest.writeLong(this.addTime);
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageItem imageItem = (ImageItem) o;

        if (!id.equals(imageItem.id)) return false;
        if (!uri.equals(imageItem.uri)) return false;
        return path.equals(imageItem.path);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + uri.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }
}

