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

import com.vaadin.annotations.Title;
import com.vaadin.cdi.CDIUI;
import com.vaadin.ui.UI;
import org.vaadin.cdiviewmenu.ViewMenuUI;


/**
 * UI class and its init method  is the "main method" for Vaadin apps.
 * But as we are using Vaadin CDI, Navigator and Views, we'll just
 * extend the helper class ViewMenuUI that provides us a top level layout,
 * automatically generated top level navigation and Vaadin Navigator usage.
 * <p>
 * We also configure the theme, host page title and the widgetset used
 * by the application.
 * </p>
 * <p>
 * The real meat of this example is in CustomerView and CustomerForm classes.
 * </p>
 */
@CDIUI("")
@Title("Simple CRM")
public class AppUI extends ViewMenuUI {

    /**
     * @return the currently active UI instance with correct type.
     */
    public static AppUI get() {
        return (AppUI) UI.getCurrent();
    }

}
