package org.coolnx.lib;

import android.text.InputFilter;
import android.text.Spanned;

public class LLengthFilter implements InputFilter {
	public LLengthFilter(int max) {
		mMax = max;
	}

	private static boolean isDoubleChar(char c) {
		return c < 0 || c > 0x80;
	}

	private static int getStrLen(CharSequence str) {
		int len = str.length();
		for (int i = len - 1; i >= 0; i--) {
			char c = str.charAt(i);
			if (isDoubleChar(c)) {
				len++;
			}
		}
		return len;
	}

	private static CharSequence subSequence(CharSequence str, int start, int len) {
		int l = start;
		for (int i = 0; i < len; i++) {
			char c = str.charAt(l);
			if (isDoubleChar(c)) {
				i++;
				if (i >= len)
					break;
			}
			l++;
		}
		return str.subSequence(start, l - start);
	}

	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {

		int keep = mMax - (getStrLen(dest) - (dend - dstart));

		if (keep <= 0) {
			return "";
		} else if (keep >= end - start) {
			return null; // keep original
		} else {
			keep += start;
			if (Character.isHighSurrogate(source.charAt(keep - 1))) {
				--keep;
				if (keep == start) {
					return "";
				}
			}
			return subSequence(source, start, keep);
		}
	}

	private int mMax;
}