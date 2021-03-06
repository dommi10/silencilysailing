package net.silencily.sailing.framework.utils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.StringUtils;

/**
 * @author zhangli
 * @version $Id: StringConverter.java,v 1.1 2010/12/10 10:54:19 silencily Exp $
 * @since 2007-5-1
 */
public class StringConverter implements Converter {
    
    public Object convert(Class type, Object value) {
        if (type != String.class && type != String[].class) {
            Converter c = findConverter(type);
            if (c != null) {
                return c.convert(type, value);
            }
        }
        String ret = null;
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            ret = (String) value;
            /*
            try {
                ret = new String(((String) value).getBytes(null));
            } catch (UnsupportedEncodingException e) {
                ret = (String) value;
                if (logger.isInfoEnabled()) {
                    logger.info("转换参数编码错误,参数值["
                        + value
                        + "]", e);
                }
            }
            */
        } else {
            ret = value.toString();
        }
        /* 如果存在单数的'\'就变成两个相连的'\\',避免页面和程序中的问题 */
        if (ret.indexOf('\\') > -1) {
            boolean single = false;
            char[] c = new char[ret.length() * 2];
            int cursor = 0;
            for (int i = 0; i < ret.length(); i++) {
                c[cursor++] = ret.charAt(i);
                if (ret.charAt(i) == '\\') {
                    single = !single;
                } else if (single) {
                    c[cursor++] = '\\';
                    single = !single;
                }
            }
            
            if (single) {
                c[cursor++] = '\\';
            }
            
            ret = new String(c, 0, cursor);
        }
        
        if (type.equals(String[].class)) {
            return StringUtils.split(ret, ',');
        }

        return ret;
    }
    
    private Converter findConverter(Class type) {
        Class clazz = type.getSuperclass();
        Converter c = null;
        if (clazz != null) {
            c = BeanUtilsBean.getInstance().getConvertUtils().lookup(clazz);
            if (c == null) {
                c = findConverter(clazz);
            }
        }
        return c;
    }
}
