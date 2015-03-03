package com.bytezone.dm3270.structuredfields;

import com.bytezone.dm3270.display.Screen;

public class Inbound3270DS extends StructuredField
{
  public Inbound3270DS (byte[] buffer, int offset, int length, Screen screen)
  {
    super (buffer, offset, length, screen);

    assert data[0] == StructuredField.INBOUND_3270DS;
    System.out.printf ("Inbound 3270DS%n");     // haven't seen one yet
  }

  @Override
  public void process ()
  {
  }
}