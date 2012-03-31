package com.unidw.album.model;

public class AlbumModel {

	private String id;
	private String albumName;
	private String albumThumbnailSource;
	private String createTime;
	private String pageNum;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public String getAlbumThumbnailSource() {
		return albumThumbnailSource;
	}

	public void setAlbumThumbnailSource(String albumThumbnailSource) {
		this.albumThumbnailSource = albumThumbnailSource;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getPageNum() {
		return pageNum;
	}

	public void setPageNum(String pageNum) {
		this.pageNum = pageNum;
	}

}
