package com.example.module_base.share;

public class WXShareImgUrlBean extends WXShareBaseBean {
	public String description;
	public String url;
	public String filePath = "";
	public String title = "IFA";

	public WXShareImgUrlBean() {
		shareType = WXShareAction.SHAREIMGURL;
	}
}
