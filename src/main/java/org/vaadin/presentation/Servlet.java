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

import org.vaadin.viewportservlet.ViewPortCDIServlet;

import javax.servlet.annotation.WebServlet;

/**
 * Normally with Vaadin CDI, the servlet is automatically introduced. If you
 * need to customize stuff in the servlet or host page generation, you can still
 * do that. In this example we use a servlet implementation that adds a viewport
 * meta tag to the host page. It is essential essential for applications that
 * have designed the content to be suitable for smaller screens as well.
 */
@WebServlet(urlPatterns = "/*")
public class Servlet extends ViewPortCDIServlet {

}
