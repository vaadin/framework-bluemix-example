package org.vaadin.presentation.views;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Notification;
import org.vaadin.addon.leaflet.*;
import org.vaadin.addon.leaflet.control.LZoom;
import org.vaadin.addon.leaflet.shared.ControlPosition;
import org.vaadin.backend.CustomerService;
import org.vaadin.backend.domain.Customer;
import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.maddon.label.Header;
import org.vaadin.maddon.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@CDIView("map")
@ViewMenuItem(icon = FontAwesome.GLOBE, order = 1)
public class MapView extends MVerticalLayout implements View {

    @Inject
    CustomerService service;

    LMap worldMap = new LMap();

    @PostConstruct
    void init() {

        add(new Header("Customers on map").setHeaderLevel(2));
        expand(worldMap);
        setMargin(new MarginInfo(false, true, true, true));

        LZoom zoom = new LZoom();
        zoom.setPosition(ControlPosition.topright);
        worldMap.addControl(zoom);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        worldMap.removeAllComponents();
        LOpenStreetMapLayer osm = new LOpenStreetMapLayer();
        osm.setDetectRetina(true);
        worldMap.addComponent(osm);
        for (final Customer customer : service.findAll()) {
            if(customer.getLocation() != null) {
                LMarker marker = new LMarker(customer.getLocation());
                marker.addClickListener(new LeafletClickListener() {
                    @Override
                    public void onClick(LeafletClickEvent event) {
                        Notification.show(
                                "Customer: " + customer.getFirstName() + " " + customer.
                                getLastName());
                    }
                });
                worldMap.addComponent(marker);
           }
        }
        worldMap.zoomToContent();
    }
}
