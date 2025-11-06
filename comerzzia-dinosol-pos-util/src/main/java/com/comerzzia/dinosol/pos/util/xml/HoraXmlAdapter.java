package com.comerzzia.dinosol.pos.util.xml;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class HoraXmlAdapter extends XmlAdapter<String, Date> {
	
	private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

	@Override
	public String marshal(Date v) throws Exception {
		return format.format(v);
	}

	@Override
	public Date unmarshal(String v) throws Exception {
		return format.parse(v);
	}

}