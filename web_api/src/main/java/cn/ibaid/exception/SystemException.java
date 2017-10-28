package cn.ibaid.exception;


/**
 * 运行时异常类
 * 
 * @author 513416
 *
 */
public class SystemException extends RuntimeException {

	private static final long serialVersionUID = 2943412316873574040L;

	private int code;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public SystemException() {
		super();
	}
	
	public SystemException(String message) {
		super(message);
	}
	
	public SystemException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SystemException(Throwable cause) {
        super(cause);
    }
	
	public SystemException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
	
	public SystemException(int code, String message) {
        super(message);
        this.code = code;
    }
	
	public SystemException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }
}
