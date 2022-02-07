package net.wuxianjie.web.mapper;

import net.wuxianjie.web.model.entity.AccountEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账号表 SQL 映射器
 *
 * @author 吴仙杰
 */
@Mapper
public interface AccountMapper {

  AccountEntity findByName(String accountName);
}
