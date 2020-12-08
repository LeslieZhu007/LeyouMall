package com.leyou.common.annotations;

import com.leyou.common.advice.CommonExceptionAdvice;
import com.leyou.common.advice.CommonLogAdvice;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({CommonExceptionAdvice.class, CommonLogAdvice.class})
public @interface EnableExceptionAdvice {
}
