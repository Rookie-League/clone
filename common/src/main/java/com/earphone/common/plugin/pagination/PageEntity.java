package com.earphone.common.plugin.pagination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaojiamin
 * @description
 * @createTime 2016-4-19 上午9:41:37
 */
public class PageEntity<T> implements java.io.Serializable {
    private static final long serialVersionUID = 8890618493507851638L;

    /**
     * 默认构造函数
     */
    public PageEntity() {
        this(DEFAULT_PAGE_NO, DEFAULT_PAGE_SIZE);
    }

    /**
     * 构造一个pageSize条数的Page对象
     */
    public PageEntity(int pageNo, int pageSize) {
        setAutoCount(true);
        setTotal(0L);
        setPageNo(pageNo);
        setPageSize(pageSize);
    }

    /**
     * 默认为自动计算记录总数 true 自动计算记录总数 false 不计算总记录数
     */
    private boolean autoCount;

    /**
     * 查询对象时是否自动另外执行count查询获取总记录数, true.
     */
    public boolean isAutoCount() {
        return autoCount;
    }

    /**
     * 查询对象时是否自动另外执行count查询获取总记录数.
     */
    public void setAutoCount(final boolean autoCount) {
        this.autoCount = autoCount;
    }

    /**
     * 排序类型
     */
    public enum ListOrder {
        ASC("asc"), DESC("desc");
        public String order;

        ListOrder(String order) {
            this.order = order;
        }

        public static ListOrder parseOrder(String order) {
            for (ListOrder listOrder : ListOrder.values()) {
                if (listOrder.order.equals(order.toLowerCase().trim())) {
                    return listOrder;
                }
            }
            return ASC;
        }
    }

    //order子句
    private Map<String, ListOrder> orderMap;

    public Map<String, ListOrder> getOrderMap() {
        if (Objects.isNull(orderMap)) {
            setOrderMap(new HashMap<>());
        }
        return orderMap;
    }

    public void setOrderMap(Map<String, ListOrder> orderMap) {
        this.orderMap = orderMap;
    }

    public PageEntity<T> putOrderBy(String column, ListOrder order) {
        getCondition().put(column, order);
        return this;
    }

    //默认页码
    private static final int DEFAULT_PAGE_NO = 1;
    //当前页的页码
    private int pageNo;

    public int getPageNo() {
        return pageNo;
    }

    /**
     * 设置当前页的页号,序号从1开始,低于1时自动调整为1.
     */
    public void setPageNo(final int pageNo) {
        this.pageNo = pageNo;
        if (pageNo < DEFAULT_PAGE_NO) {
            this.pageNo = DEFAULT_PAGE_NO;
        }
    }

    //默认页码
    private static final int DEFAULT_PAGE_SIZE = 10;
    //当前页的条数
    private int pageSize;

    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置每页的记录数量,低于0时自动调整为10.
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
        if (pageSize < 1) {
            this.pageSize = DEFAULT_PAGE_SIZE;
        }
    }

    //查询条件的对象总数
    private long total;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    //where子句条件
    private Map<String, Object> condition;

    public Map<String, Object> getCondition() {
        if (Objects.isNull(condition)) {
            setCondition(new HashMap<>());
        }
        return condition;
    }

    public void setCondition(Map<String, Object> condition) {
        this.condition = condition;
    }

    public PageEntity<T> putCondition(String key, Object value) {
        getCondition().put(key, value);
        return this;
    }

    //对象的结果集
    private List<T> result;

    /**
     * 取得页内的记录列表.
     */
    public List<T> getResult() {
        if (Objects.isNull(result)) {
            setResult(new ArrayList<>());
        }
        return result;
    }

    /**
     * 设置页内的记录列表.
     */
    public void setResult(final List<T> result) {
        this.result = result;
        setIsLastPage(!this.hasNext());
    }

    //是否是最后一页
    private boolean isLastPage;

    public boolean getIsLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    //分页开始
    public int getStart() {
        return ((pageNo - 1) * pageSize) + 1;
    }

    /**
     * 根据pageSize与total计算总页数.
     */
    public long getTotalPages() {
        if (total < 0) return 0;
        long count = total / pageSize;
        if (total % pageSize > 0) count++;
        return count;
    }

    /**
     * 是否还有下一页.
     */
    public boolean hasNext() {
        return (pageNo + 1 <= getTotalPages());
    }

    /**
     * 是否还有上一页.
     */
    public boolean hasPrev() {
        return (pageNo > 1);
    }
}
