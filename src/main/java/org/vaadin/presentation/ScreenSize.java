/*
 * Copyright 2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.vaadin.presentation;

import com.vaadin.server.Page;

public enum ScreenSize {

    SMALL, MEDIUM, LARGE;

    /**
     * A helper method to get the categorized size for the currently active
     * client.
     *
     * @return the screen size category for currently active client
     */
    public static ScreenSize getScreenSize() {
        int width = Page.getCurrent().getBrowserWindowWidth();
        if (width < 600) {
            return SMALL;
        } else if (width > 1050) {
            return LARGE;
        } else {
            return MEDIUM;
        }
    }
}
