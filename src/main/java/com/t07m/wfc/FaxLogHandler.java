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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.wfc.config.FaxLog;

import net.cubespace.Yamler.Config.InvalidConfigurationException;

public class FaxLogHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(FaxLogHandler.class);

	private FaxLog log;

	public FaxLogHandler() {
		init();
	}

	private void init() {
		log = new FaxLog();
		try {
			log.init();
			log.save();
		} catch (InvalidConfigurationException e) {
			try {
				Files.delete(Path.of("FaxLog.yml"));
			} catch (IOException e1) {}			
			try {
				log.init();
			} catch (InvalidConfigurationException e1) {
				e1.printStackTrace();
				logger.error("Unable to rectify FaxLog. Log has been disabled!");
				log = null;
			}
		}
	}

	public boolean contains(String tiff) {
		if(log != null) {
			synchronized(log) {
				for(String l : log.getFaxLog()) {
					if(l.equals(tiff))
						return true;
				}
			}
		}
		return false;
	}

	public void add(FaxJob job) {
		if(log != null) {
			synchronized(log){
				ArrayList<String> list = (ArrayList<String>) Arrays.asList(log.getFaxLog());
				if(!list.contains(job.getTiff())) {
					list.add(job.getTiff());
					log.setFaxLog(list.toArray(new String[list.size()]));
					try {
						log.save();
					} catch (InvalidConfigurationException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void remove(FaxJob job) {
		if(log != null) {
			synchronized(log) {
				ArrayList<String> list = (ArrayList<String>) Arrays.asList(log.getFaxLog());
				if(list.contains(job.getTiff())) {
					list.remove(job.getTiff());
					log.setFaxLog(list.toArray(new String[list.size()]));
					try {
						log.save();
					} catch (InvalidConfigurationException e) {
						e.printStackTrace();
					}
				}	
			}
		}
	}

}
