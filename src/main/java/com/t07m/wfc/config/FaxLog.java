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
import net.cubespace.Yamler.Config.YamlConfig;

public class FaxLog extends YamlConfig{

	private @Getter @Setter String[] faxLog = {};
	
	public FaxLog() {
		CONFIG_HEADER = new String[]{"FaxLog File to track faxes that have been handled but not removed."};
		CONFIG_FILE = new File("FaxLog.yml");
	}
	
}
