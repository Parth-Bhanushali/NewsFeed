package com.example.android.newsfeed_project;

public class News {

    /**
     * Headline(title) of the News
     */
    private String mHeadline;

    /**
     * Section(category maybe) name in which this News belongs to
     */
    private String mSectionName;

    /**
     * Image or thumbnail Url of the News
     */
    private String mThumbnailUrl;

    /**
     * Date when the News was published on the web with the format " yyyy-MM-dd'T'HH:mm:ss'Z' "
     * e.g. 2010-10-15T09:27:37Z
     */
    private String mDate;

    /**
     * Web Url of the News which contains whole news
     */
    private String mWebUrl;

    /**
     * Constructs a new {@link News} object.
     *
     * @param headline     is the headline of the News
     * @param sectionName  is the name of the section in which this News belongs to
     * @param thumbnailUrl is the thumbnail(image) url of the News
     * @param date         is the date when this News was published on the Web (In Date format)
     * @param webUrl       is the web url of the News
     */
    public News(String headline, String sectionName, String thumbnailUrl, String date, String webUrl) {
        mHeadline = headline;
        mSectionName = sectionName;
        mThumbnailUrl = thumbnailUrl;
        mDate = date;
        mWebUrl = webUrl;
    }

    /**
     * Returns the Headline of the News.
     */
    public String getHeadline() {
        return mHeadline;
    }

    /**
     * Returns the section name of the News.
     */
    public String getSectionName() {
        return mSectionName;
    }

    /**
     * Returns the thumbnail/image url of the News.
     */
    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    /**
     * Returns the Date when the News was published on the Web (In Date format).
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Returns the website url of the News
     */
    public String getWebUrl(){
        return mWebUrl;
    }
}
