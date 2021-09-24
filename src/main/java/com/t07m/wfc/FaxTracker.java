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

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FaxTracker {

	private static final Logger logger = LoggerFactory.getLogger(FaxTracker.class);

	private ArrayList<FaxJob> jobs = new ArrayList<FaxJob>();

	public void submitFile(File file, WindowsFaxCompanion app) {
		logger.debug("Checking file - " + file.getName());
		try {
			file.renameTo(file);
			if(!file.canWrite()) {
				return;
			}
		} catch (Exception e) {
			return;
		}
		for(FaxJob job : jobs) {
			if(job.getTiff().equals(file.getPath()))
				return;
		}
		logger.info("New FaxJob Found - " + file.getName());
		jobs.add(new FaxJob(file.getPath(), app));
	}

	public FaxJob[] getJobs() {
		return jobs.toArray(new FaxJob[jobs.size()]);
	}

	public void removeJob(FaxJob job) {
		jobs.remove(job);
	}
}
