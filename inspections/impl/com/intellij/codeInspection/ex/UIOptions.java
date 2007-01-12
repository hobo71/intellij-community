/*
 * Copyright (c) 2000-2006 JetBrains s.r.o. All Rights Reserved.
 */

package com.intellij.codeInspection.ex;

import com.intellij.codeInspection.InspectionsBundle;
import com.intellij.codeInspection.ui.InspectionResultsView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.util.*;
import com.intellij.ui.AutoScrollToSourceHandler;
import com.intellij.util.Icons;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;

/**
 * User: anna
 * Date: 28-Feb-2006
 */
public class UIOptions implements JDOMExternalizable {
  public boolean AUTOSCROLL_TO_SOURCE = false;
  public float SPLITTER_PROPORTION = 0.5f;
  public boolean GROUP_BY_SEVERITY = false;
  public boolean FILTER_RESOLVED_ITEMS = true;
  public boolean ANALYZE_TEST_SOURCES = true;
  public boolean SHOW_DIFF_WITH_PREVIOUS_RUN = false;
  public int SCOPE_TYPE = 1;
  public String CUSTOM_SCOPE_NAME = "";
  public final AutoScrollToSourceHandler myAutoScrollToSourceHandler;
  public boolean SHOW_ONLY_DIFF = false;
  public boolean SHOW_STRUCTURE = true;

  public UIOptions() {
    myAutoScrollToSourceHandler = new AutoScrollToSourceHandler() {
      protected boolean isAutoScrollMode() {
        return AUTOSCROLL_TO_SOURCE;
      }

      protected void setAutoScrollMode(boolean state) {
        AUTOSCROLL_TO_SOURCE = state;
      }
    };

  }

  public UIOptions copy() {
    final UIOptions result = new UIOptions();
    @NonNls Element temp = new Element("temp");
    try {
      writeExternal(temp);
      result.readExternal(temp);
    }
    catch (Exception e) {
      //can't be
    }
    return result;
  }

  public void save(UIOptions options) {
    @NonNls Element temp = new Element("temp");
    try {
      options.writeExternal(temp);
      readExternal(temp);
    }
    catch (Exception e) {
      //can't be
    }
  }

  public void readExternal(Element element) throws InvalidDataException {
    DefaultJDOMExternalizer.readExternal(this, element);
  }

  public void writeExternal(Element element) throws WriteExternalException {
    DefaultJDOMExternalizer.writeExternal(this, element);
  }

  public AnAction createGroupBySeverityAction(final InspectionResultsView view) {
    return new ToggleAction(InspectionsBundle.message("inspection.action.group.by.severity"),
                            InspectionsBundle.message("inspection.action.group.by.severity.description"),
                            IconLoader.getIcon("/nodes/sortBySeverity.png")) {


      public boolean isSelected(AnActionEvent e) {
        return GROUP_BY_SEVERITY;
      }

      public void setSelected(AnActionEvent e, boolean state) {
        GROUP_BY_SEVERITY = state;
        view.update();
      }
    };
  }

  public AnAction createFilterResolvedItemsAction(final InspectionResultsView view){
    return new ToggleAction(InspectionsBundle.message("inspection.filter.resolved.action.text"),
                            InspectionsBundle.message("inspection.filter.resolved.action.text"),
                            IconLoader.getIcon("/ant/filter.png")) {


      public boolean isSelected(AnActionEvent e) {
        return FILTER_RESOLVED_ITEMS;
      }

      public void setSelected(AnActionEvent e, boolean state) {
        FILTER_RESOLVED_ITEMS = state;
        view.update();
      }
    };
  }

  public AnAction createShowOutdatedProblemsAction(final InspectionResultsView view) {
    return new ToggleAction(InspectionsBundle.message("inspection.filter.show.diff.action.text"),
                            InspectionsBundle.message("inspection.filter.show.diff.action.text"),
                            IconLoader.getIcon("/actions/diff.png")) {


      public boolean isSelected(AnActionEvent e) {
        return SHOW_DIFF_WITH_PREVIOUS_RUN;
      }

      public void setSelected(AnActionEvent e, boolean state) {
        SHOW_DIFF_WITH_PREVIOUS_RUN = state;
        if (!SHOW_DIFF_WITH_PREVIOUS_RUN) {
          SHOW_ONLY_DIFF = false;
        }
        view.update();
      }
    };
  }

  public AnAction createGroupByDirectoryAction(final InspectionResultsView view) {
    return new ToggleAction("Group by directory",
                            "Group by directory",
                            Icons.DIRECTORY_CLOSED_ICON) {

      public boolean isSelected(AnActionEvent e) {
        return SHOW_STRUCTURE;
      }

      public void setSelected(AnActionEvent e, boolean state) {
        SHOW_STRUCTURE = state;
        view.update();
      }
    };
  }

  public AnAction createShowDiffOnlyAction(final InspectionResultsView view) {
    return new ToggleAction(InspectionsBundle.message("inspection.filter.show.diff.only.action.text"),
                            InspectionsBundle.message("inspection.filter.show.diff.only.action.text"),
                            IconLoader.getIcon("/actions/showChangesOnly.png")) {


      public boolean isSelected(AnActionEvent e) {
        return SHOW_ONLY_DIFF;
      }

      public void setSelected(AnActionEvent e, boolean state) {
        SHOW_ONLY_DIFF = state;
        view.update();
      }

      public void update(final AnActionEvent e) {
        super.update(e);
        e.getPresentation().setEnabled(SHOW_DIFF_WITH_PREVIOUS_RUN);
      }
    };
  }
}
