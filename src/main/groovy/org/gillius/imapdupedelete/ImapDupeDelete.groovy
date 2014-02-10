/*
 * Copyright 2014 Jason Winnebeck
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gillius.imapdupedelete

import javax.mail.*

/**
 * ImapDupeDelete
 *
 * @author Jason Winnebeck
 */
class ImapDupeDelete {
	String protocol
	String host
	String username
	String password

	private Session session = null
	private Store store = null
	private List<Folder> folders = null
	private List<Message> duplicates = null

	public void connect() {
		session = Session.getDefaultInstance(new Properties())
		store = session.getStore(protocol)

		time("Connecting to $username @ $host") {
			store.connect(host, username, password)
		}
	}

	public void listFolders() {
		time("Listing folders") {
			folders = []
			addFolders(store.getPersonalNamespaces())
			println((folders*.fullName as TreeSet).join('\n'))
		}
	}

	private void addFolders(Folder[] folders) {
		folders.each(this.&addFolder)
	}

	private void addFolder(Folder folder) {
		folders << folder
		addFolders(folder.list())
	}

	public void deleteDuplicates(String folderName) {
		def folder = store.getFolder(folderName)
		assert folder
		folder.open(Folder.READ_WRITE)

		Message[] messages

		time("Getting messages for $folder.fullName") {
			messages = folder.getMessages()
			println "There are $messages.length messages"
		}

		time("Fetching headers") {
			def fp = new FetchProfile()
			fp.add(FetchProfile.Item.ENVELOPE)
			folder.fetch(messages, fp)
		}

		Set<Envelope> envelopes = new HashSet<>()

		time("Finding duplicates") {
			duplicates = []
			for (Message message : messages) {
				if (!envelopes.add(Envelope.fromMessage(message))) {
					duplicates << message
				}
			}
		}

		boolean cont = true
		boolean expunge = false
		while (cont) {
			println "Out of ${messages.length} messages, found ${duplicates.size()} duplicates"
			println " Enter DELETE to delete the ${duplicates.size()} duplicates"
			println " LIST <N> to list the first N duplicates"
			println " Any other option exits with no modifications"
			print "> "

			//System.console() doesn't work in all of the environments I want to use
			def line = (new BufferedReader(new InputStreamReader(System.in))).readLine().toLowerCase()
			switch(line) {
				case "delete":
					time("Deleting ${duplicates.size()} duplicates") {
						Flags flags = new Flags(Flags.Flag.DELETED)
						for (Message message : duplicates) {
							folder.setFlags(duplicates as Message[], flags, true)
						}
					}
					expunge = true
					cont = false
					break

				case ~/^list \d+$/:
					def num = line[5..-1] as int
					def sortedDuplicates = duplicates.sort {it.receivedDate ?: it.sentDate}
					println sortedDuplicates.take(num).collect(Envelope.&fromMessage).join('\n')
					break

				default:
					cont = false
			}
		}

		time("Closing folder (and expunging deleted messages if deleting)") {
			folder.close(expunge)
		}
	}

	public void close() {
		store?.close()
		session = null
		store = null
		folders = null
		duplicates = null
	}

	private static <T> T time(String what, Closure<T> closure) {
		println what + "..."
		long start = System.currentTimeMillis()
		T ret = closure()
		println what + " took " + (System.currentTimeMillis() - start) + "ms"
		return ret
	}
}
