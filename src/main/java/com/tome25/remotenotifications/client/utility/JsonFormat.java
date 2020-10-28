package com.tome25.remotenotifications.client.utility;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Objects;

import com.tome25.utils.json.JsonElement;
import com.tome25.utils.json.JsonParser;

public class JsonFormat extends Format {

	/**
	 * Generated serial version unique identifier
	 */
	private static final long serialVersionUID = -2391922567152465471L;

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
		Objects.requireNonNull(obj, "obj can't be null!");
		Objects.requireNonNull(toAppendTo, "toAppendTo can't be null!");
		Objects.requireNonNull(pos, "pos can't be null!");
		if (!(obj instanceof JsonElement)) {
			throw new IllegalArgumentException("Format target must implement JsonElement");
		}
		pos.setBeginIndex(0);
		pos.setEndIndex(0);
		toAppendTo.append(obj.toString());
		return toAppendTo;
	}

	@Override
	public JsonElement<?> parseObject(String source, ParsePosition pos) {
		try {
			JsonElement<?> result = JsonParser.parseString(source.substring(pos.getIndex()));
			pos.setIndex(pos.getIndex() + result.toString().length());
			return result;
		} catch (ParseException e) {
			pos.setErrorIndex(pos.getIndex() + e.getErrorOffset());
			return null;
		}
	}

}
