/*
 * Copyright 20011, OpenIAM LLC
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.idm.srvc.synch.dto;

import java.util.List;

/**
 * <Attribute> represents the individual attributes from a synchronization source that is being passed for synchronization
 * @author suneet
 *
 */
public class Attribute {
	private String name;

    private String value;
    private List<String> valueList;
    boolean multiValued = false;


	private String type;
	private int columnNbr;
	
	public Attribute() {

    }

    public Attribute(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}


	public Attribute(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
    public Attribute getCopy() {
       return new Attribute(name, type, value, columnNbr);
    }

  	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Attribute(String name, String type, String value) {
		super();
		this.name = name;
		this.type = type;
		this.value = value;
	}
	public Attribute(String name, String type, String value, int colNbr) {
		super();
		this.name = name;
		this.type = type;
		this.value = value;
		this.columnNbr = colNbr;
	}

    public void populateAttribute(String name, List<String> valueList) {
        if (valueList == null || valueList.size() < 2) {
            value = valueList.get(0);
        }else {
          this.valueList = valueList;
          this.multiValued = true;
        }
        this.name = name;
    }

    public int getColumnNbr() {
		return columnNbr;
	}

	public void setColumnNbr(int columnNbr) {
		this.columnNbr = columnNbr;
	}

    public boolean isMultiValued() {
        return multiValued;
    }

    public void setMultiValued(boolean multiValued) {
        this.multiValued = multiValued;
    }

    public List<String> getValueList() {
        return valueList;
    }

    public void setValueList(List<String> valueList) {
        this.valueList = valueList;
    }

    @Override
    public String toString() {
        return "Attribute{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", valueList=" + valueList +
                ", multiValued=" + multiValued +
                ", type='" + type + '\'' +
                ", columnNbr=" + columnNbr +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attribute)) return false;

        Attribute attribute = (Attribute) o;

        if (columnNbr != attribute.columnNbr) return false;
        if (multiValued != attribute.multiValued) return false;
        if (name != null ? !name.equals(attribute.name) : attribute.name != null) return false;
        if (type != null ? !type.equals(attribute.type) : attribute.type != null) return false;
        if (value != null ? !value.equals(attribute.value) : attribute.value != null) return false;
        if (valueList != null ? !valueList.equals(attribute.valueList) : attribute.valueList != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (valueList != null ? valueList.hashCode() : 0);
        result = 31 * result + (multiValued ? 1 : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + columnNbr;
        return result;
    }
}
