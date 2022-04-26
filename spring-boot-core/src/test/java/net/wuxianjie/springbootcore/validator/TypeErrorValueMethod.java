package net.wuxianjie.springbootcore.validator;

/**
 * @author 吴仙杰
 */
enum TypeErrorValueMethod {

  GOOGLE,
  YOUTUBE;

  String value() {
    throw new RuntimeException("有意为之");
  }
}
