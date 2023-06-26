package com.example.remotecontrollsystem.ui.view.list.route;

import com.amrdeveloper.treeview.TreeNode;
import com.example.remotecontrollsystem.R;
import com.example.remotecontrollsystem.model.entity.Route;
import com.example.remotecontrollsystem.model.entity.Waypoint;
import com.example.remotecontrollsystem.ui.view.list.waypoint.WaypointTreeNode;

public class RouteTreeNode extends TreeNode {
    public RouteTreeNode(Route route) {
        super(route, R.layout.list_item_route_tree);

        findChildWaypoints(route);
    }

    private void findChildWaypoints(Route route) {
        for (Waypoint waypoint : route.getWaypointList()) {
            WaypointTreeNode waypointTreeNode = new WaypointTreeNode(waypoint);
            addChild(waypointTreeNode);
        }
    }

    public void changeName(String name) {
        Route route = getRoute();
        route.setName(name);

        setRoute(route);
    }

    public void setRoute(Route route) {
        setValue(route);
    }

    public Route getRoute() {
        return (Route) getValue();
    }


}
