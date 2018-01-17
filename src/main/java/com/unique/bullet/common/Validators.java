package com.unique.bullet.common;

import com.unique.bullet.exception.BulletException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.TypedStringValue;

import java.util.Map;

public class Validators {
    private static final Logger logger = LogManager.getLogger(Validators.class);

    public static void checkFilterProp(String key) {
        if (StringUtils.isNoneEmpty(key)) {
            if (!key.startsWith(Constants.PROP_FREFIX)) {
                throw new BulletException(String.format("filter property[%s] must be start with " + Constants.PROP_FREFIX, key));
            }
        }
    }

    public static void checkFilterProp(Map<?, ?> filterProp) {
        for (Map.Entry<?, ?> entry : filterProp.entrySet()) {
            if (TypedStringValue.class.isInstance(entry.getKey())) {
                TypedStringValue key = (TypedStringValue) entry.getKey();
                checkFilterProp(key.getValue());
            }
            if (String.class.isInstance(entry.getKey())) {
                checkFilterProp(String.valueOf(entry.getKey()));
            }

        }
    }

}
