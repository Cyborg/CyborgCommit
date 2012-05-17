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

import java.util.HashMap;

import static com.alta189.cyborg.commit.CyborgCommit.getDatabase;

public class Author {
	private String name;
	private String email;

	public Author(HashMap<String, Object> author) {
		if (author.containsKey("name")) {
			this.setName((String) author.get("name"));
		}
		if (author.containsKey("email")) {
			this.setEmail((String) author.get("email"));
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		CommitAuthor author = getDatabase().select(CommitAuthor.class).where().equal("name", name.toLowerCase()).execute().findOne();
		if (author != null && author.getFormattedName() != null && !author.getFormattedName().isEmpty())
			name = author.getFormattedName();
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
