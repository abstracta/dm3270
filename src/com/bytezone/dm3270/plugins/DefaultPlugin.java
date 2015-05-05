package com.bytezone.dm3270.plugins;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

public abstract class DefaultPlugin implements Plugin
{
  protected String getMD5 (byte[] buffer)
  {
    try
    {
      byte[] digest = MessageDigest.getInstance ("MD5").digest (buffer);
      return DatatypeConverter.printHexBinary (digest);
    }
    catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace ();
    }
    return "";
  }

  protected int countModifiableFields (PluginData screen)
  {
    int count = 0;
    for (ScreenField field : screen.screenFields)
      if (!field.isProtected)
        ++count;
    return count;
  }

  protected List<ScreenField> getModifiableFields (PluginData screen)
  {
    List<ScreenField> fields = new ArrayList<> ();
    for (ScreenField field : screen.screenFields)
      if (!field.isProtected)
        fields.add (field);
    return fields;
  }

  protected List<ScreenField> getProtectedFields (PluginData screen)
  {
    List<ScreenField> fields = new ArrayList<> ();
    for (ScreenField field : screen.screenFields)
      if (field.isProtected)
        fields.add (field);
    return fields;
  }

  protected List<ScreenField> getAlphanumericFields (PluginData screen)
  {
    List<ScreenField> fields = new ArrayList<> ();
    for (ScreenField field : screen.screenFields)
      if (field.isAlpha)
        fields.add (field);
    return fields;
  }

  protected List<ScreenField> getNumericFields (PluginData screen)
  {
    List<ScreenField> fields = new ArrayList<> ();
    for (ScreenField field : screen.screenFields)
      if (!field.isAlpha)
        fields.add (field);
    return fields;
  }
}