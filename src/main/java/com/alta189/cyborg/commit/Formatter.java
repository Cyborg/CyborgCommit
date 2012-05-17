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

import com.alta189.cyborg.Cyborg;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.yaml.snakeyaml.Yaml;

public class Formatter {
	private List<Commit> commits = new ArrayList<Commit>();
	private Repository repo = null;

	@SuppressWarnings("unchecked")
	public void load(String payload) {
		Yaml yaml = new Yaml();
		Object obj = yaml.load(payload);
		if (obj instanceof String) {
			System.out.println(obj);
		} else {
			HashMap<String, Object> map = (HashMap<String, Object>) obj; // Load entire payload

			// Load the Repository \\
			HashMap<String, Object> repo = (HashMap<String, Object>) map.get("repository");

			this.repo = new Repository(repo);
			this.repo.setBranch(((String) map.get("ref")).replaceAll("refs/heads/", ""));

			// Load Commits \\
			ArrayList<Object> commits = (ArrayList<Object>) map.get("commits");
			for (Object commitObj : commits) {
				HashMap<String, Object> commitMap = (HashMap<String, Object>) commitObj;
				Commit commit = new Commit(commitMap);
				if (CommitManager.isUnique(commit.getId())) {
					CommitManager.addCommit(commit.getId());
					this.commits.add(commit);
				}
			}
		}
	}

	public void send(Channel channel) {
		if (repo != null && commits.size() > 0) {
			for (Commit commit : commits) {
				Cyborg.getInstance().sendMessage(channel, "\u000306" + Colors.BOLD + repo.getName() + Colors.NORMAL + ": " + "\u000303" + commit.getAuthor().getName() + " " + Colors.MAGENTA + repo.getBranch() + Colors.NORMAL + " - " + Colors.BLUE + commit.getShortURL());
				Cyborg.getInstance().sendMessage(channel, "    Files Added: " + Colors.RED + commit.getAdded().size() + Colors.NORMAL + "   Files Removed: " + Colors.RED + commit.getRemoved().size() + Colors.NORMAL + "   Files Modified: " + Colors.RED + commit.getModified().size());
				for (String msg : commit.getMessage().split("\n")) {
					if (msg == null || msg.isEmpty()) {
						continue;
					} else if (msg.startsWith("Signed-off-by:") && !CyborgCommit.getConfig().getBoolean("signoff")) {
						continue;
					}
					Cyborg.getInstance().sendMessage(channel, Colors.BLUE + "    - '" + Colors.NORMAL + msg + Colors.BLUE + "'");
				}
			}
		}
	}

	public Repository getRepo() {
		return this.repo;
	}

	public List<Commit> getCommits() {
		return commits;
	}
}
