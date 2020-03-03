package com.hxz.example.util.protocol;

public class MessageResponse {
    private String requestid;
    private int errcode;
    private String errinfo;
    private String data;
    
	public String getRequestid() {
		return requestid;
	}
	public void setRequestid(String requestid) {
		this.requestid = requestid;
	}
	public int getErrcode() {
		return errcode;
	}
	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}
	public String getErrinfo() {
		return errinfo;
	}
	public void setErrinfo(String errinfo) {
		this.errinfo = errinfo;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	public boolean isSuccess(){
		return this.errcode == 0;
	}

	@Override
	public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("MessageRequest[requestid=");
        sb.append(requestid);
        sb.append(", errcode=");
        sb.append(errcode);
        sb.append(", errinfo=");
        sb.append(errinfo);
        sb.append(", data=");
        sb.append(data);
        sb.append("]");
        return sb.toString();
	}
}
