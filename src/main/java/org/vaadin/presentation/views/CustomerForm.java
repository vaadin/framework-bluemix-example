package org.vaadin.presentation.views;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.backend.CustomerService;
import org.vaadin.backend.domain.Customer;
import org.vaadin.backend.domain.CustomerStatus;
import org.vaadin.backend.domain.Gender;
import org.vaadin.maddon.button.MButton;
import org.vaadin.maddon.fields.MTextField;
import org.vaadin.maddon.fields.TypedSelect;
import org.vaadin.maddon.form.AbstractForm;
import org.vaadin.maddon.label.Header;
import org.vaadin.maddon.layouts.MFormLayout;
import org.vaadin.maddon.layouts.MMarginInfo;
import org.vaadin.maddon.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.inject.Inject;

/**
 * A UI component built to modify Customer entities. The used superclass
 * provides binding to the entity object and e.g. Save/Cancel buttons by
 * default. In larger apps, you'll most likely have your own customized super
 * class for your forms.
 * <p>
 * Note, that the advanced bean binding technology in Vaadin is able to take
 * advantage also from Bean Validation annotations that are used also by e.g.
 * JPA implementation. Check out annotations in Customer objects email field and
 * how they automatically reflect to the configuration of related fields in UI.
 * </p>
 */
public class CustomerForm extends AbstractForm<Customer> {

    @Inject
    CustomerService service;

    // Prepare some basic field components that our bound to entity property
    // by naming convetion, you can also use PropertyId annotation
    TextField firstName = new MTextField("First name").withFullWidth();
    TextField lastName = new MTextField("Last name").withFullWidth();
    DateField birthDate = new DateField("Birth day");
    // Select to another entity, options are populated in the init method
    TypedSelect<CustomerStatus> status = new TypedSelect().
            withCaption("Status");
    OptionGroup gender = new OptionGroup("Gender");
    TextField email = new MTextField("Email").withFullWidth();

    Button deleteButton = new MButton(FontAwesome.TRASH_O);

    @Override
    protected Component createContent() {

        setStyleName(ValoTheme.LAYOUT_CARD);

        return new MVerticalLayout(
                new Header("Edit customer").setHeaderLevel(3),
                new MFormLayout(
                        firstName,
                        lastName,
                        email,
                        birthDate,
                        gender,
                        status
                ).withFullWidth(),
                getToolbar()
        ).withStyleName(ValoTheme.LAYOUT_CARD);
    }

    @PostConstruct
    void init() {
        setEagarValidation(true);
        status.setWidthUndefined();
        status.setOptions(CustomerStatus.values());
        gender.addItems((Object[]) Gender.values());
        gender.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        setSavedHandler(new SavedHandler<Customer>() {

            @Override
            public void onSave(Customer entity) {
                try {
                    // make EJB call to save the entity
                    service.saveOrPersist(entity);
                    // fire save event to let other UI components know about
                    // the change
                    saveEvent.fire(entity);
                } catch (EJBException e) {
                    /*
                     * The Customer object uses optimitic locking with the 
                     * version field. Notify user the editing didn't succeed.
                     */
                    Notification.show("The customer was concurrently edited "
                            + "by someone else. Your changes were discarded.",
                            Notification.Type.ERROR_MESSAGE);
                    refrehsEvent.fire(entity);
                }
            }
        });
        setResetHandler(new ResetHandler<Customer>() {

            @Override
            public void onReset(Customer entity) {
                refrehsEvent.fire(entity);
            }
        });
        deleteButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                service.deleteEntity(getEntity());
                deleteEvent.fire(getEntity());
            }
        });
    }

    @Override
    public HorizontalLayout getToolbar() {
        HorizontalLayout toolbar = super.getToolbar();
        toolbar.setMargin(new MMarginInfo(true, false));
        // Configure the form to show delete button for already persisted
        // entities only
        if (getEntity().isPersisted()) {
            toolbar.setWidth("100%");
            toolbar.addComponent(deleteButton);
            toolbar.setExpandRatio(deleteButton, 1);
            toolbar.setComponentAlignment(deleteButton, Alignment.TOP_RIGHT);
        }
        return toolbar;
    }

    @Override
    protected void adjustResetButtonState() {
        // always enabled in this form
        getResetButton().setEnabled(true);
    }

    /* "CDI interface" to notify decoupled components. Using traditional API to
     * other componets would probably be easier in small apps, but just
     * demonstrating here how all CDI stuff is available for Vaadin apps.
     */
    @Inject
    @CustomerEvent(CustomerEvent.Type.SAVE)
    javax.enterprise.event.Event<Customer> saveEvent;

    @Inject
    @CustomerEvent(CustomerEvent.Type.REFRESH)
    javax.enterprise.event.Event<Customer> refrehsEvent;

    @Inject
    @CustomerEvent(CustomerEvent.Type.DELETE)
    javax.enterprise.event.Event<Customer> deleteEvent;
}
