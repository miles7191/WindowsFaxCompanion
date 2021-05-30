/*
 * Copyright (C) 2021 Matthew Rosato
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.t07m.wfc.config;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.YamlConfig;

public class WFCConfig extends YamlConfig{

	@Comment("Windows Fax And Scan Inbox")
	private @Getter @Setter String WatchFolder = "";
	private @Getter @Setter String WorkFolder = "";
	@Comment("Gmail Username & App password")
	private @Getter @Setter String EmailUser = "";
	private @Getter @Setter String EmailPassword = "";
	@Comment("Recipient Emails & Backup Admin Email")
	private @Getter @Setter String[] RecipientEmails = {};
	private @Getter @Setter String[] AdminEmails = {};
	@Comment("Name used to identify this client")
	private @Getter @Setter String clientName = "";
	private @Getter @Setter String emailTemplate = "<html><head><style>body{background-color:#A8B7BD;margin:0;}h1{background-color:#35546E;color:#D4E6ED;margin:0;padding:10;font-family:Tahoma,sans-serif;}h2{font-size:100px;margin:0;}h3{font-family:Tahoma,sans-serif;}div{padding:20;}</style><h1><center>Windows Fax Companion</center></h1></head><body><div><h2><center>&#x2709;</center></h2><hr><h3><center>New Fax Recieved From<br>{CLIENTNAME}<br>{FILENAME}</center></h3></div></body></html>";
	
	public WFCConfig() {
		CONFIG_HEADER = new String[]{"Windows Fax Companion Configuration Data"};
		CONFIG_FILE = new File("config.yml");
	}
	
}
