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

import com.alta189.cyborg.api.command.CommandContext;
import com.alta189.cyborg.api.command.CommandResult;
import com.alta189.cyborg.api.command.CommandSource;
import com.alta189.cyborg.api.command.ReturnType;
import com.alta189.cyborg.api.command.annotation.Command;
import com.alta189.cyborg.api.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.alta189.cyborg.api.command.CommandResultUtil.get;
import static com.alta189.cyborg.commit.CyborgCommit.getDatabase;
import static com.alta189.cyborg.perms.PermissionManager.hasPerm;

public class CommitCommands {
	private static Pattern splitPattern = Pattern.compile("\\w+|\"[\\w\\s]*\"");

	@Command(name = "cadd", desc = "Removes a project from a channel's report list", aliases = {"commitadd", "subscribe", "sub"})
	public CommandResult subscribe(CommandSource source, CommandContext context) {
		if (source.getSource() != CommandSource.Source.USER || context.getLocationType() != CommandContext.LocationType.CHANNEL) {
			return get(ReturnType.NOTICE, "This command has to be done in a channel!", source, context);
		}
		if (context.getPrefix() == null || !context.getPrefix().equals(".")) {
			return null;
		}
		if (source.getSource() == CommandSource.Source.USER && !hasPerm(source.getUser(), "commit.sub")) {
			return get(ReturnType.NOTICE, "You don't have permission!", source, context);
		}
		if (context.getArgs() == null || context.getArgs().length < 1) {
			return get(ReturnType.NOTICE, "Correct usage is .cadd project...", source, context);
		}

		String project = StringUtils.toString(context.getArgs());
		CommitChannel channel = getDatabase().select(CommitChannel.class).where().equal("channel", context.getLocation().toLowerCase()).execute().findOne();
		if (channel == null) {
			channel = new CommitChannel();
			channel.setChannel(context.getLocation().toLowerCase());
		}

		channel.load();

		if (channel.getProjects().contains(project.toLowerCase())) {
			return get(ReturnType.MESSAGE, "This channel already subscribes to '" + project + "'", source, context);
		}

		channel.addProject(project.toLowerCase());
		channel.save();
		getDatabase().save(CommitChannel.class, channel);

		return get(ReturnType.MESSAGE, "Subscribed to project '" + project + "'", source, context);
	}

	@Command(name = "crem", desc = "Removes a project from a channel's report list", aliases = {"commitrem", "unsubscribe", "unsub"})
	public CommandResult unsubscribe(CommandSource source, CommandContext context) {
		if (source.getSource() != CommandSource.Source.USER || context.getLocationType() != CommandContext.LocationType.CHANNEL) {
			return get(ReturnType.NOTICE, "This command has to be done in a channel!", source, context);
		}
		if (context.getPrefix() == null || !context.getPrefix().equals(".")) {
			return null;
		}
		if (source.getSource() == CommandSource.Source.USER && !hasPerm(source.getUser(), "commit.unsub")) {
			return get(ReturnType.NOTICE, "You don't have permission!", source, context);
		}
		if (context.getArgs() == null || context.getArgs().length < 1) {
			return get(ReturnType.NOTICE, "Correct usage is .crem project...", source, context);
		}
		String project = StringUtils.toString(context.getArgs());
		CommitChannel channel = getDatabase().select(CommitChannel.class).where().equal("channel", context.getLocation().toLowerCase()).execute().findOne();
		if (channel == null) {
			channel = new CommitChannel();
		}

		channel.load();

		if (!channel.getProjects().contains(project.toLowerCase())) {
			return get(ReturnType.MESSAGE, "This channel is not subscribed '" + project + "'", source, context);
		}

		channel.removeProject(project.toLowerCase());
		channel.save();
		getDatabase().save(CommitChannel.class, channel);

		return get(ReturnType.MESSAGE, "Unsubscribed from project '" + project + "'", source, context);
	}

	@Command(name = "setauthor", desc = "Sets an author's display name", aliases = {"addauthor"})
	public CommandResult setauthor(CommandSource source, CommandContext context) {
		if (source.getSource() != CommandSource.Source.USER || context.getLocationType() != CommandContext.LocationType.CHANNEL) {
			return get(ReturnType.NOTICE, "This command has to be done in channel!", source, context);
		}
		if (context.getPrefix() == null || !context.getPrefix().equals(".")) {
			return null;
		}
		if (source.getSource() == CommandSource.Source.USER && !hasPerm(source.getUser(), "commit.author")) {
			return get(ReturnType.NOTICE, "You don't have permission!", source, context);
		}

		if (context.getArgs() == null) {
			return get(ReturnType.NOTICE, "Correct usage is .setauthor \"author name\" \"author nane\"", source, context);
		}
		Matcher matcher = splitPattern.matcher(StringUtils.toString(context.getArgs()));
		String name = null;
		String formattedName = null;
		while (matcher.find()) {
			if (name == null) {
				name = matcher.group();
				name = name.substring(1, name.length() - 1);
			} else if (formattedName == null) {
				formattedName = matcher.group();
				formattedName = formattedName.substring(1, formattedName.length() - 1);
			}
		}

		if (name == null || formattedName == null) {
			return get(ReturnType.NOTICE, "Correct usage is .setauthor \"author name\" \"author nane\"", source, context);
		}

		CommitAuthor author = getDatabase().select(CommitAuthor.class).where().equal("name", name.toLowerCase()).execute().findOne();
		if (author == null) {
			author = new CommitAuthor();
			author.setName(name.toLowerCase());
		}

		author.setFormattedName(formattedName);
		getDatabase().save(CommitAuthor.class, author);
		return get(ReturnType.MESSAGE, "Set the formatted name of author '" + name + "' to '" + formattedName + "'", source, context);
	}
}
