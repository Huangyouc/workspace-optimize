package com.geekthings.module_imagepicker.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class ImageFolder implements Parcelable {
    public String id;    //文件夹的id
    public String name;  //当前文件夹的名字
    public String path;  //当前文件夹的路径
    public ImageItem cover;   //当前文件夹需要要显示的缩略图，默认为最近的一次图片
    public ArrayList<ImageItem> images;  //当前文件夹下所有图片的集合





    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageFolder that = (ImageFolder) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return path != null ? path.equals(that.path) : that.path == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeParcelable(this.cover, flags);
        dest.writeTypedList(this.images);
    }

    public ImageFolder() {
    }

    protected ImageFolder(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.path = in.readString();
        this.cover = in.readParcelable(ImageItem.class.getClassLoader());
        this.images = in.createTypedArrayList(ImageItem.CREATOR);
    }

    public static final Creator<ImageFolder> CREATOR = new Creator<ImageFolder>() {
        @Override
        public ImageFolder createFromParcel(Parcel source) {
            return new ImageFolder(source);
        }

        @Override
        public ImageFolder[] newArray(int size) {
            return new ImageFolder[size];
        }
    };
}
