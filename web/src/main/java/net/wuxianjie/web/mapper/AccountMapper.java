package net.wuxianjie.web.mapper;

import net.wuxianjie.web.domain.Account;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账号数据的SQL映射器
 *
 * @author 吴仙杰
 * @see <a href="https://www.baeldung.com/mybatis">Quick Guide to MyBatis | Baeldung</a>
 */
@Mapper
public interface AccountMapper {

  /**
   * 根据账号名称获取账号信息
   *
   * @param accountName 账号名称，即用户名
   * @return 账号信息
   */
  Account findAccountByUserName(String accountName);
}
