/*
 * Copyright (C) 2012 CyborgDev <cyborg@alta189.com>
 *
 * This file is part of CyborgCommit
 *
 * CyborgCommit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CyborgCommit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alta189.cyborg.commit;

import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;

import java.util.ArrayList;
import java.util.List;

@Table("commitchannel")
public class CommitChannel {

	@Id
	int id;

	@Field
	private String channel;

	@Field
	private String projects;

	// Non-persistent data
	private final List<String> projectsList = new ArrayList<String>();

	public int getId() {
		return id;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public List<String> getProjects() {
		return projectsList;
	}

	public void addProject(String project) {
		projectsList.add(project.toLowerCase());
	}

	public void removeProject(String project) {
		projectsList.remove(project.toLowerCase());
	}

	public void load() {
		if (projects != null) {
			for (String project : projects.split("; ")) {
				if (project != null && !project.isEmpty())
					projectsList.add(project);
			}
		}
	}

	public void save() {
		if (projectsList.size() > 0) {
			StringBuilder builder = new StringBuilder();
			for (String project : projectsList) {
				builder.append(project).append("; ");
			}
			projects = builder.toString();
		} else {
			projects = null;
		}
	}
}
