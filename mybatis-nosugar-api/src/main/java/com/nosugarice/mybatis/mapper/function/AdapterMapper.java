package com.nosugarice.mybatis.mapper.function;

import com.nosugarice.mybatis.annotation.Provider;
import com.nosugarice.mybatis.annotation.ProviderAdapter;
import com.nosugarice.mybatis.mapper.MapperParam;
import org.apache.ibatis.annotations.Param;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/12/19
 */
public interface AdapterMapper {

    /**
     * 桥接方法
     *
     * @param adapter 桥接类型
     * @param funS    桥接方法lambda引用
     * @param params  参数列表
     * @param <R>     返回类型
     * @return 返回数据
     */
    @ProviderAdapter
    <R> R selectAdapter(Provider.Adapter adapter, @Param(MapperParam.MAPPER_FUNCTION) FunS<?> funS, @Param(MapperParam.PARAMS) Object... params);

}
