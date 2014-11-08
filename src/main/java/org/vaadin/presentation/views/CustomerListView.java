package org.vaadin.presentation.views;

import com.vaadin.cdi.CDIView;
import com.vaadin.event.FieldEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import org.vaadin.backend.CustomerService;
import org.vaadin.backend.domain.Customer;
import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.maddon.button.MButton;
import org.vaadin.maddon.fields.MTable;
import org.vaadin.maddon.fields.MValueChangeEvent;
import org.vaadin.maddon.fields.MValueChangeListener;
import org.vaadin.maddon.label.Header;
import org.vaadin.maddon.layouts.MHorizontalLayout;
import org.vaadin.maddon.layouts.MVerticalLayout;
import org.vaadin.presentation.AppUI;
import org.vaadin.presentation.views.CustomerEvent.Type;
import org.vaadin.presentation.ScreenSize;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;

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
    MTable<Customer> bookTable = new MTable(Customer.class).withFullWidth().
            withFullHeight();

    MHorizontalLayout mainContent = new MHorizontalLayout(bookTable).
            withFullWidth().withMargin(false);

    TextField filter = new TextField();

    Header header = new Header("Customers").setHeaderLevel(2);

    Button addButton = new MButton(FontAwesome.EDIT,
            new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    addBook();
                }
            });

    @PostConstruct
    public void init() {

        /*
         * Add value change listener to table that opens the selected book into
         * an editor.
         */
        bookTable.addMValueChangeListener(new MValueChangeListener<Customer>() {

            @Override
            public void valueChange(MValueChangeEvent<Customer> event) {
                editBook(event.getValue());
            }
        });

        /*
         * Configure the filter input and hook to text change events to
         * repopulate the table based on given filter. Text change
         * events are sent to the server when e.g. user holds a tiny pause
         * while typing or hits enter.
         * */
        filter.setInputPrompt("Filter customers...");
        filter.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent textChangeEvent) {
                listBook(textChangeEvent.getText());
            }
        });


        /* "Responsive Web Design" can be done with plain Java as well. Here we
         * e.g. do selective layouting and configure visible columns in
         * table based on available width */
        layout();
        adjustTableColumns();
        /* If you wish the UI to adapt on window resize/page orientation
         * change, hook to BrowserWindowResizeEvent */
        UI.getCurrent().setResizeLazy(true);
        Page.getCurrent().addBrowserWindowResizeListener(
                new Page.BrowserWindowResizeListener() {
                    @Override
                    public void browserWindowResized(
                            Page.BrowserWindowResizeEvent browserWindowResizeEvent) {
                                adjustTableColumns();
                                layout();
                            }
                });

        listBooks();
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
            bookTable.setVisibleColumns("firstName", "lastName", "email",
                    "status");
            bookTable.setColumnHeaders("First name", "Last name", "Email",
                    "Status");
        } else {
            // Only show one (generated) column with combined first + last name
            if (bookTable.getColumnGenerator("name") == null) {
                bookTable.addGeneratedColumn("name",
                        new Table.ColumnGenerator() {
                            @Override
                            public Object generateCell(Table table, Object o,
                                    Object o2) {
                                Customer c = (Customer) o;
                                return c.getFirstName() + " " + c.getLastName();
                            }
                        });
            }
            if (ScreenSize.getScreenSize() == ScreenSize.MEDIUM) {
                bookTable.setVisibleColumns("name", "email");
                bookTable.setColumnHeaders("Name", "Email");
            } else {
                bookTable.setVisibleColumns("name");
                bookTable.setColumnHeaders("Name");
            }
        }
    }

    /* ******* */
    // Controller methods.
    //
    // In a big project, consider using separate controller/presenter
    // for improved testability. MVP is a popular pattern for large
    // Vaadin applications.
    private void listBooks() {
        // Here we just fetch data straight from the EJB.
        //
        // If you expect a huge amount of data, do proper paging,
        // or use lazy loading Vaadin Container like LazyQueryContainer
        // See: https://vaadin.com/directory#addon/lazy-query-container:vaadin
        bookTable.setBeans(new ArrayList<>(service.findAll()));
    }

    private void listBook(String filterString) {
        bookTable.setBeans(new ArrayList<>(service.findByName(filterString)));
    }

    void editBook(Customer book) {
        if (book != null) {
            openEditor(book);
        } else {
            closeEditor();
        }
    }

    void addBook() {
        openEditor(new Customer());
    }

    private void openEditor(Customer customer) {
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
    void saveBook(@Observes @CustomerEvent(Type.SAVE) Customer book) {
        listBooks();
        closeEditor();
    }

    void resetBook(@Observes @CustomerEvent(Type.REFRESH) Customer book) {
        listBooks();
        closeEditor();
    }

    void deleteBook(@Observes @CustomerEvent(Type.DELETE) Customer book) {
        closeEditor();
        listBooks();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

}
