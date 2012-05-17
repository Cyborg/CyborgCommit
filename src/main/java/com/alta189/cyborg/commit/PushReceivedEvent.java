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

import com.alta189.cyborg.api.event.Event;
import com.alta189.cyborg.api.event.HandlerList;
import java.util.Collections;
import java.util.List;

public class PushReceivedEvent extends Event {
	
	public static final HandlerList handlers = new HandlerList();
	private final Repository repository;
	private final List<Commit> commits;
	private final long timestamp;
	
	public PushReceivedEvent(Repository repository, List<Commit> commits) {
		this(repository, commits, System.currentTimeMillis());
	}
	
	public PushReceivedEvent(Repository repository, List<Commit> commits, long timestamp) {
		this.repository = repository;
		this.commits = Collections.unmodifiableList(commits);
		this.timestamp = timestamp;
	}

	public List<Commit> getCommits() {
		return commits;
	}

	public Repository getRepository() {
		return repository;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
