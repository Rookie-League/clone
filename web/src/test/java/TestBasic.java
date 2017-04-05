import com.earphone.wrapper.wrapper.ResultWrapper;
import com.earphone.common.constant.ResultType;

import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.earphone.common.validation.Assert;

import java.util.Objects;

/**
 * @author yaojiamin
 * @description TODO
 * @createTime 2016-6-24 上午10:59:59
 */
public class TestBasic {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static MockHttpServletRequest request;
    private static MockHttpServletResponse response;
    private static ClassPathXmlApplicationContext context;

    /**
     * @description
     * @createTime 2016-5-7 下午3:51:02
     * @fileName TestBasic.java
     * @author yaojiamin
     */
    protected void assertThis(ResultWrapper response, String message) {
        Assert.wrapObject(response.getType()).equals(ResultType.SUCCESS, message);
    }

    /**
     * @return the request
     */
    protected MockHttpServletRequest getRequest() {
        if (Objects.isNull(request)) {
            request = new MockHttpServletRequest();
            request.setSession(new MockHttpSession());
            return request;
        }
        return request;
    }

    /**
     * @return the response
     */
    protected MockHttpServletResponse getResponse() {
        if (Objects.isNull(response)) {
            return response = new MockHttpServletResponse();
        }
        return response;
    }

    @BeforeClass
    public static void setContext() {
        context = getContext();
    }

    /**
     * @return the context
     */
    private static ClassPathXmlApplicationContext getContext() {
        if (Objects.isNull(context)) {
            return context = new ClassPathXmlApplicationContext(new String[]{"spring.xml"});
        }
        return context;
    }

    /**
     * @description
     * @createTime 2016-5-5 下午3:11:19
     * @fileName TestBasic.java
     * @author yaojiamin
     */
    private <T> T getBean(T bean, Class<T> clazz) {
        if (Objects.nonNull(bean)) return bean;
        return getContext().getBean(clazz);
    }
}
