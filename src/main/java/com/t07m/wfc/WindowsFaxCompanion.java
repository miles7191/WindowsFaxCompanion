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
package com.t07m.wfc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.application.Application;
import com.t07m.wfc.config.WFCConfig;
import com.t07m.wfc.email.EmailHandler;
import com.t07m.wfc.email.GmailClient;
import com.t07m.wfc.services.FaxJobService;
import com.t07m.wfc.services.FaxWatchService;

import lombok.Getter;
import net.cubespace.Yamler.Config.InvalidConfigurationException;

public class WindowsFaxCompanion extends Application{

	public WindowsFaxCompanion(boolean gui) {
		super(gui, "WindowsFaxCompanion");
	}
	
	public static void main(String[] args) {
		boolean gui = true;
		if(args.length > 0) {
			for(String arg : args) {
				if(arg.equalsIgnoreCase("-nogui")) {
					gui = false;
				}
			}
		}
		new WindowsFaxCompanion(gui).start();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(WindowsFaxCompanion.class);

	private @Getter WFCConfig config;
	
	private @Getter FaxTracker faxTracker;
	private @Getter EmailHandler emailHandler;
	
	public void init() {
		this.config = new WFCConfig();
		try {
			this.config.init();
			this.config.save();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			System.err.println("Unable to load configuration file!");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {}
			System.exit(-1);
		}
		logger.info("Launching Application.");
		faxTracker = new FaxTracker();
		emailHandler = new EmailHandler(new GmailClient(config.getEmailUser(), config.getEmailPassword()), config);
		this.registerService(new FaxWatchService(this));
		this.registerService(new FaxJobService(this));
		logger.info("Application Running...");
	}
	
}
