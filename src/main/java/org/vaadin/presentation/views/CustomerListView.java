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
package org.vaadin.presentation.views;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.vaadin.backend.CustomerService;
import org.vaadin.backend.domain.Customer;
import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.presentation.AppUI;
import org.vaadin.presentation.ScreenSize;
import org.vaadin.presentation.views.CustomerEvent.Type;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

/**
 * A view that lists Customers in a Table and lets user to choose one for
 * editing. There is also RIA features like on the fly filtering.
 */
@CDIView("customers")
@ViewMenuItem(icon = FontAwesome.USERS, order = ViewMenuItem.BEGINNING)
public class CustomerListView extends MVerticalLayout implements View {

    @Inject
    private CustomerService service;

    @Inject
    CustomerForm customerEditor;

    // Introduce and configure some UI components used on this view
    Grid<Customer> customerListing = new Grid(Customer.class);

    MHorizontalLayout mainContent = new MHorizontalLayout().
            withFullWidth().withMargin(false).expand(customerListing);

    TextField filter = new TextField();

    Header header = new Header("Customers").setHeaderLevel(2);

    Button addButton = new MButton(FontAwesome.EDIT,
            new Button.ClickListener() {

        @Override
        public void buttonClick(Button.ClickEvent event) {
            addCustomer();
        }
    });

    @PostConstruct
    public void init() {

        /*
         * Add value change listener to table that opens the selected customer into
         * an editor.
         */
        customerListing.asSingleSelect()
                .addValueChangeListener(
                        e -> {
                            editCustomer(e.getValue());
                        });

        /*
         * Configure the filter input and hook to text change events to
         * repopulate the table based on given filter. Text change
         * events are sent to the server when e.g. user holds a tiny pause
         * while typing or hits enter.
         * */
        filter.setPlaceholder("Filter customers...");
        filter.setValueChangeMode(ValueChangeMode.LAZY);
        filter.addValueChangeListener(e -> {
            listCustomers(e.getValue());
        });


        /* "Responsive Web Design" can be done with plain Java as well. Here we
         * e.g. do selective layouting and configure visible columns in
         * table based on available width */
        layout();
        adjustTableColumns();
        /* If you wish the UI to adapt on window resize/page orientation
         * change, hook to BrowserWindowResizeEvent */
        UI.getCurrent().setResizeLazy(true);
        Page.getCurrent().addBrowserWindowResizeListener(e -> {
            adjustTableColumns();
            layout();
        });

        listCustomers();
    }

    /**
     * Do the application layout that is optimized for the screen size.
     * <p>
     * Like in Java world in general, Vaadin developers can modularize their
     * helpers easily and re-use existing code. E.g. In this method we are using
     * extended versions of Vaadins basic layout that has "fluent API" and this
     * way we get bit more readable code. Check out vaadin.com/directory for a
     * huge amount of helper libraries and custom components. They might be
     * valuable for your productivity.
     * </p>
     */
    private void layout() {
        removeAllComponents();
        if (ScreenSize.getScreenSize() == ScreenSize.LARGE) {
            addComponents(
                    new MHorizontalLayout(header, filter, addButton)
                            .expand(header)
                            .alignAll(Alignment.MIDDLE_LEFT),
                    mainContent
            );

            filter.setSizeUndefined();
        } else {
            addComponents(
                    header,
                    new MHorizontalLayout(filter, addButton)
                            .expand(filter)
                            .alignAll(Alignment.MIDDLE_LEFT),
                    mainContent
            );
        }
        setMargin(new MarginInfo(false, true, true, true));
        expand(mainContent);
    }

    /**
     * Similarly to layouts, we can adapt component configurations based on the
     * client details. Here we configure visible columns so that a sane amount
     * of data is displayed for various devices.
     */
    private void adjustTableColumns() {
        if (ScreenSize.getScreenSize() == ScreenSize.LARGE) {
            customerListing.setColumns("firstName", "lastName", "email",
                    "status");
        } else {
            customerListing.removeAllColumns();
            customerListing.addColumn(c -> c.getFirstName() + " " + c.getLastName())
                    .setCaption("Name");
            if (ScreenSize.getScreenSize() == ScreenSize.MEDIUM) {
                customerListing.addColumn("email");
            }
        }
        // This call shouldn't be needed. A workaround to
        // https://github.com/vaadin/framework/issues/8938
        customerListing.getDataProvider().refreshAll();
    }

    /* ******* */
    // Controller methods.
    //
    // In a big project, consider using separate controller/presenter
    // for improved testability. MVP is a popular pattern for large
    // Vaadin applications.
    private void listCustomers() {
        // Here we just fetch data straight from the EJB.
        //
        // If you expect a huge amount of data, do proper paging,
        // using setDataProvider method, see:
        // https://vaadin.com/blog/-/blogs/lazy-loading-with-vaadin-8
        if (filter.getValue() == null) {
            customerListing.setItems(new ArrayList<>(service.findAll()));
        } else {
            listCustomers(filter.getValue());
        }
    }

    private void listCustomers(String filterString) {
        customerListing.setItems(new ArrayList<>(service.findByName(filterString)));
        // TODO maintain sorting somehow
        //customerTable.sort();
    }

    void editCustomer(Customer customer) {
        if (customer != null) {
            openEditor(customer);
        } else {
            closeEditor();
        }
    }

    void addCustomer() {
        openEditor(new Customer());
    }

    private void openEditor(Customer customer) {
        customerEditor.setWidth("400px");
        customerEditor.setEntity(customer);
        // display next to table on desktop class screens
        if (ScreenSize.getScreenSize() == ScreenSize.LARGE) {
            mainContent.addComponent(customerEditor);
            customerEditor.focusFirst();
        } else {
            // Replace this view with the editor in smaller devices
            AppUI.get().getContentLayout().
                    replaceComponent(this, customerEditor);
        }
        customerEditor.setVisible(true);
    }

    private void closeEditor() {
        // As we display the editor differently in different devices,
        // close properly in each modes
        if (customerEditor.getParent() == mainContent) {
            mainContent.removeComponent(customerEditor);
        } else {
            AppUI.get().getContentLayout().
                    replaceComponent(customerEditor, this);
        }
    }

    /* These methods gets called by the CDI event system, which is also
     * available for Vaadin UIs when using Vaadin CDI add-on. In this
     * example events are arised from CustomerForm. The CDI event system
     * is a great mechanism to decouple components.
     */
    void saveCustomer(@Observes
            @CustomerEvent(Type.SAVE) Customer customer) {
        listCustomers();
        closeEditor();
    }

    void resetCustomer(@Observes
            @CustomerEvent(Type.REFRESH) Customer customer) {
        listCustomers();
        closeEditor();
    }

    void deleteCustomer(@Observes
            @CustomerEvent(Type.DELETE) Customer customer) {
        closeEditor();
        listCustomers();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

}
