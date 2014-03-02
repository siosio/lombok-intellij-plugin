package de.plushnikov.intellij.plugin.extension.key;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

public class SimpleProcessorKey extends ProcessorKey {
  private final String name;
  private final String fqName;

  public SimpleProcessorKey(@NotNull String qualifiedName) {
    this.name = StringUtil.getShortName(qualifiedName);
    this.fqName = qualifiedName;
  }

  public String getName() {
    return name;
  }

  @Override
  protected String getQualifiedName() {
    return fqName;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("SimpleProcessorKey{");
    sb.append("name='").append(getName()).append('\'');
    sb.append(", fqName='").append(getQualifiedName()).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
