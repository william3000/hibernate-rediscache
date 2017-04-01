package com.xl.core.common;

public class Const {

	public enum ReserveWords {
		Appliccation("redis.application"), TimeStamp("redis.timestamp.map");
		
		private String word;

		private ReserveWords(String _word) {
			this.word = _word;
		}
		
		@Override
		public String toString() {
			return this.word;
		}
	}
	
}
