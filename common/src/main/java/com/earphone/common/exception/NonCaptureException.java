/**
 * 
 */
package com.earphone.common.exception;

/**
 * @description
 * @author yaojiamin
 * @createTime 2016-4-6 下午2:45:19
 */
public class NonCaptureException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = -7057576830619737368L;

    /**
     * 
     */
    public NonCaptureException() {
        super ( );
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public NonCaptureException(String message, Throwable cause) {
        super ( message, cause );
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public NonCaptureException(String message) {
        super ( message );
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public NonCaptureException(Throwable cause) {
        super ( cause );
        // TODO Auto-generated constructor stub
    }
}
