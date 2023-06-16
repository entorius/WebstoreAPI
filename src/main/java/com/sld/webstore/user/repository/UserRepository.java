package com.sld.webstore.user.repository;

import com.sld.webstore.user.entity.UserEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserRepository {

    @Insert("""
            INSERT INTO web_store_user (id, name, surname, username, password) values(
            #{userEntity.id,typeHandler=com.sld.webstore.config.mybatis.UuidTypeHandler},
            #{userEntity.name},
            #{userEntity.surname},
            #{userEntity.username},
            #{userEntity.password})
            """)
    void save(@Param("userEntity") UserEntity userEntity);
}
