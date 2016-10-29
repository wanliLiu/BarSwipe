package io.github.laucherish.purezhihud.utils.logger;

/**
 * @author Orhan Obut
 */
public final class Settings {

  private int methodCount = 2;
  private boolean showThreadInfo = true;
  private int methodOffset = 0;
  private boolean smartTag = false;

  /**
   * Determines how logs will printed
   */
  private io.github.laucherish.purezhihud.utils.logger.LogLevel logLevel = io.github.laucherish.purezhihud.utils.logger.LogLevel.FULL;

  public Settings hideThreadInfo() {
    showThreadInfo = false;
    return this;
  }

  public Settings setMethodCount(int methodCount) {
    if (methodCount < 0) {
      methodCount = 0;
    }
    this.methodCount = methodCount;
    return this;
  }

  public Settings setLogLevel(io.github.laucherish.purezhihud.utils.logger.LogLevel logLevel) {
    this.logLevel = logLevel;
    return this;
  }

  public Settings setMethodOffset(int offset) {
    this.methodOffset = offset;
    return this;
  }

  public Settings smartTag(boolean smartTag) {
    this.smartTag = smartTag;
    return this;
  }

  public int getMethodCount() {
    return methodCount;
  }

  public boolean isShowThreadInfo() {
    return showThreadInfo;
  }

  public io.github.laucherish.purezhihud.utils.logger.LogLevel getLogLevel() {
    return logLevel;
  }

  public int getMethodOffset() {
    return methodOffset;
  }

  public boolean isSmartTag() {
    return smartTag;
  }
}
