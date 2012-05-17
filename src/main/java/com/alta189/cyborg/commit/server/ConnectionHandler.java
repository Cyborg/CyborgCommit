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

package com.alta189.cyborg.commit.server;

import com.alta189.cyborg.Cyborg;
import com.alta189.cyborg.api.util.CollectionUtil;
import com.alta189.cyborg.commit.CommitChannel;
import com.alta189.cyborg.commit.Formatter;
import com.alta189.cyborg.commit.PushReceivedEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import org.pircbotx.Channel;

import static com.alta189.cyborg.commit.CyborgCommit.getDatabase;

public class ConnectionHandler extends Thread {
	private Socket socket;
	private Server server;

	public ConnectionHandler(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
	}

	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			boolean post = false;
			boolean headerEnd = false;
			String payload = "";
			boolean stop = true;
			while ((stop)) {
				String cnt = reader.readLine();
				System.out.println(cnt);
				if (cnt == null) {
					System.out.println("line is null");
					break;
				}
				if (cnt.startsWith("POST /")) {
					post = true;
				}
				if (cnt.equals("")) {
					System.out.println("Header end");
					headerEnd = true;
				}
				if (post && headerEnd) {
					String args[] = cnt.split("=");
					if (args.length == 2) {
						if (args[0].equals("payload")) {
							payload = URLDecoder.decode(args[1], "UTF-8");
							stop = false;
							System.out.println("Got some payload!");
						}
					}
				}
			}
			if (payload.equals("")) {
				return;
			}

			Formatter formatter = new Formatter();

			formatter.load(payload);

			for (Channel channel : Cyborg.getInstance().getChannels()) {
				CommitChannel chan = getDatabase().select(CommitChannel.class).where().equal("channel", channel.getName().toLowerCase()).execute().findOne();
				if (chan != null) {
					chan.load();
					if (chan.getProjects() != null && chan.getProjects().size() > 0) {
						if (CollectionUtil.reverseContains(chan.getProjects(), formatter.getRepo().getName().toLowerCase())) {
							formatter.send(channel);
						}
					}
				}
			}
			System.out.println("Calling event PushReceivedEvent");
			Cyborg.getInstance().getEventManager().callEvent(new PushReceivedEvent(formatter.getRepo(), formatter.getCommits()));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		server.removeHandler(this);
	}
}
