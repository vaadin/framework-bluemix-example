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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LOpenStreetMapLayer;
import org.vaadin.addon.leaflet.LeafletClickEvent;
import org.vaadin.addon.leaflet.LeafletClickListener;
import org.vaadin.addon.leaflet.control.LZoom;
import org.vaadin.addon.leaflet.shared.ControlPosition;
import org.vaadin.backend.CustomerService;
import org.vaadin.backend.domain.Customer;
import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Notification;

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
