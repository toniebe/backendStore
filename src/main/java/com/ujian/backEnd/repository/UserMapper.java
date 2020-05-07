package com.ujian.backEnd.repository;


import com.ujian.backEnd.model.Chart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from user where username = #{username} and password = #{password}")
    int cekUser(String username,String password);

    @Select("select nama from user where id = #{id}")
    String cariNama(int id);

    @Insert("insert into transaction (nama,item) values (#{nama},#{item})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()",keyProperty = "id",before = false,resultType = Integer.class)
    List<Chart> insert(Chart cart);

    @Select("select * from transaction")
    List<Chart> showAll();






}
