/**
 * 
 */
package com.earphone.common.exception;

/**
 * @description
 * @author yaojiamin
 * @createTime 2016-4-6 下午2:45:49
 */
public class CapturedException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 7496156956682532706L;

    /**
     * 
     */
    public CapturedException() {
        super ( );
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public CapturedException(String message, Throwable cause) {
        super ( message, cause );
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public CapturedException(String message) {
        super ( message );
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public CapturedException(Throwable cause) {
        super ( cause );
        // TODO Auto-generated constructor stub
    }
}
