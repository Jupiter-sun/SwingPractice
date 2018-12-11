package zwei.ui.mediator;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created on 2018-12-11
 *
 * @author 九条涼果 chunxiang.huang@hypers.com
 */
final class Helper {

  private Helper() {}

  /**
   * 使用反射给类中一定类型的字段设置值
   * Note: 不支持Primitive类型
   *
   * @param hostObj   被设置值的对象
   * @param actualObj 设置成这个值
   * @param bound     被设置的对象的字段
   */
  public static void registerComponent(Object hostObj, Object actualObj, Class<?> bound) {
    if (bound.isPrimitive()) {
      throw new RuntimeException("不支持Primitive类型");
    }

    Collection<Field> assignableFields = getAssignableFields(hostObj.getClass(), bound);
    for (Field field : assignableFields) {
      boolean accessible = field.isAccessible();
      field.setAccessible(true);
      try {
        if (field.get(hostObj) == null) {
          setField(hostObj, actualObj, field);
          return;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      } finally {
        field.setAccessible(accessible);
      }
    }
    throw new RuntimeException("没有合适的可赋值字段");
  }

  /**
   * 使用反射给对象设置值，相当于 {@code hostObj.hostField = actualObj }
   *
   * @param hostObj   被设置值的对象
   * @param actualObj 设置成这个值
   * @param hostField 被设置的对象的字段
   */
  private static void setField(Object hostObj, Object actualObj, Field hostField)
      throws IllegalAccessException {
    hostField.set(hostObj, actualObj);
  }

  /**
   * 获取能够设置到指定类型的字段列表，同时关心{@link Reference}
   *
   * @param targetClazz 被解析的类
   * @param filterBound 按这个类过滤
   * @return 在类中能够设置的字段列表
   */
  @NotNull
  private static Collection<Field> getAssignableFields(Class<?> targetClazz, Class<?> filterBound) {
    Field[] declaredFields = targetClazz.getDeclaredFields();
    Collection<Field> results = new LinkedList<>();
    for (Field field : declaredFields) {
      Reference annotation = field.getAnnotation(Reference.class);
      Class<?> filter = annotation == null ? filterBound : annotation.value();

      if (field.getType().isAssignableFrom(filter)) {
        results.add(field);
      }
    }
    return results;
  }
}
