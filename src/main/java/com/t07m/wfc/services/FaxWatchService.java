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
package com.t07m.wfc.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.application.Service;
import com.t07m.wfc.WindowsFaxCompanion;

public class FaxWatchService extends Service<WindowsFaxCompanion>{

	private static final Logger logger = LoggerFactory.getLogger(FaxWatchService.class);
	
	public FaxWatchService(WindowsFaxCompanion app) {
		super(app, TimeUnit.SECONDS.toMillis(10));
	}

	public void process() {
		synchronized(this.getApp().getConfig()) {
			logger.debug("Scanning for new .tif files.");
			long lastSent = this.getApp().getConfig().getInternalLastSent();
			try {
				Files.list(Paths.get(getApp().getConfig().getWatchFolder()))
				.filter(path -> (path.toString().toLowerCase().endsWith(".tif") && path.toFile().lastModified() > lastSent))
				.forEach(path -> getApp().getFaxTracker().submitFile(path.toFile(), getApp()));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
