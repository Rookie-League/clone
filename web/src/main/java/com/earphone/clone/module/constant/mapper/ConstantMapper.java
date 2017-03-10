package com.earphone.clone.module.constant.mapper;

import com.earphone.clone.module.constant.domain.Constant;
import org.apache.ibatis.annotations.Param;

/**
 * @author yaojiamin
 * @description TODO
 * @createTime 2016-6-8 上午10:08:07
 */
public interface ConstantMapper {
    /**
     * @description TODO
     * @createTime 2016-6-8 上午10:08:11
     * @fileName ConstantMapper.java
     * @author yaojiamin
     */
    Constant findByKey(@Param("key") String key);
}
