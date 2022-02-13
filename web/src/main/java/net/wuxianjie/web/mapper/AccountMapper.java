package net.wuxianjie.web.mapper;

import net.wuxianjie.web.model.Account;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账号数据的SQL映射器
 *
 * @author 吴仙杰
 */
@Mapper
public interface AccountMapper {

  /**
   * 根据账号名称获取账号信息
   *
   * @param accountName 账号名称，即用户名
   * @return 账号信息
   */
  Account getAccount(String accountName);
}
