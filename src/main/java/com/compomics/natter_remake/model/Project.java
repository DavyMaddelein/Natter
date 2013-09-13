package com.compomics.natter_remake.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Davy
 */
public class Project {

    private int projectId;
    private String projectName;
    private List<LcRun> lcruns = new ArrayList<LcRun>();

    public Project(int projectId, String projectName) {
        this.projectId = projectId;
        this.projectName = projectName;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    @Override
    public String toString() {
        return projectId + " - " + projectName;
    }

    public void addLcRun(LcRun lcRun) {
        lcruns.add(lcRun);
    }

    public List<LcRun> getLcRuns() {
        return Collections.unmodifiableList(lcruns);
    }
}
