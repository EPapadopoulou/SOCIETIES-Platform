/* Copyright (c) 2009 Google Inc.
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

package org.opensocial.models;

import java.util.List;

/**
 * OpenSocial model class representing the Tags
 * For reference:
 * 
 * http://www.opensocial.org/Technical-Resources/opensocial-spec-v09/REST-API.html#rfc.section.3.6.2
 *
 * @author a google engineer who is not able to indent his own code
 */
public class Tags extends Model {

	public static final String TAGS = "tags";

	/**
	 * Returns the Open Social tags
	 */
	public List<ObjectOpenSocial> getObjects() {
		return getFieldAsList(TAGS);
	}

	/**
	 * @param the attachments to set
	 */
	public void setObjects(List<ObjectOpenSocial> objects) {
		put(TAGS, objects);
	}

}
