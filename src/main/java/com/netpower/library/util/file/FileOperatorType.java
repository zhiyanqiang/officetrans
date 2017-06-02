package com.netpower.library.util.file;

/**
 * 文件操作类型
 * 
 * @author gzd
 * 
 */
public enum FileOperatorType {
	/** 文档类型 */
	DOC {
		@Override
		public int getValue() {
			return 1;
		}
	},
	/** 音像类型_SWF */
	KINESCOPESWF {
		@Override
		public int getValue() {
			return 2;
		}
	},
	/** 音像类型_MEDIA */
	KINESCOPEMEDIA {
		@Override
		public int getValue() {
			return 6;
		}
	},
	/** 文件夹 */
	DIR {
		@Override
		public int getValue() {
			return 3;
		}
	},
	/** 压缩 */
	COMPRESS {
		@Override
		public int getValue() {
			return 4;
		}
	},
	/** 图片类型 */
	PIC {
		@Override
		public int getValue() {
			return 5;
		}
	},
	/** 其他类型 */
	OTHER {
		@Override
		public int getValue() {
			return 0;
		}
	};
	
	/**
	 * 各类型文件操作规则
	 * @return
	 */
	public abstract int getValue();
}