package com.earphone.clone.module.constant.domain;

/**
 * 
 * ClassName: Constant 
 * @Description: 常量
 * @author cyb
 * @date 2016-4-5
 */
public class Constant {
	private Integer id;
	
	/**
	 * 常量的键，会在程序中声明，已用于获取value或者子常量
	 */
	private String key;
	
	/**
	 * 常量的值
	 */
	private String value;
	
	/**
	 * 备注
	 */
	private String remark;
	
	private Long operTime;
	
	private String operUser;
	
	private String hierarchy;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getOperTime() {
		return operTime;
	}

	public void setOperTime(Long operTime) {
		this.operTime = operTime;
	}

	public String getOperUser() {
		return operUser;
	}

	public void setOperUser(String operUser) {
		this.operUser = operUser;
	}

	public String getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(String hierarchy) {
		this.hierarchy = hierarchy;
	}
	
}
