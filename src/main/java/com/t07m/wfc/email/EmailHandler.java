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
package com.t07m.wfc.email;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jline.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t07m.wfc.config.WFCConfig;

public class EmailHandler {

	private static final Logger logger = LoggerFactory.getLogger(EmailHandler.class);
	
	private final EmailClient client;
	private final WFCConfig config;

	public EmailHandler(EmailClient client, WFCConfig config) {
		this.client = client;
		this.config = config;
	}

	public boolean emailNewFax(String file, String... recipients) {
		if(file != null && Files.exists(Path.of(file))) {
			try {
				logger.debug("Building New Fax Email");
				MimeMessage message = client.createMessage();
				message.setFrom(new InternetAddress("Apps.T07M@gmail.com", "Windows Fax Companion"));
				for(String recipient : recipients) {
					message.addRecipient(Message.RecipientType.BCC, new InternetAddress(recipient));
				}
				message.setSubject("New Recieved Fax");
				Multipart multipart = new MimeMultipart();
				MimeBodyPart attachmentPart = new MimeBodyPart();
				MimeBodyPart textPart = new MimeBodyPart();
				File f = new File(file);
				attachmentPart.attachFile(f);
				textPart.setContent(config.getEmailTemplate().replace("{CLIENTNAME}", config.getClientName()).replace("{FILENAME}", new File(file).getName()), "text/html");
				multipart.addBodyPart(textPart);
				multipart.addBodyPart(attachmentPart);
				message.setContent(multipart);
				return client.sendMessage(message);
			} catch (MessagingException | IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean emailMessage(String text, String... recipients) {
		try {
			logger.debug("Building New Email");
			MimeMessage message = client.createMessage();
			message.setFrom(new InternetAddress("Apps.T07M@gmail.com", "Windows Fax Companion"));
			for(String recipient : recipients) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			}
			message.setSubject("New Message From " + config.getClientName());
			message.setText(text);
			return client.sendMessage(message);
		} catch (MessagingException | IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
