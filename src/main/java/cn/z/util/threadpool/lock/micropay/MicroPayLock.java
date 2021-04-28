package cn.z.util.threadpool.lock.micropay;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/***
 * @class MicroPayLock
 * @description 主扫售饭机支付锁
 * @author zch
 * @date 2019/12/10
 * @version V0.0.1.201912101334.01
 * @modfiyDate 201912101334
 * @createDate 201912101334
 * @package com.singbon.util
 */
public class MicroPayLock {
  private final static Logger myLogger = Logger.getRootLogger();
  public final static Map<Integer, Integer> LOCK = new ConcurrentHashMap<>();
  /**
   * 主扫设备线程逻辑流程锁 同一台设备只能新建一个处理流程线程
   *
   * @param deviceNum cardSn
   * @param type      方法 1方法  2清楚
   * @return boolean
   */
  public synchronized static boolean getLocker(Integer deviceNum, Integer type) {
    try {
      if (type == 1) {
        if (LOCK.isEmpty()) {
          LOCK.put(deviceNum, deviceNum);
          return true;
        }
        else {
          if (LOCK.containsKey(deviceNum)) {
            return false;
          }
          else {
            LOCK.put(deviceNum, deviceNum);
            return true;
          }
        }
      }
      else {
        LOCK.remove(deviceNum);
        return true;
      }
    }
    catch (Exception e) {
      myLogger.error(e.getMessage(), e);
      return false;
    }
  }
}
