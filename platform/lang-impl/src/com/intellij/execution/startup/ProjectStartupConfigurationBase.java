/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.execution.startup;

import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.AbstractCollection;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author Irina.Chernushina on 8/19/2015.
 */
public class ProjectStartupConfigurationBase implements PersistentStateComponent<ProjectStartupConfigurationBase> {
  @Tag("configurations") @AbstractCollection(surroundWithTag = false)
  private final List<ConfigurationDescriptor> myList;

  protected ProjectStartupConfigurationBase() {
    myList = new ArrayList<ConfigurationDescriptor>();
  }

  @Nullable
  @Override
  public ProjectStartupConfigurationBase getState() {
    return this;
  }

  @Override
  public void loadState(ProjectStartupConfigurationBase state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public void clear() {
    myList.clear();
  }

  @Transient
  public List<ConfigurationDescriptor> getList() {
    return myList;
  }

  public void setList(@NotNull final List<ConfigurationDescriptor> list) {
    myList.clear();
    Collections.sort(list, new ConfigurationDescriptorComparator());
    myList.addAll(list);
  }

  public boolean isEmpty() {
    return myList.isEmpty();
  }

  public void setConfigurations(@NotNull Collection<RunnerAndConfigurationSettings> collection) {
    final List<ConfigurationDescriptor> names =
      ContainerUtil.map(collection, settings -> new ConfigurationDescriptor(settings.getUniqueID(), settings.getName()));
    setList(names);
  }

  public boolean deleteConfiguration(String id) {
    final List<ConfigurationDescriptor> list = getList();
    final Iterator<ConfigurationDescriptor> iterator = list.iterator();
    while (iterator.hasNext()) {
      final ConfigurationDescriptor descriptor = iterator.next();
      if (descriptor.getId().equals(id)) {
        iterator.remove();
        return true;
      }
    }
    return false;
  }

  public boolean rename(String oldId, RunnerAndConfigurationSettings settings) {
    final List<ConfigurationDescriptor> list = getList();
    for (ConfigurationDescriptor descriptor : list) {
      if (descriptor.getId().equals(oldId)) {
        final List<ConfigurationDescriptor> newList =
          new ArrayList<ConfigurationDescriptor>(list);
        newList.remove(descriptor);
        newList.add(new ConfigurationDescriptor(settings.getUniqueID(), settings.getName()));
        setList(newList);
        return true;
      }
    }
    return false;
  }

  @Tag("configuration")
  public static class ConfigurationDescriptor {
    private String myId;
    private String myName;

    public ConfigurationDescriptor() {
    }

    public ConfigurationDescriptor(@NotNull String id, @NotNull String name) {
      myId = id;
      myName = name;
    }

    @Attribute("id")
    public String getId() {
      return myId;
    }

    @Attribute("name")
    public String getName() {
      return myName;
    }

    public void setId(String id) {
      myId = id;
    }

    public void setName(String name) {
      myName = name;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ConfigurationDescriptor that = (ConfigurationDescriptor)o;

      if (!myId.equals(that.myId)) return false;
      if (!myName.equals(that.myName)) return false;

      return true;
    }

    @Override
    public int hashCode() {
      int result = myId.hashCode();
      result = 31 * result + myName.hashCode();
      return result;
    }
  }

  private static class ConfigurationDescriptorComparator implements Comparator<ConfigurationDescriptor> {
    @Override
    public int compare(ConfigurationDescriptor o1,
                       ConfigurationDescriptor o2) {
      return o1.getName().compareToIgnoreCase(o2.getName());
    }
  }
}
