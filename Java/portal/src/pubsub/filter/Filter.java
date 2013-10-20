package pubsub.filter;

import java.io.Serializable;

import pubsub.msg.AbstractMessage;


public class Filter implements Serializable {
	public static final int EQUALS = 0, GT = 1, LT = 2;
	private int op;
	private String key;
	private Object value;
	public boolean eval(AbstractMessage msg) {
		switch (op) {
		case EQUALS:
			if (msg.get(key)!=null) {
				return msg.get(key).equals(value);
			}
			else return false;
		case GT:
			Object v = msg.get(key);
			if (v != null && v instanceof Comparable && value instanceof Comparable) {
				return ((Comparable)v).compareTo(value)>0;
			}
			else return false;
		case LT:
			v = msg.get(key);
			if (v != null && v instanceof Comparable && value instanceof Comparable) {
				return ((Comparable)v).compareTo(value)<0;
			}
			else return false;
		default: return false;
		}
	}
	public boolean equals(Object o) {
		if (o instanceof Filter) {
			Filter f = (Filter)o;
			return (op==f.op && key.equals(f.key) && value.equals(f.value));
		} else return false;
	}
	public static Filter parseFilter(String exp) {
		int op;
		char opChar;
		if (exp.indexOf('=')!=-1) {
			opChar = '=';
			op = EQUALS;
		} else if (exp.indexOf('>')!=-1) {
			opChar = '>';
			op = GT;
		} else if (exp.indexOf('<')!=-1) {
			opChar = '<';
			op = LT;
		} else return null;
		String[] kv = exp.split(String.valueOf(opChar));
		return new Filter(op, kv[0], kv[1]);
	}
	
	public Filter(int op, String key, Object value) {
		this.op = op;
		this.key = key;
		this.value = value;
	}
	public String toString() {
		char operator = ' ';
		switch (op) {
		case 0:operator='=';break;
		case 1:operator='>';break;
		case 2:operator='<';break;
		}
		return key + operator + value;
	}
}
