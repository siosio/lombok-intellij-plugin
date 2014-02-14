package de.plushnikov.intellij.plugin.extension.key;

public abstract class ProcessorKey {
  protected abstract String getName();

  protected abstract String getQualifiedName();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || !(o instanceof ProcessorKey)) {
      return false;
    }

    ProcessorKey that = (ProcessorKey) o;

    return getQualifiedName().equals(that.getQualifiedName());
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }
}
