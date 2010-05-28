/**
 * Copyright 2009 Joe LaPenna
 */

package org.cogsurv.cogsurver.types;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 */
public class Group<T extends CogSurvType> extends ArrayList<T> implements CogSurvType {

  private static final long serialVersionUID = 1L;

  private String            mType;

  public void setType(String type) {
    mType = type;
  }

  public String getType() {
    return mType;
  }
}
