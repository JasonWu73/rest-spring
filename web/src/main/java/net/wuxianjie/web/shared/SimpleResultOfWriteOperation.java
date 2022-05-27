package net.wuxianjie.web.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 写操作（如增、删、改）的简单结果。
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleResultOfWriteOperation {

  /**
   * 提示信息。
   */
  private String message;
}
