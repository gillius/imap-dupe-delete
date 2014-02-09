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

/**
 * Main
 *
 * @author Jason Winnebeck
 */
class Main {
	public static void main(String[] args) {
		if (args.length != 4 && args.length != 5) {
			System.err.println("Usage: <protocol> <host> <username> <password> [folder]")
			System.err.println("If folder is not provided, a list of folders is displayed")
			System.err.println("If a folder is provided, then duplicates will be checked in that folder")
			System.exit(1)
		}

		ImapDupeDelete mail = new ImapDupeDelete(
				protocol: args[0],
				host: args[1],
				username: args[2],
				password: args[3]
		)
		try {
			mail.connect()
			switch (args.length) {
				case 4:
					mail.listFolders()
					break

				case 5:
					mail.deleteDuplicates(args[4])
					break

				default:
					assert false
			}
		} finally {
			mail.close()
		}
	}
}
