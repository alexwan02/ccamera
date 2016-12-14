package cn.alexwan.ccamera.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ImageParameters
 * Created by alexwan on 2016/12/13.
 */
public class ImageParameters implements Parcelable {
    public boolean mIsPortrait;
    public int mDisplayOrientation;
    public int mLayoutOrientation;
    public int mCoverHeight, mCoverWidth;
    public int mPreviewHeight, mPreviewWidth;

    public ImageParameters() {
    }

    public ImageParameters(Parcel in) {
        mIsPortrait = (in.readByte() == 1);
        mDisplayOrientation = in.readInt();
        mLayoutOrientation = in.readInt();
        mCoverHeight = in.readInt();
        mCoverWidth = in.readInt();
        mPreviewHeight = in.readInt();
        mPreviewWidth = in.readInt();
    }

    public int calculateCoverWidthHeight(){
        return Math.abs(mPreviewHeight - mPreviewWidth) / 2;
    }

    public int getAnimationParameter(){
        return mIsPortrait ? mCoverHeight : mCoverWidth;
    }

    public boolean isIsPortrait(){
        return mIsPortrait;
    }

    public ImageParameters createCopy(){
        ImageParameters params = new ImageParameters();
        params.mIsPortrait = mIsPortrait;
        params.mDisplayOrientation = mDisplayOrientation;
        params.mLayoutOrientation = mLayoutOrientation;

        params.mCoverHeight = mCoverHeight;
        params.mCoverWidth = mCoverWidth;
        params.mPreviewHeight = mPreviewHeight;
        params.mPreviewWidth = mPreviewWidth;
        return params;
    }

    @Override
    public String toString() {
        return "is Portrait : " + mIsPortrait + " , " +
                "\ncover height : " + mCoverHeight + " width : " + mCoverWidth +
                "\npreview height : " + mPreviewHeight + " width : " + mPreviewWidth;
    }

    public static final Creator<ImageParameters> CREATOR = new Creator<ImageParameters>() {
        @Override
        public ImageParameters createFromParcel(Parcel in) {
            return new ImageParameters(in);
        }

        @Override
        public ImageParameters[] newArray(int size) {
            return new ImageParameters[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mIsPortrait ? 1 : 0));
        //
        dest.writeInt(mDisplayOrientation);
        dest.writeInt(mLayoutOrientation);
        //
        dest.writeInt(mCoverHeight);
        dest.writeInt(mCoverWidth);
        dest.writeInt(mPreviewHeight);
        dest.writeInt(mPreviewWidth);

    }
}
