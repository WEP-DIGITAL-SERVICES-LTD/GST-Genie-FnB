package com.wepindia.pos.views.InwardManagement;

/**
 * Created by SachinV on 12-03-2018.
 */

public class CriteriaBean {

    int _id;
    String name;
    boolean isSelected;

    public CriteriaBean() {
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
