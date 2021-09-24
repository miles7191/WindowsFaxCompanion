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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class FaxJob implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(FaxJob.class);

	private int attemptLimit = 3;
	
	@ToString.Exclude
	private final WindowsFaxCompanion app;

	private @Getter long tiffTime = 0;
	
	private @Getter String tiff;
	private @Getter @Setter String pdf;

	private String name;

	private @Getter @Setter boolean emailed;

	private @Getter  int attempt;

	private @Getter boolean complete;
	private @Getter @Setter boolean submitted;
	private @Getter boolean running;


	public FaxJob(String tiff, WindowsFaxCompanion app) {
		this.tiff = tiff;
		this.app = app;
		File tf = new File(tiff);
		name = tf.getName();
		tiffTime = tf.lastModified();
	}

	public void run() {
		running = true;
		while(running && attempt < attemptLimit) {
			attempt++;
			if(pdf == null)
				convertToPDF();
			if(pdf != null)
				emailed = emailRecipients();
			if(emailed && cleanup()) {
				complete = true;
				running = false;
				logger.info("FaxJob Completed Successfully - " + name);
				return;
			}
			if(pdf != null && !emailed) {
				attemptLimit = 20;
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		logger.error("FaxJob Failed - " + this.toString());
		emailAdmin("FaxJob - " + name + " - status: " + this.toString());
		running = false;
	}

	private boolean convertToPDF() {
		logger.debug("Attempting to convert to PDF " + name);
		if(getTiff() != null && Files.exists(Paths.get(getTiff()))) {
			try {
				String workingPath = app.getConfig().getWorkFolder();
				File tempPDF = Files.createFile(Path.of(workingPath + File.separator + new File(getTiff()).getName().replace(".tif", "") + ".pdf")).toFile();
				tempPDF.deleteOnExit();
				boolean success = Util.TiffToPDF(getTiff(), tempPDF.getPath());
				if(!success) {
					tempPDF.delete();
				}
				setPdf(tempPDF.getPath());
				return success;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean emailRecipients() {
		logger.debug("Attempting to email recipients " + name);
		return app.getEmailHandler().emailNewFax(pdf, app.getConfig().getRecipientEmails());
	}

	private boolean cleanup() {
		logger.debug("Attempting to cleanup " + name);
		try {
			Files.delete(Path.of(pdf));
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private boolean emailAdmin(String message) {
		logger.debug("Attempting to email admin");
		return app.getEmailHandler().emailMessage(message, app.getConfig().getAdminEmails());
	}

}
