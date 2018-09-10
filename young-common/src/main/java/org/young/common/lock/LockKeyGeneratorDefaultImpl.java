package org.young.common.lock;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 缓存键生成器接口默认实现
 *
 * @author yangyong young1982@foxmail.com
 * date 2018/9/10 18:08
 */
@Slf4j
public class LockKeyGeneratorDefaultImpl implements LockKeyGenerator {

    /**
     * 获取AOP参数,生成指定的缓存key
     * @param joinPoint
     * AOP参数
     * @return 缓存key
     */
    @Override
    public String getLockKey(final ProceedingJoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        //
        final Lock lockAnnotation = method.getAnnotation(Lock.class);
        final Object[] args = joinPoint.getArgs();
        final Parameter[] parameters = method.getParameters();
        //
        final StringBuilder builder = new StringBuilder();
        //解析方法中带有LockParam注解
        LockParam lockParamAnnotation;
        String lockParamValue;
        for(int i = 0; i < parameters.length; i++){
            lockParamAnnotation = parameters[i].getAnnotation(LockParam.class);
            if(lockParamAnnotation == null){
                continue;
            }
            lockParamValue = buildLockParam(lockParamAnnotation, parameters[i], args[i]);
            if(!Strings.isNullOrEmpty(lockParamValue)) {
                if(builder.length() > 0){
                    builder.append(lockAnnotation.delimiter());
                }
                builder.append(lockParamValue);
            }
        }
        return lockAnnotation.prefix() + builder.toString();
    }

    /**
     * 构建锁键数据
     * @param annotation
     * 锁参数注解
     * @param parameter
     * 参数对象
     * @param arg 参数对象
     * @return 锁键数据
     */
    private String buildLockParam(final LockParam annotation,final Parameter parameter, final Object arg){
        log.debug("buildLockParam(annotation: {}, parameter: {}, arg: {})...", annotation, parameter, arg);
        if(arg == null){
            return null;
        }
        if(Strings.isNullOrEmpty(annotation.name())){
            return arg.toString();
        }
        if(parameter != null) {
            final ExpressionParser parser = new SpelExpressionParser();
            final StandardEvaluationContext context = new StandardEvaluationContext();
            context.setVariable(parameter.getName(), arg);
            final Expression exp = parser.parseExpression(annotation.name());
            return exp.getValue(context, String.class);
        }
        return null;
    }

}
