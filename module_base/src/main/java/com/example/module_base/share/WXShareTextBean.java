package com.example.module_base.share;

public class WXShareTextBean extends WXShareBaseBean {
	/**
	 * 文本分享的内容
	 */
	public String content;

	public WXShareTextBean() {
		shareType = WXShareAction.SHARETEXT;
	}
}
