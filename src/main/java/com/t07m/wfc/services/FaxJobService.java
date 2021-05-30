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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.application.Service;
import com.t07m.wfc.FaxJob;
import com.t07m.wfc.WindowsFaxCompanion;

public class FaxJobService extends Service<WindowsFaxCompanion>{

	private static final Logger logger = LoggerFactory.getLogger(FaxJobService.class);
	
	private ExecutorService es = Executors.newFixedThreadPool(2);
	
	public FaxJobService(WindowsFaxCompanion app) {
		super(app, TimeUnit.SECONDS.toMillis(5));
	}
	
	public void process() {
		for(FaxJob job : getApp().getFaxTracker().getJobs()) {
			if(!job.isSubmitted()) {
				logger.debug("Submiting FaxJob");
				es.submit(job);
				job.setSubmitted(true);
				continue;
			}
			if(!job.isRunning() && job.getAttempt() > 0) {
				logger.warn("Unable to delete .tif file. Adding to FaxLog.");
				getApp().getFaxLogHandler().add(job);
				getApp().getFaxTracker().removeJob(job);
			}
		}
	}
	
	public void cleanup() {
		es.shutdown();
	}
}
