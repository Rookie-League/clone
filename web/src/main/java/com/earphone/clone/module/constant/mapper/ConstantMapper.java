package com.earphone.clone.module.constant.mapper;

import java.util.List;

import com.earphone.clone.module.constant.domain.Constant;
import com.earphone.common.plugin.pagination.PageEntity;

/**
 * @description TODO
 * @author yaojiamin
 * @createTime 2016-6-8 上午10:08:07
 */
public interface ConstantMapper {
    /**
     * @description TODO
     * @createTime 2016-6-8 上午10:08:11
     * @fileName ConstantMapper.java
     * @author yaojiamin
     */
    Constant findByKey ( String key );

    /**
     * @description TODO
     * @createTime 2016-6-8 上午10:08:13
     * @fileName ConstantMapper.java
     * @author yaojiamin
     */
    List<Constant> findChildListByHierarchy ( PageEntity<Constant> page );

    /**
     * @description TODO
     * @createTime 2016-6-8 上午10:08:14
     * @fileName ConstantMapper.java
     * @author yaojiamin
     */
    Constant findNoCacheByKey ( String key );
}
