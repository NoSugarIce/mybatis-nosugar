package com.nosugarice.mybatis.mapper.function;

import com.nosugarice.mybatis.annotation.ProviderAdapter;
import com.nosugarice.mybatis.mapper.MapperParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/12/19
 */
public interface AdapterMapper {

    /**
     * 桥接count 查询
     *
     * @param funS
     * @param params
     * @return
     */
    @ProviderAdapter(value = ProviderAdapter.Type.COUNT)
    long adapterCount(@Param(MapperParam.MAPPER_FUNCTION) FunS<?> funS, @Param(MapperParam.PARAMS) Object... params);

    /**
     * 桥接count 查询
     *
     * @param funS
     * @param params
     * @param <T>
     * @return
     */
    @ProviderAdapter(value = ProviderAdapter.Type.PAGE)
    <T> List<T> adapterPage(@Param(MapperParam.MAPPER_FUNCTION) FunS<?> funS, @Param(MapperParam.PARAMS) Object... params);

}
